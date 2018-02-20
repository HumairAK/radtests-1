package com.redhat.xpaas.zeppelin;

import com.redhat.xpaas.zeppelin.entity.CodeCell;
import com.redhat.xpaas.zeppelin.entity.CodeCellWeb;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;
import java.util.stream.Collectors;

public class ZeppelinWebUI implements ZeppelinAPI {
  private final WebDriver webDriver;
  private final String HOSTNAME;

  public ZeppelinWebUI(WebDriver webDriver, String hostname){
    this.webDriver = webDriver;
    this.HOSTNAME = hostname;
  }

  @Override
  public void loadProject(String projectName) {
    String url = "http://" + HOSTNAME;
    webDriver.get(url);
    By byNoteBookList = By.cssSelector("#notebook-names");

    // Wait for projects list to load
    WebDriverWait wait = new WebDriverWait(webDriver,30);
    wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#notebook-names > div")));

    WebElement noteBookList = webDriver.findElement(byNoteBookList);
    List<WebElement> projects = noteBookList.findElements(By.cssSelector("a.ng-binding"));
    boolean found = false;
    for(WebElement project : projects ){
      List<WebElement> list = project.findElements(By.xpath("./i"));
      if(list.size() > 0 && project.getText().contains(projectName)){
        project.click();
        found = true;
      }
    }

    if(!found){
      throw new RuntimeException("Unable to locate project in root directory.");
    }
  }

  @Override
  public CodeCell getNthCodeCell(int n) {
    if (n <= 0){
      throw new IndexOutOfBoundsException("Cell index must be greater than 0");
    }
    List<WebElement> codeCells = getAllCodeCells();

    return codeCells.isEmpty() ? null : new CodeCellWeb(codeCells.get(n - 1), webDriver);
  }

  @Override
  public void webDriverCleanup() {
    webDriver.quit();
  }

  private List<WebElement> getAllCodeCells(){
    By byNotebookContainer = By.id("content");
    WebDriverWait wait = new WebDriverWait(webDriver,30);
    wait.until(ExpectedConditions.visibilityOfElementLocated(byNotebookContainer));

    List<WebElement> container = webDriver.findElement(byNotebookContainer).findElements(By.xpath("./div"));

    return container.stream().filter(cells -> cells.getAttribute("id").contains("paragraphColumn_main")).collect(Collectors.toList());
  }

}
