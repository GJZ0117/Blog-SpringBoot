package com.gjz.Aspect;

import com.alibaba.fastjson.JSON;
import com.gjz.utils.HttpContextUtils;
import com.gjz.utils.IpUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;

@Component
@Aspect
@Slf4j
public class LogAspect {

    @Pointcut("@annotation(com.gjz.Aspect.LogAnnotation)")
    public void pt() {
    }

    // 环绕通知
    @Around("pt()")
    public Object log(ProceedingJoinPoint joinPoint) throws Throwable {
        long beginTime = System.currentTimeMillis();
        // 执行方法
        Object result = joinPoint.proceed();
        // 执行时间
        long time = System.currentTimeMillis() - beginTime;
        // 保存日志
        recordLog(joinPoint, time);

        return result;
    }

    private synchronized void recordLog(ProceedingJoinPoint joinPoint, long time) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        LogAnnotation logAnnotation = method.getAnnotation(LogAnnotation.class);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date now = new Date();
        String requestTime = sdf.format(now);

        log.info("====================== log start =====================");
        log.info("resuest time: {}", requestTime);
        log.info("module: {}", logAnnotation.module());
        log.info("operation: {}", logAnnotation.operation());

        //请求的方法名
        String className = joinPoint.getTarget().getClass().getName();
        String methodName = signature.getName();
        log.info("request method: {}", className + "." + methodName + "()");

        //请求的参数
        Object[] args = joinPoint.getArgs();
        if (args.length > 0) {
            String params = JSON.toJSONString(args[0]);
            log.info("params: {}", params);
        }

        //获取request 设置IP地址
        HttpServletRequest request = HttpContextUtils.getHttpServletRequest();
        log.info("ip: {}", IpUtils.getIpAddr(request));

        log.info("excute time : {} ms", time);
        log.info("====================== log   end =====================");
    }

}
