package com.redhat.xpaas.logger;

import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogWrapper {
  private final Logger log;
  private final LoggerUtil logUtil;

  public LogWrapper(Class logClass, String projectName){
    log = LoggerFactory.getLogger(logClass);
    logUtil = new LoggerUtil(projectName);
  }

  public void action(String message, Runnable func){
    log.info(logUtil.start(message));
    func.run();
    log.info(logUtil.finish(message));
  }

  public Logger getLogger(){
    return this.log;
  }

  public void start(String message){
    log.info(logUtil.start(message));
  }

  public void finish(String message, Long time){
    log.info(logUtil.finish(message, time));
  }

  public void error(String action, String errorMsg){
    log.error(logUtil.error(action, errorMsg));
  }

  public void info(String message){
    log.info(logUtil.start(message));
  }

  public void logTestStart(Description description){
    log.info(logUtil.start("executing-test-" + description.getMethodName()));
  }

  public void logTestFail(Description description){
    log.info(logUtil.failed("executing-test-" + description.getMethodName()));
  }

  public void logTestSuccess(Description description){
    log.info(logUtil.passed("executing-test-" + description.getMethodName()));
  }

  public TestRule getLogTestWatcher(){
    return new TestWatcher() {
      private long start;

      protected void starting(Description description) {
        log.info(logUtil.start("executing-test-" + description.getMethodName()));
        start = System.currentTimeMillis();
      }

      @Override
      protected void failed(Throwable e, Description description) {
        long end = System.currentTimeMillis();
        long took = (end - start);
        log.info(logUtil.failed("executing-test-" + description.getMethodName(), took));
      }

      @Override
      protected void succeeded(Description description) {
        long end = System.currentTimeMillis();
        long took = (end - start);
        log.info(logUtil.passed("executing-test-" + description.getMethodName(), took));
      }
    };
  }
}
