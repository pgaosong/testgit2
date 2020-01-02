package cn.smbms.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * 局部异常处理--只能处理当前控制器内的异常，不能处理其他控制器内发生的异常
 */

@Controller
@RequestMapping(("/test"))
public class TestController {

    @RequestMapping("/exception")
    public String testException(Integer id){
        if(id==1){
            System.out.println("test:空指针异常");
            throw new NullPointerException("空指针异常");
        }else if(id==2){
            System.out.println("test:算数异常");
            throw new ArithmeticException("算数异常");
        }
        return null;
    }
}
