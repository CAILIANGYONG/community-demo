package com.example.community.dto;

import com.example.community.model.User;
import lombok.Data;

@Data
public class commentDTO {
    private  Long id;
    private  Long parentId;
    private Integer type;
    private  Long commentator;
    private Long gmtCreate;
    private  Long gmtModified;
    private Long likeCount;
    private String content;
    private User user;
}
