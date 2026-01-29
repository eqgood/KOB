package org.kob.backend.service.impl.user.bot;

import org.kob.backend.mapper.BotMapper;
import org.kob.backend.pojo.Bot;
import org.kob.backend.pojo.User;
import org.kob.backend.service.impl.utils.UserDetailsImpl;
import org.kob.backend.service.user.account.bot.UpdateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class UpdateServiceImpl implements UpdateService {
    @Autowired
    private BotMapper botMapper;
    @Override
    public Map<String, String> update(Map<String, String> data) {
        UsernamePasswordAuthenticationToken authenticationToken =
                (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl loginUser = (UserDetailsImpl) authenticationToken.getPrincipal();

        User user = loginUser.getUser();
        int bot_id = Integer.parseInt(data.get("bot_id"));

        String title = data.get("title");
        String description = data.get("description");
        String content = data.get("content");

        Map<String, String> map = new HashMap<>();
        if(title == null || title.isEmpty()){
            map.put("message","标题不能为空");
            return map;
        }
        if(title.length() > 100){
            map.put("message","标题长度不能大于100");
            return map;
        }
        if (description == null || description.isEmpty()) {
            description = "这个用户很懒，什么也没留下~";
        }
        if(description.length() > 300){
            map.put("message","描述长度不能超过300");
            return map;
        }
        if(content == null || content.isEmpty()){
            map.put("message","代码内容不能为空");
            return map;
        }
        if(content.length() > 10000){
            map.put("message","代码内容长度不能超过10000");
            return map;
        }

        Bot bot = botMapper.selectById(bot_id);
        if(bot == null){
            map.put("message","Bot不存在或已删除");
            return map;
        }
        if(!bot.getUserId().equals(user.getId())){
            map.put("message","没有权限修改该bot");
            return map;
        }
        Date now = new Date();
        Bot new_bot = new Bot(
                bot.getId(),
                user.getId(),
                title,
                description,
                content,
                bot.getCreateTime(),
                now
        );
        botMapper.updateById(new_bot);
        map.put("message","success");

        return map;
    }
}
