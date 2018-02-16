package com.redhat.xpaas;

import com.redhat.xpaas.logger.LogWrapper;
import com.redhat.xpaas.logger.Loggable;
import com.redhat.xpaas.openshift.OpenshiftUtil;
import com.redhat.xpaas.rad.S3Source.api.S3SourceWebUI;

import static com.redhat.xpaas.rad.S3Source.deployment.S3Source.deployS3Source;

public class Setup {
  private LogWrapper log = new LogWrapper(Setup.class, "s3source");
  private static final OpenshiftUtil openshift = OpenshiftUtil.getInstance();
  private String NAMESPACE = RadConfiguration.masterNamespace();
  private static S3SourceWebUI S3Source;

  @Loggable(message="Starting initialization", project ="S3Source")
  S3SourceWebUI initializeApplications() {
    initializeProject();
    S3Source = deployS3Source();
    return S3Source;
  }

  void cleanUp() {
    if(S3Source != null){
      S3Source.webDriverCleanup();
    }

    if(RadConfiguration.deleteNamespaceAfterTests()){
      openshift.deleteProject(NAMESPACE);
    }
  }

  private void initializeProject(){
    OpenshiftUtil.getInstance().createProject(NAMESPACE, RadConfiguration.recreateNamespace());
  }


}
