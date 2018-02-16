package com.redhat.xpaas;

import com.redhat.xpaas.logger.LogWrapper;
import com.redhat.xpaas.openshift.OpenshiftUtil;
import com.redhat.xpaas.oshinko.deployment.Oshinko;
import com.redhat.xpaas.rad.CephSource.api.CephSourceWebUI;
import static com.redhat.xpaas.rad.CephSource.deployment.CephSource.deployCephSource;

public class Setup {
  private LogWrapper log = new LogWrapper(Setup.class, "pysparkhdfs");
  private String NAMESPACE = RadConfiguration.masterNamespace();
  private static final OpenshiftUtil openshift = OpenshiftUtil.getInstance();
  private static CephSourceWebUI CephSource;

  public CephSourceWebUI initializeApplications() {
    log.action("creating-new-namespace", this::initializeProject);
    log.action("starting-oshinko-instance", Oshinko::deploySparkFromResource);
    log.action("deploy-CephSource", () -> CephSource = deployCephSource());
    return CephSource;
  }

  public void cleanUp() {
    if(CephSource != null){
      log.action("shutting-down-webdrivers", () -> CephSource.webDriverCleanup());
    }

    if(RadConfiguration.deleteNamespaceAfterTests()){
      log.action("deleting-namespace", () -> openshift.deleteProject(NAMESPACE));
    }
  }

  private void initializeProject(){
    OpenshiftUtil.getInstance().createProject(NAMESPACE, RadConfiguration.recreateNamespace());
  }

}
