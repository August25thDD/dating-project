package com.yang.commons.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Announcement implements Serializable {

    private Long id;
    private String title;
    private String description;
    private Date created; // 创建时间
    private Date updated; // 修改时间
}