package com.example.community.dto;

import lombok.Data;

@Data
public class AccesstokenDTO {       //获取access token
    private String client_id;
    private String client_secret;
    private String code;
    private String redirect_uri;
    private String state;

}
