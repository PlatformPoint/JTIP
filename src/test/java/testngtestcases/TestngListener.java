package testngtestcases;

import com.platformpoint.automation.tfs.TfsMapping;
import com.platformpoint.automation.tfs.TfsUtil;

import org.testng.ITestResult;
import org.testng.TestListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by chq-gurmits on 3/24/2016.
 * This class after every junit test including @After and is the best place to make a TFS API call to updated results in TFS
 */
public class TestngListener extends TestListenerAdapter {

    static final Logger LOGGER = LoggerFactory.getLogger(TestngListener.class);


    @Override
    public void onTestStart(ITestResult tr) {
        LOGGER.info(tr.getMethod().getMethodName()+" Test Started....");
    }

    @Override
    public void onTestFailure(ITestResult tr) {
        BasetestngTFSTestClass.outcome = "Failed";
        try {
            if (TfsMapping.TFS_INTEGRATION) {
                TfsUtil.updateTfsTestRun(tr.getMethod().getMethodName(), BasetestngTFSTestClass.outcome);
            }
        } catch (Exception ex) {
            LOGGER.error("Exception Caught while Updating Results in TFS " + ex.getMessage());

        }
      //  log(tr.getName()+ "--Test method failed\n");
    }

    @Override
    public void onTestSkipped(ITestResult tr) {
        BasetestngTFSTestClass.outcome = "Skipped";
        try {
            if (TfsMapping.TFS_INTEGRATION) {
                TfsUtil.updateTfsTestRun(tr.getMethod().getMethodName(), BasetestngTFSTestClass.outcome);
            }
        } catch (Exception ex) {
            LOGGER.error("Exception Caught while Updating Results in TFS " + ex.getMessage());

        }
    }

    @Override
    public void onTestSuccess(ITestResult tr) {
        BasetestngTFSTestClass.outcome = "Passed";
        try {
            if (TfsMapping.TFS_INTEGRATION) {
                TfsUtil.updateTfsTestRun(tr.getMethod().getMethodName(), BasetestngTFSTestClass.outcome);
            }
        } catch (Exception ex) {
            LOGGER.error("Exception Caught while Updating Results in TFS " + ex.getMessage());

        }
    }

}
