package com.example.community.service;

import com.example.community.dto.PaginationDTO;
import com.example.community.dto.QuestionDTO;
import com.example.community.exception.CustomizeErrorCode;
import com.example.community.exception.CustomizeException;
import com.example.community.mapper.QuessionMapper;
import com.example.community.mapper.QuestionExtMapper;
import com.example.community.mapper.QuestionMapper;
import com.example.community.mapper.UserMapper;
import com.example.community.model.Question;
import com.example.community.model.QuestionExample;
import com.example.community.model.User;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class QuestionService {
        @Autowired
    private QuessionMapper quessionMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private QuestionMapper questionMapper;
    @Autowired
    private QuestionExtMapper questionExtMapper;

    //主页显示
    public PaginationDTO list(String search,Integer page, Integer size) {

        if(StringUtils.isNotBlank(search))
        {
            String[] tags = StringUtils.split(search, " ");
          search = Arrays.stream(tags).collect(Collectors.joining("|"));

        }


        PaginationDTO paginationDTO = new PaginationDTO();
        Integer totalPage;
        Integer totalCount = (int )questionMapper.countByExample(new QuestionExample());//拿到用户总条数
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
        QuestionExample questionExample = new QuestionExample();
        questionExample.setOrderByClause("gmt_create desc");
        List<Question> questions = questionMapper.selectByExampleWithRowbounds(questionExample, new RowBounds(offset, size));
        List<QuestionDTO> questionDTOList = new ArrayList<>();



        for (Question question : questions) {
            User user = userMapper.selectByPrimaryKey(question.getCreator());
            QuestionDTO questionDTO = new QuestionDTO();
            BeanUtils.copyProperties(question,questionDTO);
            questionDTO.setUser(user);
            questionDTOList.add(questionDTO);
        }
        paginationDTO.setData(questionDTOList);

        return  paginationDTO;
    }
    //获取当前用户，提交问题，显示页面
    public PaginationDTO list(Long userId, Integer page, Integer size) {

        PaginationDTO paginationDTO = new PaginationDTO();
        Integer totalPage;
        QuestionExample questionExample = new QuestionExample();
        questionExample.createCriteria()
                .andCreatorEqualTo(userId);
        Integer totalCount = (int )questionMapper.countByExample(questionExample);//拿到用户总条数

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
        QuestionExample example = new QuestionExample();
        example.createCriteria()
                .andCreatorEqualTo(userId);
        List<Question> questions = questionMapper.selectByExampleWithRowbounds(example, new RowBounds(offset, size));

        List<QuestionDTO> questionDTOList = new ArrayList<>();



        for (Question question : questions) {
            User user = userMapper.selectByPrimaryKey(question.getCreator());
            QuestionDTO questionDTO = new QuestionDTO();
            BeanUtils.copyProperties(question,questionDTO);
            questionDTO.setUser(user);
            questionDTOList.add(questionDTO);
        }
        paginationDTO.setData(questionDTOList);



        return  paginationDTO;
    }

    public QuestionDTO getById(long id) {

        Question question = questionMapper.selectByPrimaryKey(id);
        if(question ==null){
            throw  new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
        }
        QuestionDTO questionDTO = new QuestionDTO();
        BeanUtils.copyProperties(question,questionDTO);
        User user = userMapper.selectByPrimaryKey(question.getCreator());
        questionDTO.setUser(user);
        return  questionDTO;
    }

    public void createOrUpdate(Question question) {//选择是否创建问题
        if(question.getId() ==null){//创建
            question.setGmtCreate(System.currentTimeMillis());
            question.setGmtModified(question.getGmtCreate());
            question.setViewCount(0);
            question.setLikeCount(0);
            question.setCommentCount(0);
            questionMapper.insert(question);
        }
        else
        {
//更新

            Question updateQuestion = new Question();
            updateQuestion.setGmtModified(System.currentTimeMillis());
            updateQuestion.setTitle(question.getTitle());
            updateQuestion.setDescription(question.getDescription());
            updateQuestion.setTag(question.getTag());
            QuestionExample example = new QuestionExample();
            example.createCriteria()
                    .andIdEqualTo(question.getId());

            int updated=questionMapper.updateByExampleSelective(updateQuestion, example);
            if(updated!=1){
                throw  new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
            }
        }
    }
//记录观看数
    public void incView(Long id) {
        Question question = questionMapper.selectByPrimaryKey(id);
        Question updateQuestion = new Question();
        updateQuestion.setViewCount(question.getViewCount()+1);
        QuestionExample questionExample = new QuestionExample();
        questionExample.createCriteria()
                .andIdEqualTo(id);

        questionMapper.updateByExampleSelective(updateQuestion,questionExample);

    }

    public List<QuestionDTO> selectRelate(QuestionDTO queryDTO) {
        if(StringUtils.isBlank(queryDTO.getTag()))
        {
            return  new ArrayList<>();//不可能为空，之前有验证，除非手动加数据
        }
        String[] tags = StringUtils.split(queryDTO.getTag(), ",");
        String regexpTag = Arrays.stream(tags).collect(Collectors.joining("|"));
        Question question = new Question();
        question.setId(queryDTO.getId());
        question.setTag(regexpTag);

        List<Question> questions = questionExtMapper.selectRelated(question);//拿到question  关联列表
        List<QuestionDTO> questionDTOS = questions.stream().map(q -> {
            QuestionDTO questionDTO = new QuestionDTO();
            BeanUtils.copyProperties(q ,questionDTO);
            return questionDTO;
        }).collect(Collectors.toList());
        return questionDTOS;

    }
}
