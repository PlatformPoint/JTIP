package com.platformpoint.automation.config;

import com.platformpoint.automation.license.OnlineEncryptDecrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @author chq-gurmits
 */

public class Config {

    private static final Logger LOGGER = LoggerFactory.getLogger(Config.class);
    public static String propertyFile = "system.properties";
    private static String LICENSEFILE = "license.properties";
    private Properties prop;
    private String vstsUser = null;
    private String vstsUserPassword = null;
    private String proxyURL = null;
    private int proxyPort = 8080;
    private String proxyUser = null;
    private String proxyUserPassword = null;
    private boolean isLicenseValid = false;

    public Config() throws Exception {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(propertyFile);

        if (inputStream != null) {
            getProp().load(inputStream);
        } else {
            throw new FileNotFoundException("***ERROR: property file '" + propertyFile
                    + "' not found in the classpath");
        }

        vstsUser = getProp().getProperty("vsts.alternate.authentication.user.name");
        vstsUserPassword = getProp().getProperty("vsts.alternate.authentication.user.password");
        proxyURL = getProp().getProperty("proxy.url");
        proxyPort = Integer.parseInt(getProp().getProperty("proxy.port"));
        proxyUser = getProp().getProperty("proxy.username");
        proxyUserPassword = getProp().getProperty("proxy.password");
        proxyURL = (proxyURL == null) ? null : proxyURL.trim();

        if (!isLicenseValid) {
            verifyLicense();
        }

    }


    public void verifyLicense() throws Exception {
        LOGGER.info("Licensed file path  =" + LICENSEFILE);
        InputStream inputStream = getClass().getClassLoader()
                .getResourceAsStream(LICENSEFILE);

        if (inputStream != null) {
            getProp().load(inputStream);
        } else {
            throw new FileNotFoundException("***ERROR: property file '"
                    + LICENSEFILE + "' not found in the classpath");
        }

        //Key portion of the properties the left hand side
        String userNameKey = "license.client.username";
        String clientNameKey = "license.client.name";
        String startDateKey = "license.start.date";
        String endDateKey = "license.end.date";
        String clientDomainKey = "license.client.domain";
        String licenseCountKey = "license.total.count";

        //Key in the properties file that will tell us if the password is already encrypted or not

        String isLicenseEncrypted = "is.license.encrypted";

        //Ensure that the is.license.encrypted is always set to always true.

        boolean condition = Boolean.valueOf(getProp().getProperty(isLicenseEncrypted));
        Assert.assertTrue(condition, "Error: Invalid JTIP License - License file cannot be used with UnEncrypted Values- [ is.license.encrypted=false]  should be set to true");

        //Invoke the constructor
        OnlineEncryptDecrypt app = new OnlineEncryptDecrypt();

        Map<String, String> licenseData = new HashMap<String, String>();

        String propertyKey = "client.username";
        String _data = getProp().getProperty(propertyKey);
        String _encryptedData = getProp().getProperty("license." + propertyKey);
        String _decryptedData = app.decryptPropertyValue(_encryptedData);
        licenseData.put(propertyKey, _decryptedData);
        boolean testLicense = _decryptedData.contains(_data);
        Assert.assertTrue(testLicense, "Error: Invalid JTIP License - Invalid User name in the License file: License is valid for only following users >> " + _decryptedData);


        propertyKey = "client.name";
        _data = getProp().getProperty(propertyKey);
        _encryptedData = getProp().getProperty("license." + propertyKey);
        _decryptedData = app.decryptPropertyValue(_encryptedData);
        licenseData.put(propertyKey, _decryptedData);
        testLicense = _decryptedData.contains(_data);
        Assert.assertTrue(testLicense, "Error: Invalid JTIP License - Invalid Customer / Client  name in the License file; License is valid for only the following customer  >> " + _decryptedData);

        propertyKey = "end.date";
        _data = getProp().getProperty(propertyKey);
        _encryptedData = getProp().getProperty("license." + propertyKey);
        _decryptedData = app.decryptPropertyValue(_encryptedData);
        licenseData.put(propertyKey, _decryptedData);
        testLicense = _decryptedData.contains(_data);
        Assert.assertTrue(testLicense, "Error: JTIP license expiry date does not matches the encrypted value");

        propertyKey = "client.domain";
        _data = getProp().getProperty(propertyKey);
        _encryptedData = getProp().getProperty("license." + propertyKey);
        _decryptedData = app.decryptPropertyValue(_encryptedData);
        licenseData.put(propertyKey, _decryptedData);
        testLicense = _decryptedData.contains(_data);
        Assert.assertTrue(testLicense, "Error: Invalid JTIP License - Invalid Customer / Client  Domain name in the License file");


        // Verify Logged in users
        String currentUser = System.getProperty("user.name");
        LOGGER.info("Current logged in User =" + currentUser);
        String licensedUser=licenseData.get("client.username");
        LOGGER.info("Licensed User =" + licensedUser);
        testLicense = licensedUser.toLowerCase().contains(currentUser.toLowerCase());
        Assert.assertTrue(testLicense, "Error: Invalid JTIP License - Invalid User name - Logged in User [ " + currentUser + " ]" +
                " not a valid licensed user. License is valid for only following users >> " + licensedUser);

        // Verify the license expiry date
        Date currentDate = new Date();
        LOGGER.info("Current date =" + currentDate);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dateInString =licenseData.get("end.date").split("T")[0];
        Date licenseExpiryDate = sdf.parse(dateInString);

        LOGGER.info("License Expiry Date =" + licenseExpiryDate);
        testLicense = licenseExpiryDate.after(currentDate);
        Assert.assertTrue(testLicense, "Error: Invalid JTIP License Expiry Date - License Expired on [ " + licenseExpiryDate + " ] ");


        //Verify the License Domain
        String currentDomain = System.getenv("USERDOMAIN");
        LOGGER.info("Current User Domain =" + currentDomain);
        String licensedUserDomain=licenseData.get("client.domain");
        LOGGER.info("Licensed User Domain =" + licensedUserDomain);
        testLicense = licensedUserDomain.equalsIgnoreCase(currentDomain);
        Assert.assertTrue(testLicense, "Error: Invalid JTIP License - Invalid User Domain - Logged in User domain[ " + currentDomain + " ] " +
                "not a valid licensed domain. License is valid for only following domain>> " + licensedUserDomain);


        isLicenseValid = true;

    }

    public String getVSTSuser() {
        return vstsUser;
    }

    public void setVSTSuser(String user) {
        this.vstsUser = user;
    }

    public String getVSTSuserPassword() {
        return vstsUserPassword;
    }

    public void setVSTSuserPassword(String password) {
        this.vstsUserPassword = password;
    }

    public String getProxyURL() {
        return proxyURL;
    }

    public void setProxyURL(String url) {
        this.proxyURL = url;
    }

    public int getProxyPort() {
        return proxyPort;
    }

    public void setProxyPort(int port) {
        this.proxyPort = port;
    }

    public String getProxyUser() {
        return proxyUser;
    }

    public void setProxyUser(String proxyUser) {
        this.proxyUser = proxyUser;
    }

    public String getProxyUserPassword() {
        return proxyUserPassword;
    }

    public void setProxyUserPassword(String proxyUserPassword) {
        this.proxyUserPassword = proxyUserPassword;
    }

    // public method to get the prop variable
    public Properties getProp() {
        if (prop == null) {
            prop = new Properties();
        }
        return this.prop;
    }

    // public method to set the prop variable
    public void setProp(Properties property) {
        this.prop = property;
    }

}
