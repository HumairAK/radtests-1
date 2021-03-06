package com.redhat.xpaas;

import com.redhat.xpaas.jupyter.JupyterWebUI;
import com.redhat.xpaas.logger.LogWrapper;
import com.redhat.xpaas.logger.LoggerUtil;
import com.redhat.xpaas.openshift.OpenshiftUtil;
import com.redhat.xpaas.rad.BlockChainAnalysis.api.BlockChainAnalysisSparkWebUI;
import com.redhat.xpaas.rad.BlockChainAnalysis.api.BlockChainAnalysisWebUI;
import com.redhat.xpaas.rad.BlockChainAnalysis.deployment.BlockChainAnalysisSpark;
import com.redhat.xpaas.util.Tuple;
import org.assertj.core.api.Assertions;
import org.junit.*;
import org.junit.rules.TestName;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.redhat.xpaas.rad.BlockChainAnalysis.deployment.BlockChainAnalysisSpark.deployBlockChainAnalysisSpark;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class WebUITest {

  private static BlockChainAnalysisWebUI BlockChainAnalysis;
  private static BlockChainAnalysisSparkWebUI BlockChainAnalysisSpark;
  LogWrapper log = new LogWrapper(Setup.class, "blockchain");
  private LoggerUtil logUtil = new LoggerUtil("blockchain");
  private static final OpenshiftUtil openshift = OpenshiftUtil.getInstance();

  @Rule
  public TestRule watcher = log.getLogTestWatcher();

  @BeforeClass
  public static void setUP() {
    Setup setup = new Setup();
    Tuple<BlockChainAnalysisWebUI, BlockChainAnalysisSparkWebUI> blockchainNoteBooks = setup.initializeApplications();
    BlockChainAnalysis = blockchainNoteBooks.getFirst();
    BlockChainAnalysisSpark = blockchainNoteBooks.getSecond();

//    BlockChainAnalysisSpark = BlockChainAnalysisSparkWebUI.getInstance(openshift.appDefaultHostNameBuilder("bitcoin-spark-notebook"));
//    BlockChainAnalysis = BlockChainAnalysisWebUI.getInstance(openshift.appDefaultHostNameBuilder("bitcoin-notebook"));

    BlockChainAnalysis.loadProjectByURL("blockchain.ipynb");
    BlockChainAnalysisSpark.loadProjectByURL("blockchain.snb.ipynb");
  }

  @AfterClass
  public static void tearDown(){
    Setup setup = new Setup();
    //setup.cleanUp();
  }

  @Test
  public void testAVerifyDeployment(){
    Assertions.assertThat(openshift.podRunning("app", "bitcoin-notebook")).isTrue();
    Assertions.assertThat(openshift.podRunning("app", "bitcoin-spark-notebook")).isTrue();

  }

  @Test
  public void testBJupyterNotebookVideoDisplay(){
    Assertions.assertThat(BlockChainAnalysis.getNthCodeCell(1).runCell().outputHasErrors()).isFalse();
  }

  @Test
  public void testCJupyterNotebookBasicSetup(){
    Assertions.assertThat(BlockChainAnalysis.getNthCodeCell(2).runCell().outputHasErrors()).isFalse();
  }

  @Test
  public void testDJupyterNotebookLoadingData(){
    assertCodeCellRange(3, 6);
  }

  @Test
  public void testEJupyterNotebookDataCleansing(){
    Assertions.assertThat(BlockChainAnalysis.getNthCodeCell(7).runCell().outputHasErrors()).isFalse();
  }

  @Test
  public void testFJupyterNotebookConstructingTheGraph(){
    assertCodeCellRange(8, 21);
  }

  @Test
  public void testGJupyterNotebookVisualizationOfSubGraph(){
    assertCodeCellRange(22, 28);
  }

  @Test
  public void testHSparkNotebookBasicSetup(){
    Assertions.assertThat(BlockChainAnalysisSpark.getNthCodeCell(1).runCell().outputHasErrors()).isFalse();
  }

  @Test
  public void testISparkNotebookCheckDataOnDisk(){
    assertCodeCellRangeSpark(2, 3);
  }

  @Test
  public void testJSparkNotebookLoadTheData(){
    Assertions.assertThat(BlockChainAnalysisSpark.getNthCodeCell(4).runCell().outputHasErrors()).isFalse();
  }

  @Test
  public void testKSparkNotebookNumberOfVertices(){
    Assertions.assertThat(BlockChainAnalysisSpark.getNthCodeCell(5).runCell().outputHasErrors()).isFalse();
  }

  @Test
  public void testLSparkNotebookCleanTheData(){
    assertCodeCellRangeSpark(6, 7);

  }

  @Test
  public void testMSparkNotebookNumberOfEdges(){
    Assertions.assertThat(BlockChainAnalysisSpark.getNthCodeCell(8).runCell().outputHasErrors()).isFalse();
  }

  @Test
  public void testNSparkNotebookCreatingTheGraph(){
    assertCodeCellRangeSpark(9, 10);
  }

  @Test
  public void testOSparkNotebookCalculatingThePageRank(){
    assertCodeCellRangeSpark(11, 13);
  }

  @Test
  public void testPSparkNotebookHelperFunctions(){
    assertCodeCellRangeSpark(14, 17);
  }

  private void assertCodeCellRange(int start, int end){
    for(int n = start; n <= end; n++){
      Assertions.assertThat(BlockChainAnalysis.getNthCodeCell(n).runCell().outputHasErrors()).isFalse();
    }
  }

  private void assertCodeCellRangeSpark(int start, int end){
    for(int n = start; n <= end; n++){
      Assertions.assertThat(BlockChainAnalysisSpark.getNthCodeCell(n).runCell().outputHasErrors()).isFalse();
    }
  }

}

