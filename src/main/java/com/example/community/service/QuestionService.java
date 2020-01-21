package com.example.community.service;

import com.example.community.dto.PaginationDTO;
import com.example.community.dto.QuestionDTO;
import com.example.community.mapper.QuessionMapper;
import com.example.community.mapper.UserMapper;
import com.example.community.model.Question;
import com.example.community.model.User;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class QuestionService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private QuessionMapper quessionMapper;
    public PaginationDTO list(Integer page, Integer size) {
        PaginationDTO paginationDTO = new PaginationDTO();
        Integer totalPage;
        Integer totalCount = quessionMapper.count();//拿到用户总条数
        if(totalCount % size==0){
            totalPage = totalCount/size;
        }
        else
        {
            totalPage=totalCount/size+1;
        }
        if(page < 1){
            page = 1;
        }
        if(page > totalPage){
            page=totalPage;
        }//siez *(page-1)
        paginationDTO.setPagination(totalPage,page);
        Integer offset = size *(page-1);
        List<Question> questions =  quessionMapper.list(offset,size);
        List<QuestionDTO> questionDTOList = new ArrayList<>();



        for (Question question : questions) {
            User user = userMapper.findById(question.getCreator());
            QuestionDTO questionDTO = new QuestionDTO();
            BeanUtils.copyProperties(question,questionDTO);
            questionDTO.setUser(user);
            questionDTOList.add(questionDTO);
        }
        paginationDTO.setQuestions(questionDTOList);

        return  paginationDTO;
    }
    //获取当前用户，提交问题，显示页面
    public PaginationDTO list(Integer userId, Integer page, Integer size) {

        PaginationDTO paginationDTO = new PaginationDTO();
        Integer totalPage;
        Integer totalCount = quessionMapper.countByUserId(userId);
        if(totalCount%size==0){
            totalPage = totalCount/size;
        }
        else
        {
            totalPage=totalCount/size+1;
        }
        if(page < 1){
            page = 1;
        }
        if(page >totalPage){
            page=totalPage;
        }
        paginationDTO.setPagination(totalPage,page);
        //siez *(page-1)

        Integer offset = size *(page-1);
        List<Question> questions =  quessionMapper.listByUserId(userId,offset,size);
        List<QuestionDTO> questionDTOList = new ArrayList<>();



        for (Question question : questions) {
            User user = userMapper.findById(question.getCreator());
            QuestionDTO questionDTO = new QuestionDTO();
            BeanUtils.copyProperties(question,questionDTO);
            questionDTO.setUser(user);
            questionDTOList.add(questionDTO);
        }
        paginationDTO.setQuestions(questionDTOList);



        return  paginationDTO;
    }

    public QuestionDTO getById(Integer id) {

        Question question = quessionMapper.getById(id);
        QuestionDTO questionDTO = new QuestionDTO();
        BeanUtils.copyProperties(question,questionDTO);
        return  questionDTO;
    }

}
