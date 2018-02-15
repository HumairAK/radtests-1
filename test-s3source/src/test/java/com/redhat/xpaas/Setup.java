package com.redhat.xpaas;

import com.jcabi.aspects.Loggable;
import com.redhat.xpaas.logger.LogWrapper;
import com.redhat.xpaas.openshift.OpenshiftUtil;
import com.redhat.xpaas.rad.S3Source.api.S3SourceWebUI;

import static com.redhat.xpaas.rad.S3Source.deployment.S3Source.deployS3Source;

@Loggable(prepend = true)
public class Setup {
  private LogWrapper log = new LogWrapper(Setup.class, "s3source");
  private static final OpenshiftUtil openshift = OpenshiftUtil.getInstance();
  private String NAMESPACE = RadConfiguration.masterNamespace();
  private static S3SourceWebUI S3Source;

  S3SourceWebUI initializeApplications() {
    log.action("creating-new-namespace", this::initializeProject);
    log.action("deploy-s3source", () -> S3Source = deployS3Source());
    return S3Source;
  }

  void cleanUp() {
    if(S3Source != null){
      log.action("shutting-down-webdrivers", () -> S3Source.webDriverCleanup());
    }

    if(RadConfiguration.deleteNamespaceAfterTests()){
      log.action("deleting-namespace", () -> openshift.deleteProject(NAMESPACE));
    }
  }

  private void initializeProject(){
    OpenshiftUtil.getInstance().createProject(NAMESPACE, RadConfiguration.recreateNamespace());
  }


}
