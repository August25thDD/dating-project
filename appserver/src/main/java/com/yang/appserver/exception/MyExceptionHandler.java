package com.yang.appserver.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * @program: my-tanhua
 * @description:
 * @author: Mr.Yang
 * @create: 2021-12-03 21:20
 **/
@ControllerAdvice
public class MyExceptionHandler {
    //    ResponseEntity  封装了http的response
    @ExceptionHandler(MyException.class)
    public ResponseEntity myException(MyException myException) {
        myException.printStackTrace();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(myException.getErrorResult());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity exception(Exception exception) {
        exception.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResult.error());
    }


}
