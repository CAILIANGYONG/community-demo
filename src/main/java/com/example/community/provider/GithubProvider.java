package com.example.community.provider;

import com.alibaba.fastjson.JSON;
import com.example.community.dto.AccesstokenDTO;
import com.example.community.dto.GithubUser;
import okhttp3.*;
import org.springframework.stereotype.Component;

import java.io.IOException;
@Component
public class GithubProvider  {
    public String getAccessToken(AccesstokenDTO accesstokenDTO){
         MediaType mediaType = MediaType.get("application/json; charset=utf-8");

        OkHttpClient client = new OkHttpClient();

        RequestBody body = RequestBody.create(mediaType,JSON.toJSONString(accesstokenDTO));
        Request request = new Request.Builder()
                .url("https://github.com/login/oauth/access_token")
                .post(body)
                .build();
        try (Response response = client.newCall(request).execute()) {
            String string = response.body().string();
            String[] split=string.split( "&");
            String tokenstr = split[0];
            String token = tokenstr.split( "=")[1];
            return  token;
//            return  string;
//            System.out.println(string);
//            return string;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public GithubUser getUser(String  accesstoken){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://api.github.com/user?access_token="+accesstoken)
                .build();
        try {
            Response response = client.newCall(request).execute();
            String string = response.body().string();
            GithubUser githubUser = JSON.parseObject(string, GithubUser.class);
            return githubUser;
        } catch (IOException e) {

        }
        return  null;
    }
}
