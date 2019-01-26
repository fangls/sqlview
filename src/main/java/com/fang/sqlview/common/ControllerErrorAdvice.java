package com.fang.sqlview.common;

import com.fang.sqlview.controller.SqlViewController;
import com.fang.sqlview.domain.BaseResult;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletRequest;

/**
 * Description：
 *
 * @author fangliangsheng
 * @date 2019/1/26
 */
@ControllerAdvice(basePackageClasses = SqlViewController.class)
public class ControllerErrorAdvice extends ResponseEntityExceptionHandler {

    @ExceptionHandler
    @ResponseBody
    ResponseEntity<?> handleControllerException(HttpServletRequest request, Throwable ex) {
        return new ResponseEntity<>(new BaseResult(false, ex.getMessage()), HttpStatus.OK);
    }

}
