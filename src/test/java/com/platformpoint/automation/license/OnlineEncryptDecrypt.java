package com.platformpoint.automation.license;

import org.apache.commons.configuration.ConfigurationException;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class OnlineEncryptDecrypt {

	private static final Logger LOGGER = LoggerFactory.getLogger(OnlineEncryptDecrypt.class);

    public OnlineEncryptDecrypt()  {
    }
 

    /**
     * The method that encrypt password in the properties file.
     * This method will first check if the password is already encrypted or not.
     * If not then only it will encrypt the password.
     *
     * @throws ConfigurationException
     */
    public String encryptPropertyValue(String propertyValue) throws ConfigurationException {
        LOGGER.info("Starting encryption operation for propertyValue="+propertyValue);

            //Encrypt
            StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
            // This is a required password for Jasypt. You will have to use the same password to
            // retrieve decrypted password later. T
            // This password is not the password we are trying to encrypt taken from properties file.
            encryptor.setPassword("#$pago@11051200");
            String encryptedPassword = encryptor.encrypt(propertyValue);
            LOGGER.info("Encryption done and encrypted value for property : ["+propertyValue+"] =" + encryptedPassword );
           return encryptedPassword;
    }


    /**
     *
     * @param encryptedPropertyValue
     * @return
     * @throws ConfigurationException
     */
    public String decryptPropertyValue(String encryptedPropertyValue) throws ConfigurationException {
        LOGGER.info("Starting decryption");
        StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
        encryptor.setPassword("#$pago@11051200");
        String decryptedPropertyValue = encryptor.decrypt(encryptedPropertyValue);
        //LOGGER.info(decryptedPropertyValue);

        return decryptedPropertyValue;
    }
}