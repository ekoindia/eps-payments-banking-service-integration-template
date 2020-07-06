package in.eko.service.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.net.ssl.HttpsURLConnection;

import org.jboss.logging.Logger;

import in.eko.service.exception.ApiRequestPostingException;
import in.eko.service.factory.Communication;
import in.eko.service.model.ConfigurationBO;
import in.eko.service.requestView.ApiRequestCommonView;
import in.eko.service.requestView.ServiceProviderRequestHeader;
import in.eko.service.util.AES256EncryptionDecription;
import in.eko.service.util.helper.ConfigurationConstant;
import in.eko.service.util.helper.StartupCache;
import in.eko.service.util.helper.TransactionConstant;
import in.eko.service.util.helper.TransactionType;
import in.eko.service.util.helper.WebInstanceConstants;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CommunicationService extends Communication {

	private static Logger logger = Logger.getLogger(CommunicationService.class);
	public static CommunicationService communicationService = null;
	public static ObjectMapper mapper = new ObjectMapper();
	static ConfigurationBO connectionTime = null;
	private int connectionTimeOutInMilliSec = 60000;
	SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss S");
	private static URL bridgeUrl;
	private static String clientId;
	private static String authKey;
	private String header;

	private static Integer source = WebInstanceConstants.DEFAILT_ID;

	public static CommunicationService getInstance() {

		if (communicationService == null) {

			synchronized (CommunicationService.class) {
				if (communicationService == null) {
					communicationService = new CommunicationService();

					clientId = StartupCache.getInstance().getConfigByKey(getKey(ConfigurationConstant.CLIENT_ID)).getConfigValue();
					authKey = StartupCache.getInstance().getConfigByKey(getKey(ConfigurationConstant.SECRET_KEY)).getConfigValue();

				}
			}
		}
		return communicationService;
	}

	@Override
	public String postRequest(String request, Integer requestType, Integer transactionMode) throws ApiRequestPostingException {
		logger.info("Calling postRequest method in " + this.getClass().getName());
		String responseData = null;
		HttpURLConnection httpConn = null;
		OutputStream out = null;
		InputStreamReader isr = null;

		ServiceProviderRequestHeader requestHeader = new ServiceProviderRequestHeader(clientId, authKey);

		try {
			
			//prepare request header if required
			/*
			 * header =
			 * AES256EncryptionDecription.getInstance().encrypt(mapper.writeValueAsString(
			 * requestHeader),
			 * StartupCache.getInstance().getConfigByKey(getKey(ConfigurationConstant.
			 * HEADER_ENCRYPTION_KEY)).getConfigValue());
			 */
			
			// TODO: do your stuff here
			header = "";

		} catch (Exception e) {
			e.printStackTrace();

			throw new ApiRequestPostingException("Exception occured while posting request to Service provider");
		}

		logger.info("Header Authorization: " + header);

		String url = StartupCache.getInstance().getConfigByKey(getUniqueKey(ConfigurationConstant.BRIDGE_URL)).getConfigValue();

		if (requestType.equals(TransactionType.MONEY_TRANSFER_TXTYPEID)) {
			if (transactionMode.equals(TransactionConstant.IMPS)) {
				url = url + TransactionConstant.MONEY_TRANSFER;
			}
		} else if (requestType.equals(TransactionType.TRANSACTION_ENQUIRY_TXTYPEID)) {
			url = url + TransactionConstant.TRANSACTION_ENQUIRY;
		}
		logger.info("Url formed : " + url);

		try {
			bridgeUrl = new URL(url);

			connectionTime = StartupCache.getInstance().getConfigByKey(getUniqueKey(ConfigurationConstant.CONNECTION_TIME_OUT));

			if (connectionTime != null && connectionTime.getConfigValue().trim().length() > 0) {
				connectionTimeOutInMilliSec = Integer.valueOf(connectionTime.getConfigValue());
			}

			// HttpsURLConnection.setDefaultHostnameVerifier(
			// SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

			if (bridgeUrl.getProtocol().equalsIgnoreCase("https")) {
				ignoreSslCertificateAuthentication();
				httpConn = (HttpsURLConnection) bridgeUrl.openConnection();
			} else {
				httpConn = (HttpURLConnection) bridgeUrl.openConnection();
			}
			request = "\"" + request + "\"";

			logger.info("Request : " + request + "\nTimeout : " + connectionTimeOutInMilliSec);

			byte[] requestByte = request.getBytes();
			httpConn.setRequestProperty("Content-Length", String.valueOf(requestByte.length));
			httpConn.setRequestProperty("Content-Type", "application/json");
			httpConn.setRequestProperty("Authentication", header);
			httpConn.setRequestMethod("POST");
			httpConn.setDoOutput(true);
			httpConn.setDoInput(true);
			httpConn.setConnectTimeout(connectionTimeOutInMilliSec);
			httpConn.setReadTimeout(connectionTimeOutInMilliSec);
			out = httpConn.getOutputStream();
			out.write(requestByte);

			logger.info("API Request sent at : " + sdf.format(new Date()));

			try {
				out.close();
			} catch (IOException e) {
				logger.info("Exception while closing output stream" + e.getMessage());
			}
		} catch (MalformedURLException exMurl) {
			logger.info(exMurl.getMessage());
			throw new ApiRequestPostingException("Exception occured while posting request to FINO");
		} catch (SocketTimeoutException stout) {
			logger.error(stout.getMessage());
			stout.printStackTrace();

			if (httpConn != null) {
				logger.info("Closing httpConn in exception blcok of output stream connection....");
				httpConn.disconnect();
			}

			throw new ApiRequestPostingException("Exception occured while posting request to FINO");
		} catch (IOException ioex) {
			logger.error(ioex.getMessage());
			ioex.printStackTrace();

			if (out != null) {
				try {
					logger.info("Closing output stream connection....");
					out.close();
				} catch (IOException ee) {
					ee.printStackTrace();
				}
			}

			if (httpConn != null) {
				logger.info("Closing httpConn in exception blcok of output stream connection....");
				httpConn.disconnect();
			}

			throw new ApiRequestPostingException("Exception occured while posting request to Service Provider");
		}

		try {
			// Read the response and write it to standard out.
			isr = new InputStreamReader(httpConn.getInputStream());
			logger.info("Service Provider Response received at : " + sdf.format(new Date()) + " Response Code : " + httpConn.getResponseCode());
			BufferedReader br = new BufferedReader(isr);
			String temp;
			StringBuilder tmpResponseb = new StringBuilder();

			while ((temp = br.readLine()) != null) {
				tmpResponseb.append(temp);
			}
			responseData = tmpResponseb.toString();
			logger.info("Service Provider Response Data : " + responseData + "\n");
			br.close();
			isr.close();
		} catch (java.net.MalformedURLException e) {
			logger.error("Error in postRequest(): Secure Service Required");
			e.printStackTrace();
		} catch (Exception e) {
			logger.error("Error in postRequest(): " + e.getMessage());
			e.printStackTrace();
		} finally {
			if (isr != null) {
				try {
					logger.info("Closing input stream connection....");
					isr.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (httpConn != null) {
				logger.info("Disconnecting httpConn in reading finally block....");
				httpConn.disconnect();
			}
		}
		return responseData;

	}

	@Override
	public String postRequest(String request, Integer requestType) throws ApiRequestPostingException {
		return null;
	}

	@Override
	public ApiRequestCommonView postRequest(ApiRequestCommonView request) {
		return null;
	}

	@Override
	public String getUniqueKey(String key) {
		return key + "_" + getSource();
	}

	private static String getKey(String key) {
		return key + "_" + CommunicationService.source;
	}

	@Override
	public Integer getSource() {
		return CommunicationService.source;
	}

	@Override
	public String postRequest(String request) throws Exception {
		return null;
	}

}
