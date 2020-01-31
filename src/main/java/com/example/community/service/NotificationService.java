package com.example.community.service;


import com.example.community.dto.NotificationDTO;
import com.example.community.dto.PaginationDTO;
import com.example.community.dto.QuestionDTO;
import com.example.community.enums.NotificationStatusEnum;
import com.example.community.enums.NotificationTypeEnum;
import com.example.community.exception.CustomizeErrorCode;
import com.example.community.exception.CustomizeException;
import com.example.community.mapper.NotificationMapper;
import com.example.community.mapper.UserMapper;
import com.example.community.model.*;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class NotificationService {

    @Autowired
    private NotificationMapper notificationMapper;
    @Autowired
    private UserMapper userMapper;

public PaginationDTO list(Long userId,Integer page,Integer size)
{
    PaginationDTO<NotificationDTO> paginationDTO = new PaginationDTO<>();
    Integer totalPage;
    NotificationExample notificationExample = new NotificationExample();
    notificationExample.createCriteria()
            .andReceiverEqualTo(userId);
    Integer totalCount = (int )notificationMapper.countByExample(notificationExample);//拿到用户总条数

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
    NotificationExample notificationExample1 = new NotificationExample();
    notificationExample1.createCriteria()
            .andReceiverEqualTo(userId);
    notificationExample1.setOrderByClause("gmt_create desc");
    List<Notification> notifications = notificationMapper.selectByExampleWithRowbounds(notificationExample1, new RowBounds(offset, size));
    if(notifications.size()==0)
    {
        return  paginationDTO;

    }
    List<NotificationDTO> notificationDTOS = new ArrayList<>();

    for (Notification notification : notifications) {
        NotificationDTO notificationDTO = new NotificationDTO();
        BeanUtils.copyProperties(notification,notificationDTO);
        notificationDTO.setTypeName(NotificationTypeEnum.nameOfType(notification.getType()));

        notificationDTOS.add(notificationDTO);
    }


    paginationDTO.setData(notificationDTOS);

    return  paginationDTO;
}

    public Long unreadCount(Long userId) {       //数量是怎么升上去的？？？？
        NotificationExample notificationExample = new NotificationExample();
        notificationExample.createCriteria()
                .andReceiverEqualTo(userId)
                .andStatusEqualTo(NotificationStatusEnum.UNREAD.getStatus())
        ;
        return notificationMapper.countByExample(notificationExample);
    }

    public NotificationDTO read(Long id, User user) {
        Notification notification = notificationMapper.selectByPrimaryKey(id);
        if(notification==null)
        {
            throw new CustomizeException(CustomizeErrorCode.NOTIFICATION_NOT_FOUND);
        } if(!Objects.equals( notification.getReceiver(),user.getId()))
        {
            throw new CustomizeException(CustomizeErrorCode.READ_NOTIFICATION_FAIL);
        }
        notification.setStatus(NotificationStatusEnum.READ.getStatus());
        notificationMapper.updateByPrimaryKey(notification);
        NotificationDTO notificationDTO = new NotificationDTO();
        BeanUtils.copyProperties(notification,notificationDTO);
        notificationDTO.setTypeName(NotificationTypeEnum.nameOfType(notification.getType()));

        return notificationDTO;
    }
}
