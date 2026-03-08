package org.kob.backend.service.impl.user.account.acwing;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.kob.backend.mapper.UserMapper;
import org.kob.backend.pojo.User;
import org.kob.backend.service.impl.user.account.acwing.utils.HttpClientUtil;
import org.kob.backend.service.user.account.acwing.WebService;
import org.kob.backend.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

@Service
public class WebServiceImpl implements WebService {

    private static final String appId = "7811";

    private static final String appSecret = "947fc740605b46b89a91ca5f1b462092";

    private static final String redirectUri = "https://app7811.acapp.acwing.com.cn/user/account/acwing/web/receive_code/";

    private static final String applyAccessTokenUrl = "https://www.acwing.com/third_party/api/oauth2/access_token/";

    private static final String getUserInfoUrl = "https://www.acwing.com/third_party/api/meta/identity/getinfo/";

    private static final Random random = new Random();

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private UserMapper userMapper;

    @Override
    public JSONObject applyCode() {
        JSONObject resp = new JSONObject();
        String encodeUrl = URLEncoder.encode(redirectUri, StandardCharsets.UTF_8);
        StringBuilder state = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            state.append((char)(random.nextInt(10) + '0'));
        }
        resp.put("result", "success");
        redisTemplate.opsForValue().set(state.toString(), "true");
        redisTemplate.expire(state.toString(), Duration.ofMinutes(10));  // 10分钟过期
        String applyCodeUrl = "https://www.acwing.com/third_party/api/oauth2/web/authorize/?appid=" + appId + "&redirect_uri=" + encodeUrl + "&scope=userinfo&state=" + state;
        resp.put("apply_code_url", applyCodeUrl);
        return resp;
    }

    @Override
    public JSONObject receiveCode(String code, String state) {
        JSONObject resp = new JSONObject();
        resp.put("result", "failed");
        if(code == null || state == null) return resp;
        if(!redisTemplate.hasKey(state)) return resp;
        redisTemplate.delete(state);
        List<NameValuePair> nameValuePairs = new LinkedList<>();
        nameValuePairs.add(new BasicNameValuePair("appid", appId));
        nameValuePairs.add(new BasicNameValuePair("secret", appSecret));
        nameValuePairs.add(new BasicNameValuePair("code", code));

        String getString = HttpClientUtil.get(applyAccessTokenUrl, nameValuePairs);
        if(getString == null) return resp;
        JSONObject getResp= JSONObject.parseObject(getString);
        String accessToken = getResp.getString("access_token");
        String openid = getResp.getString("openid");

        if(accessToken == null || openid == null) return resp;

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("openid", openid);
        List<User> users = userMapper.selectList(queryWrapper);
        if(!users.isEmpty()){
            User user = users.get(0);
            String jwt = JwtUtil.createJWT(user.getId().toString());

            resp.put("result", "success");
            resp.put("jwt_token", jwt);
            return resp;
        }

        nameValuePairs = new LinkedList<>();
        nameValuePairs.add(new BasicNameValuePair("access_token", accessToken));
        nameValuePairs.add(new BasicNameValuePair("openid", openid));
        getString = HttpClientUtil.get(getUserInfoUrl, nameValuePairs);
        if(getString == null) return resp;
        getResp= JSONObject.parseObject(getString);
        String username = getResp.getString("username");
        String photo = getResp.getString("photo");
        if(username == null || photo == null) return resp;
        for(int i = 0; i < 100; i ++){
            QueryWrapper<User> usernameQueryWrapper = new QueryWrapper<>();
            usernameQueryWrapper.eq("username", username);
            if(userMapper.selectList(usernameQueryWrapper).isEmpty())
                break;
            username += (char)(random.nextInt(10) + '0');
            if(i == 99) return resp;
        }
        User user = new User(
                null,
                username,
                null,
                photo,
                1500,
                openid,
                null
        );
        userMapper.insert(user);
        String jwt = JwtUtil.createJWT(user.getId().toString());
        resp.put("result", "success");
        resp.put("jwt_token", jwt);
        return resp;
    }
}
