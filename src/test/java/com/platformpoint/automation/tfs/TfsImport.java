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
import com.sun.jersey.api.client.filter.LoggingFilter;
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
import java.net.URI;
import java.net.URISyntaxException;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

/**
 * @author chq-gurmits
 */
public class TfsImport {
    static final Logger LOGGER = LoggerFactory.getLogger(TfsImport.class);
    public static String endpoint = TfsMapping.COLLECTION_URL + "/" + TfsMapping.COLLECTION_NAME + "/" + TfsMapping.PROJECT_NAME;

    public static long testExecutionTime;

    public static boolean useProxy=true;

    public static Config cfg ;

    //static ClientConfig clientConfig;
    static DefaultApacheHttpClient4Config clientConfig;
    ;
    static Client client;
    static Map<String, Object> properties;

    static {
        try {
            cfg = new Config();

            if(useProxy) {
                LOGGER.debug("PROPERTY_PROXY_URI = " + cfg.getProxyURL() + ":" + cfg.getProxyPort());
                LOGGER.debug("ROPERTY_PROXY_USERNAME = " + cfg.getProxyUser());
                LOGGER.debug("PROPERTY_PROXY_PASSWORD = " + cfg.getProxyUserPassword());
            }
                LOGGER.debug("VSTS user = " + cfg.getVSTSuser());
                LOGGER.debug("VSTS user password = " + cfg.getVSTSuserPassword());


        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {

            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                java.security.cert.X509Certificate[] chck = null;
                ;
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
            properties = client.getProperties();
            if(useProxy) {

                properties.put(ApacheHttpClient4Config.PROPERTY_PROXY_URI, cfg.getProxyURL() + ":" + cfg.getProxyPort());
                properties.put(ApacheHttpClient4Config.PROPERTY_FOLLOW_REDIRECTS,true);
                properties.put(ApacheHttpClient4Config.PROPERTY_PROXY_USERNAME, cfg.getProxyUser());
                properties.put(ApacheHttpClient4Config.PROPERTY_PROXY_PASSWORD, cfg.getProxyUserPassword());

            }

            properties.put(ClientConfig.PROPERTY_READ_TIMEOUT, 500000);
            properties.put(ClientConfig.PROPERTY_CONNECT_TIMEOUT, 500000);
            client.setReadTimeout(500000);

            HTTPBasicAuthFilter authFilter = new HTTPBasicAuthFilter(cfg.getVSTSuser(), cfg.getVSTSuserPassword());
        client.addFilter(authFilter);
        client.addFilter(new LoggingFilter());
        } catch (Exception e) {
            LOGGER.error("Exception Caught while initialising the static block in Tfsutil " + e.getMessage());
        }
    }


    /**
     *
     * @return
     * @throws URISyntaxException
     * @throws NumberFormatException
     * @throws JSONException
     */
    public static Map<String, String> getALLProjects() throws URISyntaxException, NumberFormatException, JSONException {

        //Get test plan details from
        //http://chq-issqa-ws01.corp.expeditors.com:8080/tfs/DefaultCollection/G06-POCDemo/_apis/test/plans/496?api-version=1.0

        LOGGER.debug("Displaying client Properties before the GET method: getALLProjects");
        properties = client.getProperties();

        for (Map.Entry<String, Object> entry : properties.entrySet()) {
            LOGGER.debug("Key : " + entry.getKey() + " Value : " + entry.getValue());
        }

        Map<String, String> result = new HashMap<String, String>();
        String response = "";

        ClientRequest req;
        ClientRequest.Builder builder = ClientRequest.create();
        //builder.accept( MediaType.APPLICATION_JSON_TYPE );
        builder.type(MediaType.APPLICATION_JSON);

        req = builder.build(new URI( TfsMapping.COLLECTION_URL + "/" + TfsMapping.COLLECTION_NAME  + "/_apis/projects?api-version=1.0"), "GET");
        ClientResponse clientResponse = client.handle(req);
        //The actual response in the form of String
        clientResponse.bufferEntity();
        response = clientResponse.getEntity(String.class);
        // LOGGER.info("TFS Response " + response);

        result.put("statuscode", String.valueOf(clientResponse.getStatus()));
        result.put("response", response);

        Assert.assertTrue("Error : while calling getALLProjects method ",clientResponse.getStatus()==200);
        return result;
    }






}
