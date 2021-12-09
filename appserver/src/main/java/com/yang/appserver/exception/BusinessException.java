package com.yang.appserver.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @program: my-tanhua
 * @description:
 * @author: Mr.Yang
 * @create: 2021-12-03 22:52
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BusinessException extends MyException {
    private ErrorResult errorResult;
}
