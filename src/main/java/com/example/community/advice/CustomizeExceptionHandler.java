package com.example.community.advice;


import com.alibaba.fastjson.JSON;
import com.example.community.dto.ResultDTO;
import com.example.community.exception.CustomizeErrorCode;
import com.example.community.exception.CustomizeException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@ControllerAdvice
public class CustomizeExceptionHandler {

    @ExceptionHandler(Exception.class)   // 可查异常和运行时异常的爸爸
   // @ResponseBody
    ModelAndView handle(Throwable e, Model model,   //爸爸的爸爸
                  HttpServletRequest request,
                  HttpServletResponse response) {

        String contentType = request.getContentType();//判断content type类型  决定页面异常
        if("application/json".equals(contentType)){
            ResultDTO resultDTO ;
            //返回json
            if (e instanceof CustomizeException) { //挑属于这里面的异常
                resultDTO =ResultDTO.errorof((CustomizeException) e);

            } else {
                resultDTO= ResultDTO.errorof(CustomizeErrorCode.SYS_ERROR);//系统错误


            }
            try {
                response.setContentType("application/json");
                response.setStatus(200);
                response.setCharacterEncoding("UTF-8");
                PrintWriter writer = response.getWriter();
                writer.write(JSON.toJSONString(resultDTO));
                writer.close();
            } catch (IOException ioe) {

            }
                return  null;
        }
        else
        {
            //错误页面跳转
            if (e instanceof CustomizeException) {
                model.addAttribute("message", e.getMessage());

            } else {
                model.addAttribute("message", CustomizeErrorCode.SYS_ERROR.getMessage());

            }
            return new ModelAndView("error");
        }


    }
}

