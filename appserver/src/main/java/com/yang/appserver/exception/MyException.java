package com.yang.appserver.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @program: my-tanhua
 * @description:
 * @author: Mr.Yang
 * @create: 2021-12-03 21:20
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MyException extends RuntimeException {
    private ErrorResult errorResult;

}
