package com.redhat.xpaas.logger;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Aspect
public class MethodLogger {

  private final Logger log = LoggerFactory.getLogger(MethodLogger.class);

  @Around("execution(* *(..)) && @annotation(Loggable)")
  public Object around(ProceedingJoinPoint point) {
    long start = System.currentTimeMillis();
    Object result = null;
    try {
      result = point.proceed();
    } catch (Throwable throwable) {
      throwable.printStackTrace();
    }

    Object[] args = point.getArgs();
    String message = String.format("action=%s: %s in %sms",
      MethodSignature.class.cast(point.getSignature()).getMethod().getName(),
      result,
      System.currentTimeMillis() - start);

    log.info(message);
    return result;
  }
}