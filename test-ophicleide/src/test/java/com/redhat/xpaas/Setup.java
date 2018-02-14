package com.redhat.xpaas;

import com.redhat.xpaas.logger.LogWrapper;
import com.redhat.xpaas.openshift.OpenshiftUtil;
import com.redhat.xpaas.rad.mongodb.deployment.MongoDB;
import com.redhat.xpaas.rad.ophicleide.api.OphicleideWebUI;
import com.redhat.xpaas.oshinko.deployment.Oshinko;
import com.redhat.xpaas.rad.ophicleide.deployment.Ophicleide;

public class Setup {
  private LogWrapper log = new LogWrapper(Setup.class, "ophicleide");
  private String NAMESPACE = RadConfiguration.masterNamespace();
  private OpenshiftUtil openshift = OpenshiftUtil.getInstance();
  private static OphicleideWebUI ophicleide;

  OphicleideWebUI initializeApplications() {
    log.action("creating-new-namespace", this::initializeProject);
    log.action("starting-oshinko-instance", Oshinko::deploySparkFromResource);
    log.action("starting-mongodb-instance", MongoDB::deployMongoDBPod);
    log.action("starting-ophicleide-instance", () -> ophicleide = Ophicleide.deployOphicleideWebUI());
    return ophicleide;
  }

  void cleanUp() {
    if(ophicleide != null){
      log.action("shutting-down-ophicleide-webdrivers", () -> ophicleide.webDriverCleanup());
    }

    if(RadConfiguration.deleteNamespaceAfterTests()){
      log.action("deleting-namespace", () -> openshift.deleteProject(NAMESPACE));
    }
  }

  private void initializeProject(){
    OpenshiftUtil.getInstance().createProject(NAMESPACE, RadConfiguration.recreateNamespace());
  }
}
