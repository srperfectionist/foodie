package com.sr.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.time.Instant;

/**
 * @author SR
 * @date 2019/12/1
 */
@Component
@Aspect
@Slf4j
public class ServiceLogAspect {

    /**
     * 切面表达式：
     * execution 代表所要执行的表达式主体
     * 第一处 *  代表方法返回类型 *代表所有类型
     * 第二处    包名代表aop监控的类所在的包
     * 第三处 .. 代表该包以及其子包下的所有类方法
     * 第四处 *  代表类名，*代表所有类
     * 第五处 *(..) *代表类中的方法名，(..)表示方法中的任何参数
     * @param joinPoint
     * @return
     * @throws Throwable
     */
    @Around("execution(* com.sr.service.impl..*.*(..))")
    public Object recordTimeLog(ProceedingJoinPoint joinPoint) throws Throwable {

        log.info("[开始执行] {}.{}", joinPoint.getTarget().getClass(), joinPoint.getSignature().getName());

        // 开始记录时间
        long begin = Instant.now().toEpochMilli();

        // 执行目标 service
        Object result = joinPoint.proceed();

        // 记录结束时间
        long end = Instant.now().toEpochMilli();

        long takeTime = (end - begin)/1000;

        long errTime = 3000L;
        long warnTime = 2000L;

        if (takeTime > errTime){
            log.error("执行结束，耗时 [{}] 秒", takeTime);
        }else if(takeTime > warnTime){
            log.warn("执行结束，耗时 [{}] 秒", takeTime);
        }else{
            log.info("执行结束，耗时 [{}] 秒", takeTime);
        }

        return result;
    }
}
