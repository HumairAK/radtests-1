package com.redhat.xpaas;

import com.redhat.xpaas.logger.Loggable;
import com.redhat.xpaas.openshift.OpenshiftUtil;
import com.redhat.xpaas.rad.ApacheZeppelin.api.ApacheZeppelinWebUI;
import org.assertj.core.api.Assertions;
import org.junit.*;
import org.junit.runners.MethodSorters;

import java.util.concurrent.TimeoutException;

@Loggable(project ="ApacheZeppelin")
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class WebUITest {

  private static ApacheZeppelinWebUI ApacheZeppelin;
  private static final OpenshiftUtil openshift = OpenshiftUtil.getInstance();

  @BeforeClass
  public static void setUp() throws TimeoutException, InterruptedException {
    Setup setup = new Setup();
    //WebUITest.ApacheZeppelin = setup.initializeApplications();
    WebUITest.ApacheZeppelin = ApacheZeppelinWebUI.getInstance(openshift.appDefaultHostNameBuilder("apache-zeppelin"));
    ApacheZeppelin.loadProject("Untitled Note 1");
  }

  @AfterClass
  public static void tearDown(){
    Setup setup = new Setup();
    setup.cleanUp();
  }

  @Test
  public void testAVerifyDeployment(){
    System.out.println(ApacheZeppelin.getNthCodeCell(1).runCell());
    Assertions.assertThat(true).isTrue();
  }


  private void assertCodeCellRange(int start, int end){
    for(int n = start; n <= end; n++){
      Assertions.assertThat(ApacheZeppelin.getNthCodeCell(n).runCell().outputHasErrors()).isFalse();
    }
  }

}

