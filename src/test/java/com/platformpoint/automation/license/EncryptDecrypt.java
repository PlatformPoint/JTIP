package com.platformpoint.automation.license;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


 
public class EncryptDecrypt {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(EncryptDecrypt.class);
    private final String propertyFileName;
    private final String propertyKey;
    private final String isPropertyKeyEncrypted;
 
    public final String decryptedData;
 
    /**
     * The constructor does most of the work.
     * It initializes all final variables and invoke two methods
     * for encryption and decryption job. After successful job
     * the constructor puts the decrypted password in variable
     * to be retrieved by calling class.
     * 
	 * 
	 * @param pPropertyFileName /Name of the properties file that contains the password
	 * @param pUserPasswordKey	/Left hand side of the password property as key. 
	 * @param pIsPasswordEncryptedKey 	/Key in the properties file that will tell us if the password is already encrypted or not
	 * 
	 * @throws Exception
	 */
    public EncryptDecrypt(String pPropertyFileName,String pUserPasswordKey, String pIsPasswordEncryptedKey) throws Exception {
        this.propertyFileName = pPropertyFileName;
        this.propertyKey = pUserPasswordKey;
        this.isPropertyKeyEncrypted = pIsPasswordEncryptedKey;
        try {
            encryptPropertyValue();
        } catch (ConfigurationException e) {
            throw new Exception("Problem encountered during encryption process",e);
        }
        decryptedData = decryptPropertyValue();
 
    }
 
    /**
     * The method that encrypt password in the properties file. 
     * This method will first check if the password is already encrypted or not. 
     * If not then only it will encrypt the password.
     * 
     * @throws ConfigurationException
     */
    private void encryptPropertyValue() throws ConfigurationException {
        LOGGER.info("Starting encryption operation");
        LOGGER.info("Start reading properties file: "+propertyFileName);
 
        //Apache Commons Configuration 
        PropertiesConfiguration config = new PropertiesConfiguration(propertyFileName);
 
        //Retrieve boolean properties value to see if password is already encrypted or not
        String isEncrypted = config.getString(isPropertyKeyEncrypted);
        LOGGER.info("isEncrypted = "+isEncrypted);
        //Check if password is encrypted?
        if(isEncrypted.equals("false")){
            String tmpPwd = config.getString(propertyKey);
            //LOGGER.info(tmpPwd); 
            //Encrypt
            StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
            // This is a required password for Jasypt. You will have to use the same password to
            // retrieve decrypted password later. T
            // This password is not the password we are trying to encrypt taken from properties file.
            encryptor.setPassword("#$expd@11051200");
            String encryptedPassword = encryptor.encrypt(tmpPwd);
            LOGGER.info("Encryption done and encrypted password for property key ["+propertyKey+"] =" + encryptedPassword );
 
            // Overwrite password with encrypted password in the properties file using Apache Commons Configuration library
            config.setProperty(propertyKey, encryptedPassword);
            // Set the boolean flag to true to indicate future encryption operation that password is already encrypted
            config.setProperty(isPropertyKeyEncrypted,"true");
            // Save the properties file
            config.save();
        }else{
        	 LOGGER.info("User password is already encrypted.\n ");
        }
    }
 
    private String decryptPropertyValue() throws ConfigurationException {
    	 LOGGER.info("Starting decryption");
        PropertiesConfiguration config = new PropertiesConfiguration(propertyFileName);
        String encryptedPropertyValue = config.getString(propertyKey);
        //LOGGER.info(encryptedPropertyValue); 
 
        StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
        encryptor.setPassword("#$expd@11051200");
        String decryptedPropertyValue = encryptor.decrypt(encryptedPropertyValue);
        //LOGGER.info(decryptedPropertyValue); 
 
        return decryptedPropertyValue;
    }
}