package com.example.community.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
/*
 by 2020/1/15
 */
@Controller
public class HelloController {
    @GetMapping("/")
    public String index()
    {
        return "index";
    }
}
