package junittestcases;

import com.platformpoint.automation.tfs.TfsMapping;
import com.platformpoint.automation.tfs.TfsUtil;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by chq-gurmits on 3/24/2016.
 * This class after every junit test including @After and is the best place to make a TFS API call to updated results in TFS
 */
public class JUnitRule extends TestWatcher {

    static final Logger LOGGER = LoggerFactory.getLogger(JUnitRule.class);
   // TestWatcher is a base class for Rules that take note of the testing action, without modifying it.
   // For example, this class will keep a log of each passing and failing test:

    @Override
    public void failed(Throwable e, Description description) {
        BaseJunitTFSTestClass.outcome = "Failed";
        LOGGER.debug("Updating Junit Results in TFS :"+ BaseJunitTFSTestClass.AutomatedTestCase);
        try {
            if (TfsMapping.TFS_INTEGRATION) {
                TfsUtil.updateTfsTestRun(BaseJunitTFSTestClass.AutomatedTestCase, BaseJunitTFSTestClass.outcome);
            }
        } catch (Exception ex) {
            LOGGER.error("Exception Caught while Updating Results in TFS " + ex.getMessage());

        }
    }

    @Override
    public void succeeded(Description description) {
        BaseJunitTFSTestClass.outcome = "Passed";
        LOGGER.debug("Updating Junit Results in TFS :"+ BaseJunitTFSTestClass.AutomatedTestCase);
        try {
            if (TfsMapping.TFS_INTEGRATION) {
                TfsUtil.updateTfsTestRun(BaseJunitTFSTestClass.AutomatedTestCase, BaseJunitTFSTestClass.outcome);
            }
        } catch (Exception ex) {
            LOGGER.error("Exception Caught while Updating Results in TFS " + ex.getMessage());

        }
    }


}
