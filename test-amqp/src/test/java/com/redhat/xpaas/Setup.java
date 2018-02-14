package com.redhat.xpaas;

import com.redhat.xpaas.logger.LogWrapper;
import com.redhat.xpaas.openshift.OpenshiftUtil;
import com.redhat.xpaas.oshinko.deployment.Oshinko;
import com.redhat.xpaas.rad.AMQP.api.AMQPWebUI;

import static com.redhat.xpaas.rad.AMQP.deployment.AMQP.deployArtemis;

public class Setup {
  private LogWrapper log = new LogWrapper(Setup.class, "amq");
  private String NAMESPACE = RadConfiguration.masterNamespace();
  private OpenshiftUtil openshift = OpenshiftUtil.getInstance();
  private AMQPWebUI AMQP;

  AMQPWebUI initializeApplications() {
    log.action("creating-new-namespace", this::initializeProject);
    log.action("starting-oshinko-instance", Oshinko::deploySparkFromResource);
    log.action("deploy-AMQP", () -> AMQP = deployArtemis());
    return AMQP;
  }

  void cleanUp() {
    if(RadConfiguration.deleteNamespaceAfterTests()){
      log.action("deleting-namespace", () -> openshift.deleteProject(NAMESPACE));
    }
  }

  private void initializeProject(){
    OpenshiftUtil.getInstance().createProject(NAMESPACE, RadConfiguration.recreateNamespace());
  }

}
