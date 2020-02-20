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

        RequestBody body = RequestBody.create(mediaType,JSON.toJSONString(accesstokenDTO));//直接强转json post出去
        Request request = new Request.Builder()
                .url("https://github.com/login/oauth/access_token")//携带code回调
                .post(body)
                .build();
        try (Response response = client.newCall(request).execute()) {  //切片 数据处理懂了
            String string = response.body().string();
            String[] split=string.split( "&");
            String tokenstr = split[0];
            String token = tokenstr.split( "=")[1];  // 得到token数据的
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
        Request request = new Request.Builder()   //取得请求 模拟http  get   post  请求
                .url("https://api.github.com/user?access_token="+accesstoken)
                .build();
        try {
            Response response = client.newCall(request).execute();
            String string = response.body().string();
            GithubUser githubUser = JSON.parseObject(string, GithubUser.class); //string json对象 自动转换解析成java类对象
            return githubUser;   //和上面有点类似
        } catch (IOException e) {

        }
        return  null;
    }
}
