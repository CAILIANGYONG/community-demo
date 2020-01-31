package com.example.community.controller;

import com.example.community.dto.NotificationDTO;
import com.example.community.dto.PaginationDTO;
import com.example.community.model.Notification;
import com.example.community.model.User;
import com.example.community.service.NotificationService;
import com.example.community.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/*

 */
@Controller
public class ProfileController {
    @Autowired
    private QuestionService questionService;
    @Autowired
    private NotificationService notificationService;   //模型放问题和回复者   故而需要提供service

    @GetMapping("/profile/{action}")
    public String profile(HttpServletRequest request,
                          @PathVariable(name="action") String action,
                          Model model,
                          @RequestParam(name = "page",defaultValue = "1") Integer page,
                          @RequestParam(name = "size",defaultValue = "3") Integer size
    ){

        User user =(User) request.getSession().getAttribute("user");
        if(user==null)
            {
                return "redirect:/";
            }
        if("questions".equals(action)){
                model.addAttribute("section","questions");
                model.addAttribute("sectionName","我的提问");
            PaginationDTO paginationDTO = questionService.list(user.getId(), page, size);
            model.addAttribute("pagination",paginationDTO);
        }else if("replies".equals(action)){
            PaginationDTO paginationDTO = notificationService.list(user.getId(),page,size);
            Long unreadCount = notificationService.unreadCount(user.getId());
            model.addAttribute("section","replies");
            model.addAttribute("pagination",paginationDTO);
           // model.addAttribute("unreadCount",unreadCount);    拦截器里已经有了
            model.addAttribute("sectionName","最新回复");
        }

        return "profile";
    }
}
