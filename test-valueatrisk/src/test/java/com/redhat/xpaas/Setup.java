package com.redhat.xpaas;

import com.redhat.xpaas.logger.LogWrapper;
import com.redhat.xpaas.openshift.OpenshiftUtil;
import com.redhat.xpaas.rad.ValueAtRisk.api.ValueAtRiskWebUI;
import static com.redhat.xpaas.rad.ValueAtRisk.deployment.ValueAtRisk.deployValueAtRisk;

public class Setup {

  private LogWrapper log = new LogWrapper(Setup.class, "valueatrisk");
  private OpenshiftUtil openshift = OpenshiftUtil.getInstance();
  private String NAMESPACE = RadConfiguration.masterNamespace();
  private ValueAtRiskWebUI ValueAtRisk;

  ValueAtRiskWebUI initializeApplications() {
    log.action("creating-new-namespace", this::initializeProject);
    log.action("deploy-pysparkhdfs", () -> ValueAtRisk = deployValueAtRisk());
    return ValueAtRisk;
  }

  void cleanUp() {
    if(ValueAtRisk != null){
      log.action("shutting-down-webdrivers", () -> ValueAtRisk.webDriverCleanup());
    }

    if(RadConfiguration.deleteNamespaceAfterTests()){
      log.action("deleting-namespace", () -> openshift.deleteProject(NAMESPACE));
    }
  }

  private void initializeProject(){
    OpenshiftUtil.getInstance().createProject(NAMESPACE, RadConfiguration.recreateNamespace());
  }

}
