package com.yang.commons.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnnouncementVo implements Serializable {

    private String id;
    private String title;
    private String description;
    private String createDate;

}