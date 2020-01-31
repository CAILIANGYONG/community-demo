package com.example.community.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
@Data
public class PaginationDTO<T> {
    private List<T> data; //找到了     问题列表    任意类型，可能是问题，也可能是通知
    private  boolean showPrevious;//全是旗标   秒啊
    private  boolean showFisrtPage;
    private  boolean showNext;
    private  boolean showEndPage;
    private  Integer totalPage;
    private Integer page;
    private  List<Integer> pages =new ArrayList<>();

    public void setPagination(Integer totalPage, Integer page) {
        this.totalPage=totalPage;
        this.page=page;
        pages.add(page);
        for(int i=1;i<=3;i++)
        {
            if(page-i>0)
            {
                pages.add(0,page-i);
            }
            if(page+i<totalPage){
                pages.add(page+i);
            }
        }
        //是否展示上一页
        if(page==1)
        {
            showPrevious =false;
        }
        else
        {
            showPrevious=true;
        }
        //是否展示下一页
        if(page ==totalPage)
        {
            showNext=false;
        }
        else{
            showNext = true;
        }
        //是否展示第一页
        if(pages.contains(1))
        {
            showFisrtPage = false;
        }else
        {
            showFisrtPage = true;
        }
        //是否展示最后一页
        if(pages.contains(totalPage)){
            showEndPage=false;
        }else
        {
            showEndPage=true;
        }
    }
}
