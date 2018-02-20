package com.redhat.xpaas.rad.ApacheZeppelin.api;

import com.redhat.xpaas.RadConfiguration;
import com.redhat.xpaas.util.TestUtil;
import com.redhat.xpaas.zeppelin.ZeppelinWebUI;
import org.openqa.selenium.WebDriver;

public class ApacheZeppelinWebUI extends ZeppelinWebUI{
  public static ApacheZeppelinWebUI getInstance(String hostname) {
    return new ApacheZeppelinWebUI(TestUtil.createDriver(RadConfiguration.useHeadlessForTests()), hostname);
  }

  private ApacheZeppelinWebUI(WebDriver webDriver, String hostname) {
    super(webDriver, hostname);
  }

}
