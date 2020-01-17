package com.example.community.controller;

import com.example.community.dto.AccesstokenDTO;
import com.example.community.dto.GithubUser;
import com.example.community.provider.GithubProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/*
crea
 */
@Controller
public class AuthorizeController {

    @Autowired
    private GithubProvider githubProvider;
    @Value("${github.clinet.id}")
    private  String clientId;
    @Value("${github.clinet.secret}")
    private String clientSecret;
    @Value("${github.redirect.uri}")
    private String redirectUri;
    @GetMapping("/callback")
    public  String callback(@RequestParam(name="code")String code,
                            @RequestParam(name="state")String state)
    {
        AccesstokenDTO accesstokenDTO = new AccesstokenDTO();
        accesstokenDTO.setClient_id(clientId);
        accesstokenDTO.setClient_secret(clientSecret);
        accesstokenDTO.setCode(code);
        accesstokenDTO.setRedirect_uri(redirectUri);
        accesstokenDTO.setState(state);
        String  accesstoken  = githubProvider.getAccessToken(accesstokenDTO);
        GithubUser user = githubProvider.getUser(accesstoken);
        System.out.println(user.getName());

        return "index";
    }
}
