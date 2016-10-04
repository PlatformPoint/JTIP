/**
 *
 */
package com.platformpoint.automation.tfs;


import com.platformpoint.automation.config.Config;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientRequest;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import com.sun.jersey.client.apache4.config.ApacheHttpClient4Config;
import com.sun.jersey.client.apache4.config.DefaultApacheHttpClient4Config;


import org.codehaus.jettison.json.JSONException;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.security.cert.CertificateException;
import javax.security.cert.X509Certificate;
import javax.ws.rs.core.MediaType;
import java.io.File;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @author chq-gurmits
 */
public class TfsUtil {
    static final Logger LOGGER = LoggerFactory.getLogger(TfsUtil.class);
    public static String endpoint = "";

    public static long testExecutionTime;

    public static boolean useProxy=false;

    public static Config cfg ;

    //static ClientConfig clientConfig;
    static DefaultApacheHttpClient4Config clientConfig;
    static com.sun.jersey.api.client.Client client;

    static Map<String, Object> properties;

    static {
        try {
            cfg = new Config();
            useProxy=Boolean.parseBoolean(cfg.getProp().getProperty("proxy.enabled"));

            TfsMapping.getMapping();
            endpoint = (TfsMapping.COLLECTION_URL + "/" + TfsMapping.COLLECTION_NAME + "/" + TfsMapping.PROJECT_NAME).replace(" ", "%20");

            if(useProxy) {
                LOGGER.debug("PROPERTY_PROXY_URI = " + cfg.getProxyURL() + ":" + cfg.getProxyPort());
                LOGGER.debug("ROPERTY_PROXY_USERNAME = " + cfg.getProxyUser());
                LOGGER.debug("PROPERTY_PROXY_PASSWORD = " + cfg.getProxyUserPassword());
            }
                LOGGER.debug("VSTS user = " + cfg.getVSTSuser());
               // LOGGER.debug("VSTS user password = " + cfg.getVSTSuserPassword());


        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {

            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                java.security.cert.X509Certificate[] chck = null;
                return chck;
            }

            public void checkServerTrusted(X509Certificate[] arg0, String arg1)
                    throws CertificateException {
                // TODO Auto-generated method stub

            }

            public void checkClientTrusted(X509Certificate[] arg0, String arg1)
                    throws CertificateException {

            }

            public void checkClientTrusted(
                    java.security.cert.X509Certificate[] arg0, String arg1)
                    throws java.security.cert.CertificateException {
                // TODO Auto-generated method stub

            }

            public void checkServerTrusted(
                    java.security.cert.X509Certificate[] arg0, String arg1)
                    throws java.security.cert.CertificateException {
                // TODO Auto-generated method stub

            }
        }};

        // Install the all-trusting trust manager

            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new SecureRandom());
            HttpsURLConnection
                    .setDefaultSSLSocketFactory(sc.getSocketFactory());

            // clientConfig = new DefaultClientConfig();
            clientConfig = new DefaultApacheHttpClient4Config();

            client = Client.create(clientConfig);
            client.setReadTimeout(500000);

            HTTPBasicAuthFilter authFilter = new HTTPBasicAuthFilter(cfg.getVSTSuser(), cfg.getVSTSuserPassword());
            client.addFilter(authFilter);
            // Uncomment the following to see the detailed wired log
            //client.addFilter(new LoggingFilter());
            properties = client.getProperties();
            if(useProxy) {

                properties.put(ApacheHttpClient4Config.PROPERTY_PROXY_URI, cfg.getProxyURL() + ":" + cfg.getProxyPort());
                properties.put(ApacheHttpClient4Config.PROPERTY_FOLLOW_REDIRECTS,true);
                properties.put(ApacheHttpClient4Config.PROPERTY_PROXY_USERNAME, cfg.getProxyUser());
                properties.put(ApacheHttpClient4Config.PROPERTY_PROXY_PASSWORD, cfg.getProxyUserPassword());

            }
            properties.put(ClientConfig.PROPERTY_READ_TIMEOUT, 500000);
            properties.put(ClientConfig.PROPERTY_CONNECT_TIMEOUT, 500000);

        } catch (Exception e) {
            LOGGER.error("Exception Caught while initialising the static block in Tfsutil " + e.getMessage());
        }
    }



    /**
     * @param testPlanID
     * @return
     * @throws URISyntaxException
     * @throws NumberFormatException
     * @throws JSONException
     */
    public static Map<String, String> getTestPlan(long testPlanID) throws URISyntaxException, NumberFormatException, JSONException {

        //Get test plan details from
        //http://chq-issqa-ws01.corp.expeditors.com:8080/tfs/DefaultCollection/G06-POCDemo/_apis/test/plans/496?api-version=1.0

        LOGGER.debug("Executing method: getTestPlan");
        Map<String, String> result = new HashMap<String, String>();
        String response = "";

        ClientRequest req;
        ClientRequest.Builder builder = ClientRequest.create();
        //builder.accept( MediaType.APPLICATION_JSON_TYPE );
        builder.type(MediaType.APPLICATION_JSON);

        req = builder.build(new URI(TfsUtil.endpoint + "/_apis/test/plans/" + testPlanID + "?api-version=2.0-preview"), "GET");
        ClientResponse clientResponse = client.handle(req);
        //The actual response in the form of String
        clientResponse.bufferEntity();
        response = clientResponse.getEntity(String.class);
        // LOGGER.info("TFS Response " + response);

        result.put("statuscode", String.valueOf(clientResponse.getStatus()));
        result.put("response", response);

        Assert.assertTrue("Error : while calling getTestPlan method ",clientResponse.getStatus()==200);
        return result;
    }


    /**
     * @param testPlanID
     * @return
     * @throws URISyntaxException
     * @throws NumberFormatException
     * @throws JSONException
     */
    public static Map<String, String> getAllTestSuitesinTestPlan(long testPlanID) throws URISyntaxException, NumberFormatException, JSONException {

        //Get test plan details from
        //http://chq-issqa-ws01.corp.expeditors.com:8080/tfs/DefaultCollection/G06-POCDemo/_apis/test/plans/496?api-version=1.0
        LOGGER.info("Executing method: getAllTestSuitesinTestPlan");

        Map<String, String> result = new HashMap<String, String>();
        String response = "";
        ClientRequest req;
        ClientRequest.Builder builder = ClientRequest.create();

        //builder.accept( MediaType.APPLICATION_JSON_TYPE );
        builder.type(MediaType.APPLICATION_JSON);

        req = builder.build(new URI(TfsUtil.endpoint + "/_apis/test/plans/" + testPlanID + "/suites?api-version=2.0-preview"), "GET");
        ClientResponse clientResponse = client.handle(req);
        //The actual response in the form of String
        clientResponse.bufferEntity();
        response = clientResponse.getEntity(String.class);
        // LOGGER.info("TFS Response " + response);

        result.put("statuscode", String.valueOf(clientResponse.getStatus()));
        result.put("response", response);
        Assert.assertTrue("Error : while calling getAllTestSuitesinTestPlan method ",clientResponse.getStatus()==200);
        return result;
    }


    /**
     * @param testPlanID
     * @return
     * @throws URISyntaxException
     * @throws NumberFormatException
     * @throws JSONException
     */
    public static String getTestPlanName(long testPlanID) throws URISyntaxException, NumberFormatException, JSONException {

        //Get test plan details from
        //http://chq-issqa-ws01.corp.expeditors.com:8080/tfs/DefaultCollection/G06-POCDemo/_apis/test/plans/496?api-version=1.0
        LOGGER.info("Executing method: getTestPlanName for test plan with Id = "+testPlanID);
        String response = "";
        ClientRequest req;
        ClientRequest.Builder builder = ClientRequest.create();

        //builder.accept( MediaType.APPLICATION_JSON_TYPE );
        builder.type(MediaType.APPLICATION_JSON);
        String uri = TfsUtil.endpoint + "/_apis/test/plans/" + testPlanID + "?api-version=2.0-preview";
        LOGGER.info("getTestPlanName URI =   " + uri);
        req = builder.build(new URI(uri), "GET");

        ClientResponse clientResponse = client.handle(req);
        //The actual response in the form of String
        clientResponse.bufferEntity();
        response = clientResponse.getEntity(String.class);
        // LOGGER.info("TFS Response " + response);
        LOGGER.info("getTestPlanName response =   " + response);
        LOGGER.info("getTestPlanName response code =   " + clientResponse.getStatus());
        Assert.assertTrue("Error : while calling getTestPlanName method ",clientResponse.getStatus()==200);
        org.json.JSONObject obj = new org.json.JSONObject(response);

        String testPlanName=obj.get("name").toString();
        LOGGER.debug("TestPlan with id = [ "+testPlanID+" ] has name = ["+testPlanName+"]");
        return testPlanName;
    }

    /**
     * @param testPlanID
     * @return
     * @throws URISyntaxException
     * @throws NumberFormatException
     * @throws JSONException
     */
    public static ArrayList<String> getAllTestPoints(long testPlanID) throws URISyntaxException, NumberFormatException, JSONException {

        LOGGER.debug("Executing method: getAllTestPoints");
                //Get test plan details from
        Map<String, String> allSuites = getAllTestSuitesinTestPlan(testPlanID);
        ArrayList<String> result = new ArrayList<String>();
        String allSuitesDetails = allSuites.get("response");
        LOGGER.info(" all Suites Details : " + allSuitesDetails);
        //LOGGER.info("DEBugg000001 -Test Plan details  = " + testPlanDetails);
        org.json.JSONObject obj = new org.json.JSONObject(allSuitesDetails);

        // <TBD> Instead of rootId- get all the test suites using:
        // https://expd.visualstudio.com/DefaultCollection/ExpeditorsALM/_apis/test/plans/130/suites?api-version=1.0
        //  and for each test suite, get all tetspoints


        org.json.JSONArray jarr = obj.getJSONArray("value");
        for (int ctr = 0; ctr < jarr.length(); ctr++) {
            org.json.JSONObject obj1 = new org.json.JSONObject(jarr.get(ctr).toString());
            String suiteId = obj1.get("id").toString();
            LOGGER.info("Suite id : " + suiteId);

            //Get the test points using
            //http://chq-issqa-ws01.corp.expeditors.com:8080/tfs/DefaultCollection/G06-POCDemo/_apis/test/plans/496/suites/497/points?api-version=1.0

            String response = "";
            ClientRequest req;
            ClientRequest.Builder builder = ClientRequest.create();
            builder.type(MediaType.APPLICATION_JSON);

            String uri = TfsUtil.endpoint + "/_apis/test/plans/" + testPlanID + "/suites/" + suiteId + "/points?api-version=2.0-preview";
            LOGGER.info("All Test Point URL  " + uri);

            req = builder.build(new URI(uri), "GET");
            ClientResponse clientResponse = client.handle(req);
            //The actual response in the form of String
            clientResponse.bufferEntity();
            response = clientResponse.getEntity(String.class);
            Assert.assertTrue("Error : while calling getAllTestPoints method ",clientResponse.getStatus()==200);
            org.json.JSONObject pointsObj = new org.json.JSONObject(response);
            String count = pointsObj.get("count").toString();
            LOGGER.info("Number of PointsId    = " + count);

            pointsObj = new org.json.JSONObject(pointsObj.toString());
            org.json.JSONArray friends = pointsObj.getJSONArray("value");

            for (int index = 0; index < friends.length(); ++index) {
                org.json.JSONObject currentFriend = friends.getJSONObject(index);
                String id = currentFriend.get("id").toString();

                LOGGER.info("Point ID   = " + id);
                result.add(id);
            }
        }
        return result;
    }


    /**
     * @param testPlanId
     * @return
     * @throws URISyntaxException
     * @throws NumberFormatException
     * @throws JSONException
     */
    public static synchronized Map<String, String> createTfsTestRun(long testPlanId, String testRunName) throws URISyntaxException, NumberFormatException, JSONException {
        LOGGER.info("Creating new  TestRun for TestPlanID = " + testPlanId);

        ArrayList<String> allPointslist = getAllTestPoints(testPlanId);
        //Generate request with all points id
        LOGGER.info("allPointslist.size() =" + allPointslist.size());
        String pointIDs = "";
        if (allPointslist.size() > 0) {
            pointIDs = allPointslist.get(0);
            for (int ctr = 1; ctr < allPointslist.size(); ctr++) {

                pointIDs = pointIDs + "," + allPointslist.get(ctr);

            }
        }
        String request = "{"
                + "  \"name\": \"" + testRunName + "\","
                + "    \"plan\": { \"id\" : \"" + testPlanId
                + "\"  },"
                + "    \"pointIds\": [ " + pointIDs + " ] , "
                + "  \"isAutomated\": true " +
                "}";

        LOGGER.debug("TFS test Run Request =" + request);
        Map<String, String> result = new HashMap<String, String>();
        ClientRequest req;
        ClientRequest.Builder builder = ClientRequest.create();
        //builder.accept( MediaType.APPLICATION_JSON_TYPE );
        builder.type(MediaType.APPLICATION_JSON);
        builder.entity(request);
        req = builder.build(new URI(TfsUtil.endpoint + "/_apis/test/runs?api-version=2.0-preview"), "POST");
        ClientResponse clientResponse = client.handle(req);
        //The actual response in the form of String
        String response = clientResponse.getEntity(String.class);
        // clientResponse.bufferEntity();
        LOGGER.debug("TFS Response code =" + clientResponse.getStatus());
        LOGGER.debug("TFS Response " + response);
        result.put("statuscode", String.valueOf(clientResponse.getStatus()));
        result.put("response", response);
        Assert.assertTrue("Error : while calling createTfsTestRun method ",clientResponse.getStatus()==200);
        //

        return result;
    }


    /**
     *
     * @param testplanID
     * @return
     */
    public static long getTestRunId(long testplanID) {
        long testRunId = 0;
        for (int ctr = 0; ctr < TfsMapping.TESTPLANS.size(); ctr++) {
            long configuredTestPlanId = TfsMapping.TESTPLANS.get(ctr).testPlanID;
            if (configuredTestPlanId == testplanID) {
                LOGGER.debug("TestPlan ID = " + configuredTestPlanId);
                LOGGER.debug("TestRun ID = " + TfsMapping.TESTPLANS.get(ctr).testRunID);
                testRunId = TfsMapping.TESTPLANS.get(ctr).testRunID;
                break;

            }
        }
        if (testRunId == 0) {
            {
                LOGGER.error("Test Run ID is not yet created for Test Plan with id =  [ " + testplanID + " ]");
                LOGGER.info("Please call the method :  TfsMapping.generateMapping() to create a Test Run");
            }
        }

        return testRunId;

    }


    /**
     * @return
     */
    public static String getMachineDetails() //throws UnknownHostException
    {

        String hostDetails = "Unknown";

        try {
            InetAddress addr;
            addr = InetAddress.getLocalHost();
            hostDetails = addr.getHostName() + " - " + addr.getHostAddress();


        } catch (UnknownHostException ex) {
            LOGGER.error("Hostname can not be resolved");
            return "Exception caught " + ex.getMessage();
        }
        return hostDetails;
    }


    /**
     * @return
     */
    public static String getBuildDetails() {

        String buildDetails = "Unknown";

        try {
            /*

            Environment Variable	Description
            BUILD_NUMBER	The current build number, such as "153"
            BUILD_ID	The current build id, such as "2005-08-22_23-59-59" (YYYY-MM-DD_hh-mm-ss, defunct since version 1.597)
            BUILD_URL
            The URL where the results of this build can be found (e.g. http://buildserver/jenkins/job/MyJobName/666/)
            * */
            buildDetails = "JENKINS BUILD_NUMBER = " + System.getenv("BUILD_NUMBER");
            buildDetails = buildDetails + System.getProperty("line.separator") + "JENKINS BUILD_ID = " + System.getenv("BUILD_ID");
            buildDetails = buildDetails + System.getProperty("line.separator") + "JENKINS BUILD_URL = " + System.getenv("BUILD_URL");


        } catch (Exception ex) {
            LOGGER.error("buildDetails can not be resolved");
            return "Exception caught " + ex.getMessage();
        }
        return buildDetails;
    }

    /**
     * @param automatedTestName
     * @return
     */
    public static String getErrorMessagefromLog(String automatedTestName) {
        String errorMsg = "Please see the attached log file or Run Summary";
        LOGGER.info("Working Directory = " +
                System.getProperty("user.dir"));
        File dir = new File(TfsMapping.LOG_DROP_LOCATION);
        File[] _files = dir.listFiles();
        /*LOGGER.info("Number of log files = " + _files.length);
        File[] files = dir.listFiles((d, name) -> name.toLowerCase().contains(TfsMapping.LOG_FILTER.toLowerCase()));
        LOGGER.info("Number of log files with filter= " + files.length);
        for (File f : files) {
            LOGGER.info("Test Execution Log File = " + f.getName());
            LOGGER.info("Test Execution Log File path = " + f.getAbsolutePath());

        }
        */

        return errorMsg;

    }

    /**
     * update Tfs Test result in a test Run
     * @param automatedTestName
     * @param outcome
     * @return
     * @throws Exception
     */
    public static Map<String, String> updateTfsTestRun(String automatedTestName, String outcome) throws Exception {

        //Step 1: get the test case id and test plan id from the automated Test Name
        //Step 2: get the test runid from the test plan Id
        long testCaseID = TfsMapping.getTestCaseID(automatedTestName);
        long testPlanID = TfsMapping.getTestPlanID(automatedTestName);
        long testRunID = TfsUtil.getTestRunId(testPlanID);

        String computerName = getMachineDetails();
        String errorMessage = getErrorMessagefromLog(automatedTestName);
        String duration = String.valueOf(testExecutionTime);

        LOGGER.info("Automated Test Case = " + automatedTestName);
        LOGGER.info("          TestCase ID = " + testCaseID);
        LOGGER.info("          TestPlan ID = " + testPlanID);
        LOGGER.info("          TestRun ID = " + testRunID);
        LOGGER.info("          Machine Details = " + computerName);

        String comment = "Automated Test Case = " + automatedTestName + System.getProperty("line.separator")
                + "TestCase ID = " + testCaseID + System.getProperty("line.separator")
                + "TestPlan ID = " + testPlanID + System.getProperty("line.separator")
                + "TestRun ID = " + testRunID + System.getProperty("line.separator")
                + getBuildDetails();

        //Step 3 :update the result for test case in test run
        //Add test results to a test run
        //PATCH  https://{instance}/defaultcollection/{project}/_apis/test/runs/{run}/results?api-version={version}


        Map<String, String> result = new HashMap<String, String>();
        String response = "";
        ClientRequest req;
        ClientRequest.Builder builder = ClientRequest.create();
        builder.type(MediaType.APPLICATION_JSON);
        //Use the PATCH http verb
        builder.header("X-HTTP-Method-Override", "PATCH");

        Map<String, String> testResults = getTestResults(testRunID);
        ArrayList<String> allResults = new ArrayList<String>();
        String allResultsDetails = testResults.get("response");
        LOGGER.info(" all ResultDetails : " + allResultsDetails);
        org.json.JSONObject obj = new org.json.JSONObject(allResultsDetails);

        org.json.JSONArray jarr = obj.getJSONArray("value");
        for (int ctr = 0; ctr < jarr.length(); ctr++) {
            org.json.JSONObject obj1 = new org.json.JSONObject(jarr.get(ctr).toString());
            String testcaseDetails = obj1.get("testCase").toString();
            String resultID = obj1.get("id").toString();
            LOGGER.debug("Result ID : " + resultID);
            LOGGER.debug("test case Details : " + testcaseDetails);
            obj1 = new org.json.JSONObject(testcaseDetails);

            if (!(obj1.get("id").toString().equals(String.valueOf(testCaseID)))) {
                //LOGGER.info("Skipping iterations");
                continue;
            }
            String request = "";
            if (outcome.toLowerCase().equals("failed")) {
                request = "[{"
                        + "    \"testResult\": {" +
                        "      \"id\": " + resultID +
                        "    },"
                        + "    \"state\": \"Completed\","
                        + "    \"durationInMs\": " + duration + ","
                        + "    \"errorMessage\": \"" + errorMessage + "\","
                        + "    \"computerName\": \"" + computerName + "\","
                        + "\"automatedTestName\": \"" + automatedTestName + "\","
                        + "\"outcome\": \"" + outcome + "\","
                        + "\"comment\": \"" + comment + "\""
                        //  "    \"failureType\": \"Known Issue\""
                        + "}]";
            } else {
                request = "[{"
                        + "    \"testResult\": {" +
                        "      \"id\": " + resultID +
                        "    },"
                        + "    \"state\": \"Completed\","
                        + "    \"durationInMs\": " + duration + ","
                        + "    \"computerName\": \"" + computerName + "\","
                        + "\"automatedTestName\": \"" + automatedTestName + "\","
                        + "\"outcome\": \"" + outcome + "\","
                        + "\"comment\": \"" + comment + "\""
                        //  "    \"failureType\": \"Known Issue\""
                        + "}]";
            }

            builder.entity(request);
            req = builder.build(new URI(TfsUtil.endpoint + "/_apis/test/runs/" + testRunID + "/results?api-version=2.0-preview"), "POST");
            ClientResponse clientResponse = client.handle(req);
            //The actual response in the form of String
            response = clientResponse.getEntity(String.class);
            // clientResponse.bufferEntity();
            LOGGER.debug("TFS Response code =" + clientResponse.getStatus());
            LOGGER.debug("TFS Response " + response);
            result.put("statuscode", String.valueOf(clientResponse.getStatus()));
            result.put("response", response);
            Assert.assertTrue("Error : while calling updateTfsTestRun method ",clientResponse.getStatus()==200);
        }
        return result;
    }



    /**
     *
     * @param testRunID
     * @param status
     * @return
     * @throws Exception
     */
    public static Map<String, String> setTfsTestRun(long testRunID, String status) throws Exception {

        int testcaseID = 134;
        Map<String, String> result = new HashMap<String, String>();
        String response = "";
        ClientRequest req;
        ClientRequest.Builder builder = ClientRequest.create();
        builder.type(MediaType.APPLICATION_JSON);
        builder.header("X-HTTP-Method-Override", "PATCH");

        String request = "{\"state\": \"" + status + "\"" + "}";

        builder.entity(request);
        req = builder.build(new URI(TfsUtil.endpoint + "/_apis/test/runs/" + testRunID + "?api-version=2.0-preview"), "POST");

        ClientResponse clientResponse = client.handle(req);
        //The actual response in the form of String
        response = clientResponse.getEntity(String.class);
        // clientResponse.bufferEntity();
        LOGGER.info("TFS Response code =" + clientResponse.getStatus());
        LOGGER.info("TFS Response " + response);
        result.put("statuscode", String.valueOf(clientResponse.getStatus()));
        result.put("response", response);
        return result;
    }


    /**
     * @param testRunID
     * @return
     * @throws Exception
     */
    public static Map<String, String> getTestResults(long testRunID) throws Exception {
        Map<String, String> result = new HashMap<String, String>();
        String response = "";
        ClientRequest req;
        ClientRequest.Builder builder = ClientRequest.create();
        builder.type(MediaType.APPLICATION_JSON);
        String uri = TfsUtil.endpoint + "/_apis/test/runs/" + testRunID + "/results?api-version=2.0-preview";
        LOGGER.info("getTestResults uri = " + uri);

        req = builder.build(new URI(uri), "GET");
        ClientResponse clientResponse = client.handle(req);
        //The actual response in the form of String
        response = clientResponse.getEntity(String.class);
        // clientResponse.bufferEntity();
        LOGGER.info("Get Test Results : TFS Response code =" + clientResponse.getStatus());
        LOGGER.info("Get Test Results : TFS Response " + response);
        result.put("statuscode", String.valueOf(clientResponse.getStatus()));
        result.put("response", response);
        return result;
    }


}
