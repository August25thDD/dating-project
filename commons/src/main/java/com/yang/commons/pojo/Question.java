package com.yang.commons.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Question {

    private Long id;
    private Long userId;
    //问题内容
    private String txt;
    private Date created;
    private Date updated;
}