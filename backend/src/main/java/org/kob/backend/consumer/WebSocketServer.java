package org.kob.backend.consumer;

import com.alibaba.fastjson.JSONObject;
import org.kob.backend.consumer.utils.Game;
import org.kob.backend.consumer.utils.JwtAuthentication;
import org.kob.backend.mapper.RecordMapper;
import org.kob.backend.mapper.UserMapper;
import org.kob.backend.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
@ServerEndpoint("/websocket/{token}")  // 注意不要以'/'结尾
public class WebSocketServer {

    // 保证线程安全采用 ConcurrentHashMap 和 CopyOnWriteArrayList
    public static final ConcurrentHashMap<Integer, WebSocketServer> users = new ConcurrentHashMap<>();
    private static final CopyOnWriteArrayList<User> matchpool = new CopyOnWriteArrayList<>();

    private User user;
    private Session session = null;
    private static UserMapper userMapper;
    public static RecordMapper recordMapper;

    private Game game = null;


    @Autowired
    public void setUserMapper(UserMapper userMapper) {
        WebSocketServer.userMapper = userMapper;
    }

    @Autowired
    public void setRecordMapper(RecordMapper recordMapper) {
        WebSocketServer.recordMapper = recordMapper;
    }

    @OnOpen
    public void onOpen(Session session, @PathParam("token") String token) throws IOException {

        this.session = session;
        System.out.println("connected!");
        Integer userId = JwtAuthentication.getUserId(token); // 假设 token 就是 userId
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
        System.out.println("disconnected!");
        if(this.user !=null){
            users.remove(this.user.getId());
        }
        matchpool.remove(this.user);
    }

    private void startMatching(){
        System.out.println("start matching!");
        matchpool.add(this.user);

        while(matchpool.size() >= 2){
            Iterator<User> it = matchpool.iterator();
            User a = it.next(), b = it.next();
            matchpool.remove(a);
            matchpool.remove(b);

            Game game = new Game(13, 14, 20, a.getId(),b.getId());
            game.createMap();

            users.get(a.getId()).game = game;
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
            users.get(a.getId()).sendMessage(respA.toJSONString());

            JSONObject respB = new JSONObject();
            respB.put("event", "start-matching");
            respB.put("opponent_username",a.getUsername());
            respB.put("opponent_photo",a.getPhoto());
            respB.put("game", respGame);
            users.get(b.getId()).sendMessage(respB.toJSONString());
        }
    }

    private void stopMatching(){
        System.out.println("stop matching!");
        matchpool.remove(this.user);
    }

    private void move(Integer direction){
        if(game.getPlayerA().getId().equals(user.getId())){
            game.setNextStepA(direction);
        }else if(game.getPlayerB().getId().equals(user.getId())){
            game.setNextStepB(direction);
        }
    }
    @OnMessage
    public void onMessage(String message, Session session) { // 当作路由
        // 从 Client 接收消息
        System.out.println("received message!");
        JSONObject data = JSONObject.parseObject(message);
        String event = data.getString("event");
        if("start-matching".equals(event)){
            startMatching();
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
