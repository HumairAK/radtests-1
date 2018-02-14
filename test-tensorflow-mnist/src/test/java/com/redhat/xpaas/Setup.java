package com.redhat.xpaas;

import com.redhat.xpaas.logger.LogWrapper;
import com.redhat.xpaas.openshift.OpenshiftUtil;
import com.redhat.xpaas.rad.MNIST.api.MNISTWebUI;

import static com.redhat.xpaas.rad.MNIST.deployment.MNIST.deployMNIST;

public class Setup {
  private LogWrapper log = new LogWrapper(Setup.class, "mnist");
  private OpenshiftUtil openshift = OpenshiftUtil.getInstance();
  private String NAMESPACE = RadConfiguration.masterNamespace();
  private static MNISTWebUI MNIST;

  MNISTWebUI initializeApplications() {
    log.action("creating-new-namespace", this::initializeProject);
    log.action("deploy-MNIST", () -> MNIST = deployMNIST());
    return MNIST;
  }

  void cleanUp() {
    if(MNIST != null){
      log.action("shutting-down-webdrivers", () -> MNIST.webDriverCleanup());
    }

    if(RadConfiguration.deleteNamespaceAfterTests()){
      log.action("deleting-namespace", () -> openshift.deleteProject(NAMESPACE));
    }
  }

  private void initializeProject(){
    OpenshiftUtil.getInstance().createProject(NAMESPACE, RadConfiguration.recreateNamespace());
  }

}
