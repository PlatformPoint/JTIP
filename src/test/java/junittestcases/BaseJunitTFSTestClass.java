/**
 *
 */
package junittestcases;

import com.platformpoint.automation.config.Config;
import com.platformpoint.automation.tfs.TfsMapping;
import com.platformpoint.automation.tfs.TfsUtil;
import org.junit.*;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author chq-gurmits
 */
public class BaseJunitTFSTestClass extends JUnitRule {

    private static final Logger LOGGER = LoggerFactory.getLogger(BaseJunitTFSTestClass.class);
    public static String outcome;
    public static Config cfg = null;
    public static String AutomatedTestCase;
    public static boolean oneTimeSetUp=true;
    public static boolean oneTimeTearDown=true;

    @Rule
    public TestName testName = new TestName();
    //The following will be called after every test including @After and is the best place to make a TFS PAI call to updated results in TFS
    @Rule
    public JUnitRule junitrule = new JUnitRule();
    long startTime = System.nanoTime();

    /**
     * @throws Exception Why Base is appened to all set up /tear up methods in Base
     *                   Class - If you have a @Before annotated method in the derived
     *                   class as well then you will need to name the base class setup
     *                   method to something different e.g. setUpBase() because if the
     *                   method names are the same only the @Before annotated method
     *                   in the derived class is called as it would be overriding the
     *                   method in the base class.
     */



    @BeforeClass
    public static void setUpBeforeClassBase() throws Exception {
        if(oneTimeSetUp) {

            //Start TFS Mapping
            TfsMapping.MAPPING_FILE = "src/test/resources/TFS_Junit_Mapping.xml";
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
    @AfterClass
    public static void tearDownAfterClassBase() throws Exception {
        if(oneTimeTearDown) {
            if (TfsMapping.TFS_INTEGRATION) {
                for (int ctr = 0; ctr < TfsMapping.TESTPLANS.size(); ctr++) {
                    TfsUtil.setTfsTestRun(TfsMapping.TESTPLANS.get(ctr).testRunID, "Completed");
                }
            }
            oneTimeTearDown=false;
        }
    }

    /**
     * @throws Exception
     */
    @Before
    public void setUpBase() throws Exception {
        LOGGER.info(System.getProperty("line.separator"));
        LOGGER.info("*************** Starting Test Execution  **************");
        LOGGER.info("Starting test case  " + testName.getMethodName());
        AutomatedTestCase = testName.getMethodName();
        LOGGER.info("Automated Test Case = " + AutomatedTestCase);
        LOGGER.info(System.getProperty("line.separator"));
    }

    /**
     * @throws Exception
     */
    @After
    public void tearDownBase() throws Exception {
        long elapsedTime = System.nanoTime() - startTime;
        LOGGER.info(System.getProperty("line.separator"));
        LOGGER.info("*************** EXECUTION TIME  **************");
        LOGGER.info("Total execution time to execute test  (in seconds) : " + (elapsedTime / 1000000000.000));
        LOGGER.info(System.getProperty("line.separator"));
        TfsUtil.testExecutionTime = elapsedTime;
    }
}
