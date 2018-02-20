package com.redhat.xpaas.zeppelin.entity;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class CodeCellWeb implements CodeCell {
  private final WebElement CELL;
  private final WebDriver webDriver;

  public CodeCellWeb(WebElement cell, WebDriver webDriver){
    this.CELL = cell;
    this.webDriver = webDriver;
  }

  @Override
  public CodeCell runCell() {
    CELL.click();
    By byRunButton = By.className("icon-control-play");
    CELL.findElement(byRunButton).click();

    return null;
  }

  @Override
  public String getOutput() {
    return null;
  }

  @Override
  public String getOutput(Long timeout) {
    return null;
  }

  @Override
  public boolean outputHasErrors() {
    return false;
  }

  @Override
  public WebElement getCodeLine(int lineNumber) {
    return null;
  }

  @Override
  public boolean setCodeLine(WebElement element) {
    return false;
  }

  @Override
  public boolean findAndReplaceInCell(String find, String Replace) {
    return false;
  }
}
