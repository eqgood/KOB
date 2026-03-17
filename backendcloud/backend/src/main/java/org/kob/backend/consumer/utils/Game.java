package org.kob.backend.consumer.utils;

import org.kob.backend.pojo.Bot;
import  org.kob.backend.pojo.Record;
import com.alibaba.fastjson.JSONObject;
import lombok.Getter;
import org.kob.backend.consumer.WebSocketServer;
import org.kob.backend.pojo.User;
import org.kob.backend.utils.feign.BotRunningSystemClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;

public class Game extends Thread{
    private final Integer rows;
    private final Integer cols;
    private final Integer inner_walls_count;

    @Getter
    private int[][] g;
    final private static int[] dx = {-1, 0, 1, 0};
    final private static int[] dy = {0, 1, 0, -1};

    @Getter
    private final Player playerA;
    @Getter
    private final Player playerB;

    private Integer nextStepA = null;
    private Integer nextStepB = null;

    private final ReentrantLock lock = new ReentrantLock();

    private String status = "playing"; // playing => finished

    private String loser = null; // A => A输  B => B输 all => 平局

    private final BotRunningSystemClient botRunningSystemClient;


    public Game(
            Integer rows,
            Integer cols,
            Integer inner_walls_count,
            Integer idA,
            Bot botA,
            Integer idB,
            Bot botB,
            BotRunningSystemClient botRunningSystemClient
    ) {
        this.rows = rows;
        this.cols = cols;
        this.inner_walls_count = inner_walls_count;
        this.g = new int[rows][cols];

        Integer botIdA = -1, botIdB = -1;
        String botCodeA = "", botCodeB = "";
        if(botA != null){
            botIdA = botA.getId();
            botCodeA = botA.getContent();
        }
        if(botB != null){
            botIdB = botB.getId();
            botCodeB = botB.getContent();
        }
        playerA = new Player(idA, botIdA, botCodeA,rows- 2, 1, new ArrayList<>());
        playerB = new Player(idB, botIdB, botCodeB,1, cols - 2, new ArrayList<>());
        this.botRunningSystemClient = botRunningSystemClient;
    }

    private boolean check_connectivity(int sx, int sy, int tx, int ty){
        if(sx == tx && sy == ty) return true;
        g[sx][sy] = 1;

        for(int i = 0; i < 4; i ++){
            int x = sx + dx[i],  y = sy + dy[i];
            if(g[x][y] == 0 && x >= 0 && y >= 0 && y < cols && x < this.rows && this.check_connectivity(x, y, tx, ty)) {
                g[sx][sy] = 0;
                return true;
            }
        }
        g[sx][sy] = 0;
        return false;
    }
    private boolean draw(){
        for(int i = 0;i < this.rows; i++){
            for(int j = 0; j < cols;j ++){
                g[i][j] = 0;
            }
        }
        for(int i = 0;i < this.rows; i++){
            g[i][0] = g[i][this.cols - 1] = 1;
        }
        for(int j = 0;j < cols; j++){
            g[0][j] = g[this.rows - 1][j] = 1;
        }


        // 创建随机障碍物
        Random random = new Random();
        for(int i = 0; i < this.inner_walls_count / 2; i ++){
            for(int j = 0; j < 1000;j ++){
                int  r = random.nextInt(this.rows);
                int  c = random.nextInt(this.cols);
                if(g[r][c] == 1 || g[this.rows - 1 -r][this.cols - 1 - c] == 1) continue;
                if(r == this.rows - 2 && c == 1 || r == 1 && c == this.cols - 2) continue;

                g[r][c] = g[this.rows - 1 - r][this.cols - 1 - c] = 1;
                break;
            }
        }
        return check_connectivity(this.rows - 2, 1, 1, this.cols - 2);
    }

    public void setNextStepA(Integer nextStepA) {
        lock.lock();
        try {
            this.nextStepA = nextStepA;
        } finally {
            lock.unlock();
        }
    }

    public void setNextStepB(Integer nextStepB) {
        lock.lock();
        try {
            this.nextStepB = nextStepB;
        } finally {
            lock.unlock();
        }
    }

    public void createMap(){
        for(int i = 0;i < 1000;i++){
            if(draw())
                break;
        }
    }

    private String getInput(Player player){ // 将当前局面信息编码成字符串
        Player me, you;
        if(playerA.getBotId().equals(player.getBotId())){
            me = playerA; you = playerB;
        }else{
            me = playerB; you = playerA;
        }
        return getMapString() + "#" +
                me.getSx() + "#" +
                me.getSy() + "#(" +
                me.getStepsString() + ")#" +
                you.getSx() + "#" +
                you.getSy() + "#(" +
                you.getStepsString() + ")";
    }

    private void sendBotCode(Player player){
        if(player.getBotId().equals(-1)) return; // 人亲自出马，不需要执行代码
        botRunningSystemClient.addBot(player.getId(), player.getBotCode(), getInput(player));
    }

    private boolean nextStep(){ // 等待两名玩家的下一步操作
        try {
            Thread.sleep(200);  // 每次等待 200 ms，防止用户在 200 ms 内输入多次操作导致前面的操作被覆盖掉，因为蛇的速度是一秒可以走五个格子
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        sendBotCode(playerA);
        sendBotCode(playerB);
        for(int i = 0; i < 50 ;i ++){
            try {
                Thread.sleep(100);
                lock.lock();
                try{
                    if(nextStepA != null && nextStepB != null){
                        playerA.getSteps().add(nextStepA);
                        playerB.getSteps().add(nextStepB);
                        return true;
                    }
                }finally {
                    lock.unlock();
                }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        return false;
    }

    private boolean check_valid(List<Cell> cellsA, List<Cell> cellsB){
        int n = cellsA.size();
        Cell cell = cellsA.get(n - 1);
        if(g[cell.x][cell.y] == 1) return false;
        for(int i = 0;i < n - 1; i++){
            if(cell.x == cellsA.get(i).x && cell.y == cellsA.get(i).y){
                return false;
            }
        }
        for(int i = 0;i < n; i++){
            if(cell.x == cellsB.get(i).x && cell.y == cellsB.get(i).y){
                return false;
            }
        }
        return true;
    }
    private void judge(){ // 判断两名玩家下一步操作是否合法
        List<Cell> cellsA = playerA.getCells();
        List<Cell> cellsB = playerB.getCells();
        boolean validA = check_valid(cellsA, cellsB);
        boolean validB = check_valid(cellsB, cellsA);
        if(!validA || !validB){
            status = "finished";

            if(!validA && !validB) {
                loser = "all";
            } else if(!validA){
                loser = "A";
            } else{
                loser = "B";
            }
        }
    }

    private String getMapString(){
        StringBuilder res = new StringBuilder();
        for(int i = 0; i < rows; i++){
            for(int j = 0; j < cols; j++){
                res.append(g[i][j]);
            }
        }
        return res.toString();
    }

    private void updateRating(Player playerA, Integer rating){
        User user = WebSocketServer.userMapper.selectById(playerA.getId());
        user.setRating(rating);
        WebSocketServer.userMapper.updateById(user);
    }
    private void sendToDatabase(){
        Integer ratingA = WebSocketServer.userMapper.selectById(playerA.getId()).getRating();
        Integer ratingB = WebSocketServer.userMapper.selectById(playerB.getId()).getRating();
        if("A".equals(loser)){
            ratingA -= 2;
            ratingB += 5;
        }else if("B".equals(loser)){
            ratingA += 5;
            ratingB -= 2;
        }

        updateRating(playerA, ratingA);
        updateRating(playerB, ratingB);

        Record record = new Record(
                null,
                playerA.getId(),
                playerA.getSx(),
                playerA.getSy(),
                playerB.getId(),
                playerB.getSx(),
                playerB.getSy(),
                playerA.getStepsString(),
                playerB.getStepsString(),
                getMapString(),
                loser,
                new Date()
        );
        WebSocketServer.recordMapper.insert(record);
    }

    private void sendAllMessage(String Message){ // 向两名 Client 发送信息
        if(WebSocketServer.users.get(playerA.getId()) != null)
            WebSocketServer.users.get(playerA.getId()).sendMessage(Message);
        if(WebSocketServer.users.get(playerB.getId()) != null)
            WebSocketServer.users.get(playerB.getId()).sendMessage(Message);
    }

    private void sendMove(){  // 向两名 Client 传递移动信息
        lock.lock();
        try{
            JSONObject resp = new JSONObject();
            resp.put("event", "move");
            resp.put("a_direction", nextStepA);
            resp.put("b_direction", nextStepB);
            sendAllMessage(resp.toJSONString());
            nextStepA = nextStepB = null;
        }finally{
            lock.unlock();
        }
    }

    private void sendResult(){  // 向两名 Client 发送游戏结果
        JSONObject resp = new JSONObject();
        resp.put("event", "result");
        resp.put("loser", loser);
        sendToDatabase();
        sendAllMessage(resp.toJSONString());
    }

    @Override
    public void run() {
        for(int i = 0; i < 1000; i++){
            if(nextStep()){ // 判断是否获取两条蛇的下一步操作
                judge();
                if(status.equals("playing")){
                    sendMove();
                }else{
                    sendResult();
                    break;
                }
            }else{
                status = "finished";
                lock.lock();
                try{
                    if(nextStepA == null && nextStepB == null){
                        loser = "all";
                    }else if(nextStepA == null){
                        loser = "A";
                    }else{
                        loser = "B";
                    }
                }finally {
                    lock.unlock();
                }
                sendResult();
                break;
            }
        }
    }
}
