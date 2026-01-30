package org.kob.matchingsystem.Service.impl.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

@Component
public class MatchingPool extends Thread{
    private static List<Player> Players = new ArrayList<>();

    private final ReentrantLock lock = new ReentrantLock();

    private static final String startGameUrl = "http://127.0.0.1:3000/pk/start/game/";

    private static RestTemplate restTemplate;

    @Autowired
    public void setRestTemplate(RestTemplate restTemplate){
        MatchingPool.restTemplate = restTemplate;
    }

    public void addPlayer(Integer userId, Integer rating, Integer BotId) {
        lock.lock();
        try {
            Players.add(new Player(userId, rating, BotId,0));
        } finally {
            lock.unlock();
        }
    }

    public void removePlayer(Integer userId){
        lock.lock();
        try{
            Players.removeIf(player -> player.getUserId().equals(userId));
        }finally{
            lock.unlock();
        }
    }

    private void increasingWaitingTime(){ // 将所有玩家的等待时间加1
        for(Player player: Players){
            player.setWaitingTime(player.getWaitingTime() + 1);
        }
    }

    private boolean checkMatched(Player a, Player b){ // 判断两名玩家是否可以匹配
        int ratingDelta = Math.abs(a.getRating() - b.getRating());
        int waitingTime = Math.min(a.getWaitingTime(),b.getWaitingTime());
        return ratingDelta <= waitingTime * 10;
    }

    private void sendResult(Player a, Player b){ // 返回 a 和 b 的匹配结果
        System.out.println("send result: " + a + " " + b);
        MultiValueMap<String, String> data = new LinkedMultiValueMap<>();
        data.add("a_id",a.getUserId().toString());
        data.add("a_bot_id",a.getBotId().toString());
        data.add("b_id",b.getUserId().toString());
        data.add("b_bot_id",b.getBotId().toString());
        restTemplate.postForEntity(startGameUrl, data, String.class);
    }

    private void matchPlayers(){ //尝试匹配所有玩家
        System.out.println("match players" + Players.toString());
        boolean[] used = new boolean[Players.size()];
        for(int i = 0;i < Players.size();i ++){
            if(used[i]) continue;
            for(int j = i + 1;j < Players.size();j ++){
                if(used[j]) continue;
                Player a = Players.get(i);
                Player b = Players.get(j);
                if(checkMatched(a, b)){
                    used[i] = used[j] = true;
                    sendResult(a, b);
                    break;
                }
            }
        }
        List<Player> newPlayers = new ArrayList<>();
        for(int i = 0; i < Players.size();i ++){
            if(!used[i]){
                newPlayers.add(Players.get(i));
            }
        }
        Players = newPlayers;
    }
    @Override
    public void run() {
        while(true){
            try {
                Thread.sleep(1000);
                lock.lock();
                try {
                    increasingWaitingTime();
                    matchPlayers();
                }finally {
                    lock.unlock();
                }

            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

    }
}
