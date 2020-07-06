/**
 * 
 */
package in.eko.service.factory;

import java.security.SecureRandom;
import java.security.cert.CertificateException;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import in.eko.service.exception.ApiRequestPostingException;


public abstract class Communication implements BaseCommunicationInterface{
	
	public void ignoreSslCertificateAuthentication() throws ApiRequestPostingException {
		TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
			public java.security.cert.X509Certificate[] getAcceptedIssuers() {
				return null;
			}

			@Override
			public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType)
					throws CertificateException {
			}

			@Override
			public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType)
					throws CertificateException {
			}
		} };

		try {
			SSLContext sslContext = SSLContext.getInstance("SSL");
			sslContext.init(null, trustAllCerts, new SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
		} catch (Exception e) {
			e.printStackTrace();
			throw new ApiRequestPostingException("Error occured while settingSSL Socket Foctory");
		}
	}

	public abstract String postRequest(String requestBody, Integer requestTypeId, Integer transactionMode) throws ApiRequestPostingException;

}
