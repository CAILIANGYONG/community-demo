package com.example.community.service;


import com.example.community.dto.commentDTO;
import com.example.community.enums.CommentTypeEnum;
import com.example.community.enums.NotificationStatusEnum;
import com.example.community.enums.NotificationTypeEnum;
import com.example.community.exception.CustomizeErrorCode;
import com.example.community.exception.CustomizeException;
import com.example.community.mapper.*;
import com.example.community.model.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CommentService {
    @Autowired
    private CommentMapper commentMapper;
    @Autowired

    private QuestionMapper questionMapper;
    @Autowired
    private QuestionExtMapper questionExtMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private CommentExtMapper commentExtMapper;
    @Autowired
    private NotificationMapper notificationMapper;
    @Transactional
    public void insert(Comment comment, User commentator) {
        if(comment.getParentId()==null || comment.getParentId()==0){
            throw  new CustomizeException(CustomizeErrorCode.TARGET_PARAM__NOT_FOUND);
        }
        if(comment.getType()==null || !CommentTypeEnum.isExist(comment.getType()))
        {
            throw  new CustomizeException(CustomizeErrorCode.TYPE_PARM_WRONG);

        }
        if(comment.getType() == CommentTypeEnum.COMMENT.getType())
        //回复评论
        {
            Comment dbComment = commentMapper.selectByPrimaryKey(comment.getParentId());
            if(dbComment==null)
            {
                throw  new CustomizeException(CustomizeErrorCode.COMMENT_NOT_FOUND);

            }
            //回复问题
            Question question = questionMapper.selectByPrimaryKey(dbComment.getParentId());
            if(question==null){
                throw  new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);

            }
            comment.setCommentCount(0);
            commentMapper.insert(comment);

            Comment parentComment = new Comment();
            parentComment.setId(comment.getParentId());
            parentComment.setCommentCount(1);
            commentExtMapper.intCommentCount(parentComment);
            createNotify(comment, dbComment.getCommentator(), commentator.getName(), question.getTitle(), NotificationTypeEnum.REPLY_COMMENT,question.getId());//创建通知
        }
        else
        {
            //回复问题
            Question question = questionMapper.selectByPrimaryKey(comment.getParentId());
            if(question==null){
                throw  new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);

            }
            commentMapper.insert(comment);
            question.setCommentCount(1);
            questionExtMapper.intCommentCount(question);
         createNotify(comment,question.getCreator(),commentator.getName(),question.getTitle(),NotificationTypeEnum.REPLY_QUESTION,question.getId());  //创建通知
        }
    }

    private void createNotify(Comment comment, Long reciver, String notifierName, String outerTitle, NotificationTypeEnum notificationType,Long outerId) {
        if(reciver == comment.getCommentator()) //自己回复自己不用加提示， 居然可以
        {
            return;
        }
        Notification notification = new Notification();
        notification.setGmtCreate(System.currentTimeMillis());
        notification.setType(notificationType.getType());
        notification.setOuterid(outerId);
        notification.setNotifier(comment.getCommentator());  //拿到原问题创建者id
        notification.setStatus(NotificationStatusEnum.UNREAD.getStatus());

        notification.setReceiver(reciver);       //拿到问题
        notification.setNotifierName(notifierName);
        notification.setOuterTitle(outerTitle);

        notificationMapper.insert(notification);
    }

    public List<commentDTO> listByTargetId(Long id, CommentTypeEnum type) {
        CommentExample commentExample = new CommentExample();
        commentExample.createCriteria()
                .andParentIdEqualTo(id)
                .andTypeEqualTo(type.getType())
        ;
        commentExample.setOrderByClause("gmt_create desc");
        List<Comment> comments = commentMapper.selectByExample(commentExample);
        if(comments.size()==0)
        {
            return new ArrayList<>();
        }
        //获取去重的评论人
        Set<Long> commentators = comments.stream().map(comment -> comment.getCommentator()).collect(Collectors.toSet());
        List<Long> userIds = new ArrayList();
        userIds.addAll(commentators);


        //获取评论人并转换为map   ，map可以一次获取，降低时间复杂度
        UserExample userExample = new UserExample();
        userExample.createCriteria()
                .andIdIn(userIds);
        List<User> users = userMapper.selectByExample(userExample);
        Map<Long, User> userMap = users.stream().collect(Collectors.toMap(user -> user.getId(), user -> user));

        //转换 comment 为 commmentDTO
        List<commentDTO> commentDTOS = comments.stream().map(comment -> {
            commentDTO commentDTO1 = new commentDTO();
            BeanUtils.copyProperties(comment,commentDTO1);
            commentDTO1.setUser(userMap.get(comment.getCommentator()));
            return commentDTO1;
        }).collect(Collectors.toList());
        return  commentDTOS;
    }
}
