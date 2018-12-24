package com.nowcoder.wenda.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component  //不明确是什么组件情况下，在进行依赖注入时也构造出来
//在调用所有构造好的Controller方法之前都调用before，之后都调用after方法
public class LogAspect {
    private static final Logger logger = LoggerFactory.getLogger(LogAspect.class);
    //执行（返回值 包.类.方法(参数））
    @Before("execution(* com.nowcoder.wenda.controller.*Controller.*(..))")
    public void beforeMethod(JoinPoint joinPoint){
        StringBuilder sb = new StringBuilder();
        for (Object arg : joinPoint.getArgs()) {
            if (arg != null) {
                sb.append("arg:" + arg.toString() + "|");
            }
        }
        logger.info("before Method"+sb.toString());
    }

    @After("execution(* com.nowcoder.wenda.controller.IndexController.*(..))")
    public void afterMethod(){
        logger.info("after Method");
    }
}
