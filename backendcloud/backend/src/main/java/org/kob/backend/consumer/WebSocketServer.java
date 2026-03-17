package org.kob.backend.consumer;

import com.alibaba.fastjson.JSONObject;
import org.kob.backend.consumer.utils.Game;
import org.kob.backend.consumer.utils.JwtAuthentication;
import org.kob.backend.mapper.BotMapper;
import org.kob.backend.mapper.RecordMapper;
import org.kob.backend.mapper.UserMapper;
import org.kob.backend.pojo.Bot;
import org.kob.backend.pojo.User;
import org.kob.backend.utils.feign.BotRunningSystemClient;
import org.kob.backend.utils.feign.MatchingSystemClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

@Component
@ServerEndpoint("/websocket/{token}")  // 注意不要以'/'结尾
public class WebSocketServer {

    // 保证线程安全采用 ConcurrentHashMap 和 CopyOnWriteArrayList
    public static final ConcurrentHashMap<Integer, WebSocketServer> users = new ConcurrentHashMap<>();

    private User user;
    private Session session = null;
    public static UserMapper userMapper;

    public static RecordMapper recordMapper;

    private static BotMapper botMapper;

    public Game game = null;


    private static MatchingSystemClient matchingSystemClient;


    private static BotRunningSystemClient botRunningSystemClient;

    @Autowired
    public void setMatchingSystemClient(MatchingSystemClient matchingSystemClient) {
        WebSocketServer.matchingSystemClient = matchingSystemClient;
    }

    @Autowired
    public void setBotRunningSystemClient(BotRunningSystemClient botRunningSystemClient) {
        WebSocketServer.botRunningSystemClient = botRunningSystemClient;
    }
    @Autowired
    public void setUserMapper(UserMapper userMapper) {
        WebSocketServer.userMapper = userMapper;
    }

    @Autowired
    public void setRecordMapper(RecordMapper recordMapper) {
        WebSocketServer.recordMapper = recordMapper;
    }

    @Autowired
    public void setBotMapper(BotMapper botMapper){
        WebSocketServer.botMapper = botMapper;
    }

    @OnOpen
    public void onOpen(Session session, @PathParam("token") String token) throws IOException {

        this.session = session;
        Integer userId = JwtAuthentication.getUserId(token);
        this.user = userMapper.selectById(userId);
        if(user != null){
            users.put(userId, this);
        }else{
            this.session.close();
        }
        System.out.println(users);
    }

    @OnClose
    public void onClose() {
        // 关闭链接
        if(this.user !=null){
            users.remove(this.user.getId());
        }
    }

    public static void startGame(Integer aId, Integer aBotId, Integer bId, Integer bBotId) {
        WebSocketServer server = users.get(aId);
        if (server == null) server = users.get(bId);
        if (server == null) return; // 双方都不在线，直接返回
        server.startGameInternal(aId, aBotId, bId, bBotId);
    }

    public void startGameInternal(Integer aId, Integer aBotId, Integer bId, Integer bBotId){
        User a = userMapper.selectById(aId);
        Bot botA = botMapper.selectById(aBotId);
        User b = userMapper.selectById(bId);
        Bot botB = botMapper.selectById(bBotId);

        Game game = new Game(
                13,
                14,
                20,
                a.getId(),
                botA,
                b.getId(),
                botB,
                botRunningSystemClient
        );
        game.createMap();

        if(users.get(a.getId()) != null)
            users.get(a.getId()).game = game;
        if(users.get(b.getId()) != null)
            users.get(b.getId()).game = game;
        game.start();

        JSONObject respGame = new JSONObject();
        respGame.put("a_id", a.getId());
        respGame.put("a_sx", game.getPlayerA().getSx());
        respGame.put("a_sy", game.getPlayerA().getSy());
        respGame.put("b_id", b.getId());
        respGame.put("b_sx", game.getPlayerB().getSx());
        respGame.put("b_sy", game.getPlayerB().getSy());
        respGame.put("map", game.getG());

        JSONObject respA = new JSONObject();
        respA.put("event", "start-matching");
        respA.put("opponent_username",b.getUsername());
        respA.put("opponent_photo",b.getPhoto());
        respA.put("game", respGame);
        if(users.get(a.getId()) != null)
            users.get(a.getId()).sendMessage(respA.toJSONString());

        JSONObject respB = new JSONObject();
        respB.put("event", "start-matching");
        respB.put("opponent_username",a.getUsername());
        respB.put("opponent_photo",a.getPhoto());
        respB.put("game", respGame);
        if(users.get(b.getId()) != null)
            users.get(b.getId()).sendMessage(respB.toJSONString());
    }

    private void startMatching(Integer botId){
        if (matchingSystemClient == null) {
            System.err.println("matchingSystemClient is null in startMatching, userId=" + (this.user == null ? null : this.user.getId()));
            return;
        }
        matchingSystemClient.addPlayer(this.user.getId(), this.user.getRating(), botId);
    }

    private void stopMatching(){
        if (matchingSystemClient == null) {
            System.err.println("matchingSystemClient is null in stopMatching, userId=" + (this.user == null ? null : this.user.getId()));
            return;
        }
        matchingSystemClient.removePlayer(this.user.getId());
    }


    private void move(Integer direction){
        if(game.getPlayerA().getId().equals(user.getId())){
            if(game.getPlayerA().getBotId() == -1)
                game.setNextStepA(direction);
        }else if(game.getPlayerB().getId().equals(user.getId())){
            if(game.getPlayerB().getBotId() == -1)
                game.setNextStepB(direction);
        }
    }
    @OnMessage
    public void onMessage(String message, Session session) { // 当作路由
        // 从 Client 接收消息
        JSONObject data = JSONObject.parseObject(message);
        String event = data.getString("event");
        if("start-matching".equals(event)){
            startMatching(data.getInteger("bot_id"));
        }else if("stop-matching".equals(event)){
            stopMatching();
        }else if("move".equals(event)){
            move(data.getInteger("direction"));
        }
    }

    @OnError
    public void onError(Session session, Throwable error) {
        error.printStackTrace();
    }

    public void sendMessage(String message){
        synchronized (this.session){
            try {
                this.session.getBasicRemote().sendText(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
