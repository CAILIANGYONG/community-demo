package com.example.community.exception;

public enum CustomizeErrorCode implements ICustomizeErrorCode{

    QUESTION_NOT_FOUND(2001,"你找的问题不在了要不要换个试试？"),
   TARGET_PARAM__NOT_FOUND(2002,"未选择任何问题？"),
   NO_LOGIN(2003,"当前操作需要登陆，请登陆后重试"),
   SYS_ERROR(2004,"服务器冒烟了，等等再试试？"),
   TYPE_PARM_WRONG(2005,"评论错误或不存在"),
    COMMENT_NOT_FOUND(2006,"回复的评论不存在了，要不要换一个试试？"),
    CONTNET_IS_EMPTY(2007,"输入内容不能为空")
    ;

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public Integer getCode() {
        return code;
    }

    private String message;
    private Integer code;

    CustomizeErrorCode(Integer code, String message) {
        this.message = message;
        this.code = code;
    }

}
