package com.yang.commons.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

@Data
@NoArgsConstructor
public class PageResult implements Serializable {
    //总记录数
    private Long counts = 0L;
    //页大小
    private Integer pagesize = 10;
    //总页数
    private Long pages = 0L;
    //当前页码
    private Integer page = 1;
    //列表
    private List<?> items = Collections.emptyList();

    public PageResult(Integer page, Integer pagesize,
                      Long counts, List list) {
        this.page = page;
        this.pagesize = pagesize;
        this.items = list;
        this.counts = counts;
        this.pages = counts % pagesize == 0 ? counts / pagesize : counts / pagesize + 1;
    }

}