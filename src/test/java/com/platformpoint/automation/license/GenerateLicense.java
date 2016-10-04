package com.platformpoint.automation.license;

import com.platformpoint.automation.config.Config;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by chq-gurmits on 8/30/2016.
 */
public class GenerateLicense {


    private static String NEWLICENSEFILE = "new04.client.license.properties";
    private Properties prop;
    private boolean isLicenseValid = false;

    public static void main(String args[]) throws Exception {
     //GenerateLicense gl= new GenerateLicense();

       //gl.createLicense();

       Config cfg = new Config();
     cfg.verifyLicense();

    }

    public void createLicense() throws Exception {

        InputStream inputStream = getClass().getClassLoader()
                .getResourceAsStream(NEWLICENSEFILE );

        if (inputStream != null) {
            getProp().load(inputStream);
        } else {
            throw new FileNotFoundException("***ERROR: property file '"
                    + NEWLICENSEFILE  + "' not found in the classpath");
        }

        //Key portion of the properties the left hand side
        String userNameKey = "license.client.username";
        String clientNameKey = "license.client.name";
        String startDateKey = "license.start.date";
        String endDateKey = "license.end.date";
        String clientDomainKey = "license.client.domain";
        String licenseCountKey = "license.total.count";

        //Key in the properties file that will tell us if the password is already encrypted or not

        String userNameEncrypted = "is.client.username.encrypted";
        String clientNameEncrypted = "is.client.name.encrypted";
        String startDateEncrypted = "is.start.date.encrypted";
        String endDateEncrypted = "is.end.date.encrypted";
        String clientDomainEncrypted = "is.client.domain.encrypted";
        String licenseCountEncrypted = "is.license.total.count";

        //Invoke the constructor
        EncryptDecrypt userNameApp = new EncryptDecrypt(NEWLICENSEFILE , userNameKey, userNameEncrypted);
        EncryptDecrypt clientNameApp = new EncryptDecrypt(NEWLICENSEFILE , clientNameKey, clientNameEncrypted);
        EncryptDecrypt startDateApp = new EncryptDecrypt(NEWLICENSEFILE , startDateKey, startDateEncrypted);
        EncryptDecrypt endDateApp = new EncryptDecrypt(NEWLICENSEFILE , endDateKey, endDateEncrypted);
        EncryptDecrypt clientDomainApp = new EncryptDecrypt(NEWLICENSEFILE , clientDomainKey, clientDomainEncrypted);
        EncryptDecrypt licenseCountApp = new EncryptDecrypt(NEWLICENSEFILE , licenseCountKey, licenseCountEncrypted);


        // source_username = getProp().getProperty("source.connection.user");

        isLicenseValid = true;

    }

    public Properties getProp() {
        if (prop == null) {
            prop = new Properties();
        }
        return this.prop;
    }
}
