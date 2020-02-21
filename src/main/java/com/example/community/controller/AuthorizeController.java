package com.example.community.controller;

import com.example.community.dto.AccesstokenDTO;
import com.example.community.dto.GithubUser;
import com.example.community.model.User;
import com.example.community.provider.GithubProvider;
import com.example.community.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

/*
crea
 */
@Controller
@Slf4j   //LOMBOK 的一个注解
public class AuthorizeController {

    @Autowired
    private GithubProvider githubProvider;

    @Value("${github.clinet.id}")
    private String clientId;// 数值注入
    @Value("${github.clinet.secret}")
    private String clientSecret;
    @Value("${github.redirect.uri}")
    private String redirectUri;
    @Autowired
    private UserService userService;

    @GetMapping("/callback")
    public String callback(@RequestParam(name = "code") String code,
                           @RequestParam(name = "state") String state,
                           HttpServletResponse response)//cookie 请求    不需要new  直接从参数列表拿
    {//登录跳转放回  携带code
        AccesstokenDTO accesstokenDTO = new AccesstokenDTO();//创建一个媒介存值
        accesstokenDTO.setClient_id(clientId);
        accesstokenDTO.setClient_secret(clientSecret);
        accesstokenDTO.setCode(code);
        accesstokenDTO.setRedirect_uri(redirectUri);
        accesstokenDTO.setState(state);
        String accesstoken = githubProvider.getAccessToken(accesstokenDTO);  //塞到githubtoken里去
        GithubUser githubUser = githubProvider.getUser(accesstoken);// 返回用户信息
        if (githubUser != null && githubUser.getId() != null) {
            //登录成功，写cookie 与session
            User user = new User();
            String token = UUID.randomUUID().toString();
            user.setToken(token);
            user.setName(githubUser.getName());
            user.setAccountId(String.valueOf(githubUser.getId()));
            user.setBio(githubUser.getBio());
            user.setAvartarUrl(githubUser.getAvatarUrl());
            userService.createOrUpdate(user);
            response.addCookie(new Cookie("token", token));//token放到cookie里(当时持久访问操作) 临时通行证
            // request.getSession().setAttribute("user",githubUser);
            return "redirect:/";  //重定向
        } else {
            log.error("callback get github error,{}", githubUser);
            //登录失败，重新登录
            return "redirect:/";
        }

    }

    @GetMapping("/logout")
    public String logout(
            HttpServletRequest request,//session 请求,
            HttpServletResponse response//cookie 请求
    ) {//清除session  清除 cookie
        request.getSession().removeAttribute("user");
        Cookie cookie = new Cookie("token", null);
        cookie.setMaxAge(0);//覆盖删除法
        response.addCookie(cookie);
        return "redirect:/";
    }

}
