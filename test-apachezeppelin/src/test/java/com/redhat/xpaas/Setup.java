package com.redhat.xpaas;

import com.redhat.xpaas.logger.Loggable;
import com.redhat.xpaas.openshift.OpenshiftUtil;
import com.redhat.xpaas.oshinko.deployment.Oshinko;
import com.redhat.xpaas.rad.ApacheZeppelin.api.ApacheZeppelinWebUI;

import java.util.concurrent.TimeoutException;

import static com.redhat.xpaas.rad.ApacheZeppelin.deployment.ApacheZeppelin.deployApacheZeppelin;

@Loggable(project = "apachezeppelin")
public class Setup {
  private static final OpenshiftUtil openshift = OpenshiftUtil.getInstance();
  private String NAMESPACE = RadConfiguration.masterNamespace();
  private static ApacheZeppelinWebUI ApacheZeppelin;

  public ApacheZeppelinWebUI initializeApplications() throws TimeoutException, InterruptedException {
    initializeProject();
    Oshinko.deploySparkFromResource();
    ApacheZeppelin = deployApacheZeppelin();
    return ApacheZeppelin;
  }

  public void cleanUp() {
    if(ApacheZeppelin != null){
      ApacheZeppelin.webDriverCleanup();
    }

    if(RadConfiguration.deleteNamespaceAfterTests()){
      openshift.deleteProject(NAMESPACE);
    }
  }

  private void initializeProject(){
    OpenshiftUtil.getInstance().createProject(NAMESPACE, RadConfiguration.recreateNamespace());
  }

}
