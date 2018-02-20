package com.redhat.xpaas.rad.ApacheZeppelin.deployment;

import com.redhat.xpaas.RadConfiguration;
import com.redhat.xpaas.logger.LoggerUtil;
import com.redhat.xpaas.openshift.OpenshiftUtil;
import com.redhat.xpaas.rad.ApacheZeppelin.api.ApacheZeppelinWebUI;
import com.redhat.xpaas.wait.WaitUtil;
import io.fabric8.openshift.api.model.Template;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

public class ApacheZeppelin {
  private static final OpenshiftUtil openshift = OpenshiftUtil.getInstance();
  private static final String NAMESPACE = RadConfiguration.masterNamespace();
  private static final String APP_NAME = "apache-zeppelin";

  public static ApacheZeppelinWebUI deployApacheZeppelin() throws TimeoutException, InterruptedException {
    String apacheZeppelinTemplate = "/zeppelin-openshift.yaml";

    Template template = openshift.withAdminUser(client ->
      client.templates().inNamespace(NAMESPACE).load(ApacheZeppelin.class.getResourceAsStream(apacheZeppelinTemplate)).createOrReplace()
    );

    Map<String, String> parameters = new HashMap<>();
    parameters.put("template", "$namespace/apache-zeppelin-openshift");
    parameters.put("APPLICATION_NAME", APP_NAME);
    parameters.put("GIT_URI", "https://github.com/rimolive/zeppelin-notebooks.git");
    parameters.put("ZEPPELIN_INTERPRETERS", "md");

    openshift.loadTemplate(template, parameters);

    boolean succeeded = WaitUtil.waitForActiveBuildsToComplete();

    if(!succeeded){
      throw new IllegalStateException(LoggerUtil.openshiftError("apache-zeppelin builds", "build"));
    }

    succeeded = WaitUtil.waitForPodsToReachRunningState("app", APP_NAME, 1);

    if(!succeeded){
      throw new IllegalStateException(LoggerUtil.openshiftError("apache-zeppelin deployment", "pod"));
    }

    return ApacheZeppelinWebUI.getInstance(openshift.appDefaultHostNameBuilder("apache-zeppelin"));
  }

}
