package com.example.community.dto;

import lombok.Data;

@Data
public class GithubUser {
    private  String name;
    private  Long id;
    private  String bio;
    private  String avatarUrl;//拉取图片路径
}
