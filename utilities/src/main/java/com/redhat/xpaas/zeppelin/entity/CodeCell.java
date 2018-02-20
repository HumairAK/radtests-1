package com.redhat.xpaas.zeppelin.entity;

import org.openqa.selenium.WebElement;

public interface CodeCell {

  CodeCell runCell();

  String getOutput();

  String getOutput(Long timeout);

  boolean outputHasErrors();

  WebElement getCodeLine(int lineNumber);

  boolean setCodeLine(WebElement element);

  boolean findAndReplaceInCell(String find, String Replace);

}
