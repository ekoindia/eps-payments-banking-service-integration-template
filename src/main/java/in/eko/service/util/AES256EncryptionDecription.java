package in.eko.service.util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;

import org.apache.commons.ssl.OpenSSL;
import org.apache.log4j.Logger;

public class AES256EncryptionDecription {

	private static Logger logger = Logger.getLogger(AES256EncryptionDecription.class);

	private static AES256EncryptionDecription encryptionDecription = null;

	public static AES256EncryptionDecription getInstance() {

		if (encryptionDecription == null) {
			synchronized (AES256EncryptionDecription.class) {
				encryptionDecription = new AES256EncryptionDecription();
			}
		}
		return encryptionDecription;
	}

	public String encrypt(String strToEncrypt, String key) throws IOException, GeneralSecurityException {
	
		    logger.info("String to encrypt : "+strToEncrypt+"\t Key : "+key);
			

			byte[] data = OpenSSL.encrypt("AES256", key.toCharArray(), strToEncrypt.getBytes(StandardCharsets.UTF_8));
			
			String encryptedString = new String(data); 
			
			String sp[]=encryptedString.split("\\r?\\n");
			
			encryptedString = "";
			for(String s: sp){
				encryptedString+=s;
			}
			
			logger.info("Final String : "+encryptedString);
			
			return encryptedString;

	}

	public String decrypt(String strToDecrypt, String key) {
		try {

			byte[] data = OpenSSL.decrypt("AES256", key.toCharArray(), strToDecrypt.getBytes(StandardCharsets.UTF_8));

			return new String(data);

		} catch (Exception e) {

			e.printStackTrace();
		}
		return null;
	}

}
