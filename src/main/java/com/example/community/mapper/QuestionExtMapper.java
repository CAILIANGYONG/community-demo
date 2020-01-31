package com.example.community.mapper;

import com.example.community.model.Question;
import com.example.community.model.QuestionExample;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;
@Mapper
public interface QuestionExtMapper {

    int incView(Question record);
    int intCommentCount(Question record);
    List<Question> selectRelated(Question question);
}