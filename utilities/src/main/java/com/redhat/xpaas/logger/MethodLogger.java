package com.redhat.xpaas.logger;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import java.lang.reflect.Method;

@Aspect
@SuppressWarnings
  (
    {
      "PMD.AvoidCatchingThrowable",
      "PMD.TooManyMethods",
      "PMD.CyclomaticComplexity"
    }
  )
public final class MethodLogger {

  @Around("execution(* *(..)) && @annotation(com.redhat.xpaas.logger.Loggable)")
  public Object around(ProceedingJoinPoint point) throws Throwable {
    final Method method = MethodSignature.class.cast(point.getSignature()).getMethod();
    Loggable annotation = method.getAnnotation(Loggable.class);
    return this.wrap(point, method, annotation);
  }

  private Object wrap(final ProceedingJoinPoint point, final Method method, final Loggable annotation) throws Throwable {

    if (Thread.interrupted()) {
      throw new IllegalStateException(
        String.format(
          "thread '%s' in group '%s' interrupted",
          Thread.currentThread().getName(),
          Thread.currentThread().getThreadGroup().getName()
        )
      );
    }

    String projectName = annotation.project().isEmpty() ? "radtests" : annotation.project();

    LogWrapper log = new LogWrapper(method.getDeclaringClass(), projectName);
    long start = System.currentTimeMillis();

    // Log start
    String message = annotation.message().isEmpty() ? method.getName() : annotation.message();

    log.start(message);

    Object result = null;
    result = point.proceed();

    log.finish(message, System.currentTimeMillis() - start);

    return result;
  }


}