package com.redhat.xpaas.rad.ophicleide.deployment;

import com.redhat.xpaas.openshift.OpenshiftUtil;
import com.redhat.xpaas.rad.ophicleide.api.OphicleideWebUI;
import com.redhat.xpaas.RadConfiguration;
import com.redhat.xpaas.wait.WaitUtil;
import io.fabric8.openshift.api.model.BuildRequestBuilder;
import io.fabric8.openshift.api.model.Template;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;
import java.util.function.BooleanSupplier;

public class Ophicleide {
  private static final OpenshiftUtil openshift = OpenshiftUtil.getInstance();
  private static final String APP_NAME = RadConfiguration.ophicleideAppName();
  private static final Long TIMEOUT = RadConfiguration.timeout();
  private static final String NAMESPACE = RadConfiguration.masterNamespace();

  public static OphicleideWebUI deployOphicleideWebUI() {
    initializeOphicleideResources();
    startBuilds();
    launchApplication();
    return OphicleideWebUI.getInstance(openshift.appDefaultHostNameBuilder("ophicleide-web"));
  }

  private static void initializeOphicleideResources(){
    String buildConfigTraining = "/ophicleide/buildConfig-training.yaml";
    String buildConfigWeb = "/ophicleide/buildConfig-web.yaml";
    String imageStreamTraining = "/ophicleide/imagestream-training.yaml";
    String imageStreamWeb = "/ophicleide/imagestream-web.yaml";

    // Load ImageStreamConfig
    openshift.withAdminUser(client ->
      client.imageStreams().inNamespace(NAMESPACE).load(Ophicleide.class.getResourceAsStream(imageStreamTraining)).create()
    );
    openshift.withAdminUser(client ->
      client.imageStreams().inNamespace(NAMESPACE).load(Ophicleide.class.getResourceAsStream(imageStreamWeb)).create()
    );

    // Load BuildConfigs
    openshift.withAdminUser(client ->
      client.buildConfigs().inNamespace(NAMESPACE).load(Ophicleide.class.getResourceAsStream(buildConfigTraining)).create()
    );
    openshift.withAdminUser(client ->
      client.buildConfigs().inNamespace(NAMESPACE).load(Ophicleide.class.getResourceAsStream(buildConfigWeb)).create()
    );


  }

  private static void startBuilds(){
    // Run Builds
    openshift.withAdminUser(client ->
      client.buildConfigs().inNamespace(NAMESPACE).withName("ophicleide-web").instantiate(new BuildRequestBuilder()
        .withNewMetadata()
        .withName("ophicleide-web")
        .endMetadata()
        .build())
    );

    openshift.withAdminUser(client ->
      client.buildConfigs().inNamespace(NAMESPACE).withName("ophicleide-training").instantiate(new BuildRequestBuilder()
        .withNewMetadata()
        .withName("ophicleide-training")
        .endMetadata()
        .build())
    );

    // Wait for builds to complete
    BooleanSupplier successCondition = () -> openshift.getBuilds().stream().filter(
      build -> build.getStatus().getPhase().equals("Complete")).count() == openshift.getBuilds().size();

    BooleanSupplier failCondition = () -> openshift.getBuilds().stream().filter(
      build -> build.getStatus().getPhase().equals("Cancelled") || build.getStatus().getPhase().equals("Failed"))
      .count() > 0;

    try {
      WaitUtil.waitFor(successCondition, failCondition, 1000L, TIMEOUT);
    } catch (InterruptedException|TimeoutException e) {
      e.printStackTrace();
    }
  }

  private static void launchApplication() {
    String ophResource = "/ophicleide/template.yaml";
    Template template = openshift.withAdminUser(client ->
      client.templates().inNamespace(NAMESPACE).load(Ophicleide.class.getResourceAsStream(ophResource)).createOrReplace()
    );

    Map<String, String> parameters = new HashMap<>();
    parameters.put("SPARK", RadConfiguration.sparkMasterURL());
    parameters.put("MONGO", RadConfiguration.mongodbURL());

    openshift.loadTemplate(template, parameters);

    WaitUtil.waitForPodsToReachRunningState("name", APP_NAME, 1);
  }


}
