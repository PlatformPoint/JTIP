package com.platformpoint.automation.tfs;

import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.XMLConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by chq-gurmits on 3/15/2016.
 */

public class TfsMapping {

    static final Logger LOGGER = LoggerFactory.getLogger(TfsMapping.class);
    public static String MAPPING_FILE;
    public static boolean TFS_INTEGRATION;

    /**
     * The URL to your Team Foundation Server, including virtual directory but
     * no collection name (like "http://server:8080/tfs").
     */
    public static String COLLECTION_URL;
    public static String COLLECTION_NAME;
    public static String PROJECT_NAME;


    public static List<TestPlan> TESTPLANS = new ArrayList<TestPlan>();
    public static String HTTP_PROXY_URL = null;
    public static String HTTP_PROXY_USERNAME = "";
    public static String HTTP_PROXY_PASSWORD = "";
    /**
     * The name of an existing build definition.
     */
    public static String BUILD_DEFINITION_NAME = "";
    /**
     * Set this to a writable UNC share (like "\\server\share\drops") where
     * build results can be copied.
     */
    public static String BUILD_DROP_LOCATION = "";

    public static String LOG_DROP_LOCATION = "";
    public static String LOG_FILTER = "";





    /**
     * @param automatedTestCase
     * @return
     * @throws Exception
     */
    public static long getTestCaseID(String automatedTestCase) throws Exception {
        long testCaseID = 0;
        XMLConfiguration config = new XMLConfiguration(TfsMapping.MAPPING_FILE);

        HierarchicalConfiguration sub = config.configurationAt("TestMappings");

        for (int ctr = 0; ctr < sub.getRootNode().getChildren().size(); ctr++) {
            HierarchicalConfiguration planSub = config.configurationAt("TestMappings.TestMapping(" + ctr + ")");

            // Iterate over all test cases within the test plan
            int decrement = 0;
            for (int testcasectr = 0; testcasectr < planSub.getRootNode().getChildren().size(); testcasectr++) {
                // LOGGER.info("DEBUG -- planSub.getRootNode().getChild(testcasectr).getName().toLowerCase() = "+planSub.getRootNode().getChild(testcasectr).getName().toLowerCase());
                if (!(planSub.getRootNode().getChild(testcasectr).getName().toLowerCase().equals("testcase"))) {
                    //LOGGER.info("DEBUG --Inside Continue");
                    decrement++;
                    continue;
                }
                //   LOGGER.info("Debugg -- Test Case Id = " + planSub.getString("TestCase(0)[@id]"));
                //  LOGGER.info("Debugg -- Test Case Id = " + planSub.getString("TestCase(" + (testcasectr - decrement) + ")[@id]"));
                String _testCaseId = planSub.getString("TestCase(" + (testcasectr - decrement) + ")[@id]");
                //  LOGGER.info("Debug : -- Test Case Id = " + _testCaseId);
                testCaseID = Long.parseLong(planSub.getString("TestCase(" + (testcasectr - decrement) + ")[@id]"));
                String configuredAutomatedTest = planSub.getString("TestCase(" + (testcasectr - decrement) + ")[@automatedTest]");

                LOGGER.info("Test Case Id = " + testCaseID);
                LOGGER.info("configured Automated Test Name = " + configuredAutomatedTest);
                LOGGER.info("Required Automated Test Name = " + automatedTestCase);

                if (automatedTestCase.equals(configuredAutomatedTest)) {
                    return testCaseID;
                } else {
                    testCaseID = 0;
                }
            }
        }
        if (testCaseID == 0) {
            LOGGER.error("Required Automated Test  [ " + automatedTestCase + " ] is not configured in the TFS Mapping file [" + TfsMapping.MAPPING_FILE + " ]");
        }
        return testCaseID;
    }


    /**
     * @param automatedTestCase
     * @return
     * @throws Exception
     */
    public static long getTestPlanID(String automatedTestCase) throws Exception {
        long testPlanID = 0;
        int mapping = 0;
        XMLConfiguration config = new XMLConfiguration(TfsMapping.MAPPING_FILE);

        HierarchicalConfiguration sub = config.configurationAt("TestMappings");

        for (int ctr = 0; ctr < sub.getRootNode().getChildren().size(); ctr++) {
            testPlanID = Long.parseLong(sub.getString("TestMapping(" + ctr + ").TestPlan[@testPlanID]"));
            HierarchicalConfiguration planSub = config.configurationAt("TestMappings.TestMapping(" + ctr + ")");

            // Iterate over all test cases within the test plan
            int decrement = 0;
            for (int testcasectr = 0; testcasectr < planSub.getRootNode().getChildren().size(); testcasectr++) {
                // LOGGER.info("DEBUG -- planSub.getRootNode().getChild(testcasectr).getName().toLowerCase() = "+planSub.getRootNode().getChild(testcasectr).getName().toLowerCase());
                if (!(planSub.getRootNode().getChild(testcasectr).getName().toLowerCase().equals("testcase"))) {
                    //LOGGER.info("DEBUG --Inside Continue");
                    decrement++;
                    continue;
                }

                // LOGGER.info("Debugg -- Test Case Id = " + planSub.getString("TestCase(" + (testcasectr - decrement) + ")[@id]"));
                String _testCaseId = planSub.getString("TestCase(" + (testcasectr - decrement) + ")[@id]");
                //  LOGGER.info("Debug : -- Test Case Id = " + _testCaseId);
                long testCaseID = Long.parseLong(planSub.getString("TestCase(" + (testcasectr - decrement) + ")[@id]"));
                String configuredAutomatedTest = planSub.getString("TestCase(" + (testcasectr - decrement) + ")[@automatedTest]");

                LOGGER.info("Test Case Id = " + testCaseID);
                //LOGGER.info("configured Automated Test Name = " + configuredAutomatedTest);
                // LOGGER.info("Required Automated Test Name = " + automatedTestCase);

                if (automatedTestCase.equals(configuredAutomatedTest)) {
                    // LOGGER.info("Debugg 22 -- Test Plan Id = " + testPlanID);
                    mapping = 1;
                    return testPlanID;
                } else {
                    mapping = 0;
                }
            }
        }
        if (mapping == 0) {
            LOGGER.error("Required Automated Test  [ " + automatedTestCase + " ] is not configured in the TFS Mapping file [" + TfsMapping.MAPPING_FILE + " ]");
        }
        return testPlanID;
    }


    /**
     * This is used only for unit testing purpose
     *
     * @throws Exception
     */
    public static void getMapping() throws Exception {
        // Read the Mapping File and populate TFS_INTEGRATION
        XMLConfiguration config = new XMLConfiguration(TfsMapping.MAPPING_FILE);
        TFS_INTEGRATION = Boolean.valueOf(config.getString("TestRun[@integrate]"));
        LOGGER.debug("TFS Integration status = " + TFS_INTEGRATION);
        COLLECTION_URL = config.getString("Collection[@url]");
        COLLECTION_NAME = config.getString("Collection[@name]");
        PROJECT_NAME = config.getString("Project[@name]");


        LOGGER.info("collection URL = " + COLLECTION_URL);
        LOGGER.info("collection name = " + COLLECTION_NAME);
        LOGGER.info("Project name = " + PROJECT_NAME);
    }


    /**
     * This Method reads the mapping file and create a test run in TFS
     * @throws Exception
     */
    public static void generateMapping() throws Exception {
        // Read the Mapping File and populate all class variables

        XMLConfiguration config = new XMLConfiguration(TfsMapping.MAPPING_FILE);
        COLLECTION_URL = config.getString("Collection[@url]");
        COLLECTION_NAME = config.getString("Collection[@name]");
        PROJECT_NAME = config.getString("Project[@name]");

        //Logs file location
        LOG_DROP_LOCATION = config.getString("TestLog[@dir]");
        LOG_FILTER = config.getString("TestLog[@filter]");

        LOGGER.info("collection URL = " + COLLECTION_URL);
        LOGGER.info("collection name = " + COLLECTION_NAME);
        LOGGER.info("Project name = " + PROJECT_NAME);

        LOGGER.debug("Test Results Log directory = " + LOG_DROP_LOCATION);
        LOGGER.debug("Test Results Logs filter criteria  = " + LOG_FILTER);
        TfsUtil.endpoint = (TfsMapping.COLLECTION_URL + "/" + TfsMapping.COLLECTION_NAME + "/" + TfsMapping.PROJECT_NAME).replace(" ", "%20");

        HierarchicalConfiguration sub = config.configurationAt("TestMappings");
        LOGGER.info("Number of Test Plans = " + sub.getRootNode().getChildren().size());
        long testPlanId = 0;
        long testCaseID = 0;
        String automatedTest = "";
        for (int ctr = 0; ctr < sub.getRootNode().getChildren().size(); ctr++) {
            testPlanId = Long.parseLong(sub.getString("TestMapping(" + ctr + ").TestPlan[@testPlanID]"));
            LOGGER.info("Test Plan Id = " + testPlanId);
            Map<String, String> _automatedMap = new HashMap<String, String>();

            Map<String, String> _resultMap = new HashMap<String, String>();


            // populate automatedMap
                /* <TestMapping>
                <TestPlan testPlanID="496"/>
                <TestCase ID="500" automatedTest="logTFSResults_test_001"/>
                <TestCase ID="501" automatedTest="logTFSResults_test_002"/>
                */
            HierarchicalConfiguration planSub = config.configurationAt("TestMappings.TestMapping(" + ctr + ")");
            LOGGER.info("Number of test cases in Test Plan with Id [ " + testPlanId + " ] = " + (planSub.getRootNode().getChildren().size() - 1));

            // Iterate over all test cases within the test plan
            //for(int testcasectr=0;testcasectr<=sub.getRootNode().getChildren().size();testcasectr++)

            int decrement = 0;
            for (int testcasectr = 0; testcasectr < planSub.getRootNode().getChildren().size(); testcasectr++) {
                // LOGGER.info("DEBUG -- planSub.getRootNode().getChild(testcasectr).getName().toLowerCase() = "+planSub.getRootNode().getChild(testcasectr).getName().toLowerCase());
                if (!(planSub.getRootNode().getChild(testcasectr).getName().toLowerCase().equals("testcase"))) {
                    //LOGGER.info("DEBUG --Inside Continue");
                    decrement++;
                    continue;
                }
                //LOGGER.info("Debugg -- Test Case Id = " + planSub.getString("TestCase(0)[@id]"));
                // LOGGER.info("Debugg -- Test Case Id = " + planSub.getString("TestCase("+(testcasectr-decrement)+")[@id]"));
                String _testCaseId = planSub.getString("TestCase(" + (testcasectr - decrement) + ")[@id]");
                //  LOGGER.info("Debug : -- Test Case Id = " + _testCaseId);
                testCaseID = Long.parseLong(planSub.getString("TestCase(" + (testcasectr - decrement) + ")[@id]"));
                automatedTest = planSub.getString("TestCase(" + (testcasectr - decrement) + ")[@automatedTest]");
                LOGGER.info("Test Case Id = " + testCaseID);
                LOGGER.info("Automated Test Name = " + automatedTest);
                _automatedMap.put(String.valueOf(testCaseID), automatedTest);
                _resultMap.put(String.valueOf(testCaseID), "UNEXECUTED");
            }

            TestPlan tp = new TestPlan(testPlanId, _automatedMap, _resultMap);

            // TBD- get test plan Name
               String testPlanName=TfsUtil.getTestPlanName(testPlanId);

           // String testPlanName="Junit test Plan";

            //Generate TFS test Run for each test plan
            Map<String, String> testrun = TfsUtil.createTfsTestRun(testPlanId, "[ " + PROJECT_NAME + " ] - [ "+testPlanName+" ]-[ " + testPlanId+" ]");
            String testrunDetails = testrun.get("response");
            org.json.JSONObject obj = new org.json.JSONObject(testrunDetails);

            String testRunId = obj.get("id").toString();
            //LOGGER.info(" DEBUGGGG 9999 - #@#@#@#@@#####  testRun id : " + testRunId);
            tp.testRunID = Long.parseLong(testRunId);
            // From the above response, update the testrunid for the test plan
            TESTPLANS.add(tp);
        }


    }


}



