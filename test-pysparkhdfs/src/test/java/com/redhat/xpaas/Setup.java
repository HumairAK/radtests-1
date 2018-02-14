package com.redhat.xpaas;

import com.redhat.xpaas.logger.LogWrapper;
import com.redhat.xpaas.openshift.OpenshiftUtil;
import com.redhat.xpaas.oshinko.deployment.Oshinko;
import com.redhat.xpaas.rad.PySparkHDFS.api.PySparkHDFSWebUI;
import static com.redhat.xpaas.rad.PySparkHDFS.deployment.PySparkHDFS.deployPySparkHDFS;

public class Setup {
  private LogWrapper log = new LogWrapper(Setup.class, "pysparkhdfs");
  private OpenshiftUtil openshift = OpenshiftUtil.getInstance();
  private String NAMESPACE = RadConfiguration.masterNamespace();
  private static PySparkHDFSWebUI pySparkHDFS;

  PySparkHDFSWebUI initializeApplications() {
    log.action("creating-new-namespace", this::initializeProject);
    log.action("starting-oshinko-instance", Oshinko::deploySparkFromResource);
    log.action("deploy-pysparkhdfs", () -> pySparkHDFS = deployPySparkHDFS());
    return pySparkHDFS;
  }

  void cleanUp() {
    if(pySparkHDFS != null){
      log.action("shutting-down-webdrivers", () -> pySparkHDFS.webDriverCleanup());
    }

    if(RadConfiguration.deleteNamespaceAfterTests()){
      log.action("deleting-namespace", () -> openshift.deleteProject(NAMESPACE));
    }
  }

  private void initializeProject(){
    OpenshiftUtil.getInstance().createProject(NAMESPACE, RadConfiguration.recreateNamespace());
  }

}
