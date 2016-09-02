/**
 *
 */
package cucumbertestcases;

import com.platformpoint.automation.config.Config;
import com.platformpoint.automation.tfs.TfsMapping;
import com.platformpoint.automation.tfs.TfsUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author chq-gurmits
 */
public class BaseCucumberTFSTestClass {

    private static final Logger LOGGER = LoggerFactory.getLogger(BaseCucumberTFSTestClass.class);

    public static Config cfg = null;

    public  static boolean oneTimeSetUp=true;
    public  static boolean oneTimeTearDown=true;
    public long startTime = System.nanoTime();

    /**
     * @throws Exception Why Base is appened to all set up /tear up methods in Base
     *                   Class - If you have a @Before annotated method in the derived
     *                   class as well then you will need to name the base class setup
     *                   method to something different e.g. setUpBase() because if the
     *                   method names are the same only the @Before annotated method
     *                   in the derived class is called as it would be overriding the
     *                   method in the base class.
     */
    //@cucumbertestcases.annotations.BeforeSuite
    public  static void setUpBeforeClassBase() throws Exception {
        if(oneTimeSetUp) {

            //Start TFS Mapping
            TfsMapping.MAPPING_FILE = "src/test/resources/TFS_Cucumber_Mapping.xml";
            TfsMapping.getMapping();
            LOGGER.info("TfsMapping.TFS_INTEGRATION =" + TfsMapping.TFS_INTEGRATION);
            if (TfsMapping.TFS_INTEGRATION) {
                TfsMapping.generateMapping();
            }
            // End TFS Mapping
            oneTimeSetUp=false;
        }
    }

    /**
     * @throws Exception
     */
   // @cucumbertestcases.annotations.AfterSuite
    public  static void tearDownAfterClassBase() throws Exception {
        if(oneTimeTearDown) {
            if (TfsMapping.TFS_INTEGRATION) {
                for (int ctr = 0; ctr < TfsMapping.TESTPLANS.size(); ctr++) {
                    TfsUtil.setTfsTestRun(TfsMapping.TESTPLANS.get(ctr).testRunID, "Completed");
                }
            }
            oneTimeTearDown=false;
        }
    }

}
