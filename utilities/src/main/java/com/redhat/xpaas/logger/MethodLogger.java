package com.redhat.xpaas.logger;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import java.lang.reflect.Method;

@Aspect
public class MethodLogger {

  @Around("execution(* *(..)) && @annotation(com.redhat.xpaas.logger.Loggable)")
  public Object around(ProceedingJoinPoint point) {
    final Method method = MethodSignature.class.cast(point.getSignature()).getMethod();
    Loggable annotation = method.getAnnotation(Loggable.class);

    LogWrapper log = new LogWrapper(method.getDeclaringClass(), annotation.project());

    long start = System.currentTimeMillis();

    String message =  String.format("action=%s: method=%s", annotation.message(), method.getName());

    log.info(message);

    Object result = null;
    try {
      result = point.proceed();
    } catch (Throwable throwable) {
      throwable.printStackTrace();
    }

    message = String.format("action=%s: method=%s in %sms", annotation.message(), method.getName(),
      System.currentTimeMillis() - start);

    log.info(message);
    return result;
  }


}