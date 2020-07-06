package in.eko.service.service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;

import in.eko.service.requestView.TransactionRequestView;
import in.eko.service.responseView.ServiceProviderResponseData;
import in.eko.service.responseView.ServiceProviderTransactionResponseView;
import in.eko.service.util.AES256EncryptionDecription;
import in.eko.service.util.helper.ConfigurationConstant;
import in.eko.service.util.helper.StartupCache;
import in.eko.service.util.helper.WebInstanceConstants;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MockResponseService {

	private static final ObjectMapper mapper = new ObjectMapper();

	private static Logger logger = Logger.getLogger(MockResponseService.class);

	private static String[] names = {"Mr. Rohit","N Modi","Tom Cruise","Hillary Clinton","Gaurav Mallik","Kumar Saurabh","Saurabh Mullick","Selena Gomez","Deepak","Abhinav Sinha","R K LAKSHYKAR","Pappu","Donand Trump","unknown","Eko India"};

	public String setMoneyTransferAndRequeryMockResponse(TransactionRequestView trxnReq, Integer source) {
		SimpleDateFormat sdf = new SimpleDateFormat("DDMMYYYYhhmmss");
		
		String responseBody = null;
		switch (source) {

		case WebInstanceConstants.DEFAILT_ID:
			SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			ServiceProviderTransactionResponseView finoRes = new ServiceProviderTransactionResponseView();
			finoRes.setClientUniqueID(String.valueOf(trxnReq.getEkoTrxnId()));
			finoRes.setRequestID(getRandomNumber());
			finoRes.setResponseCode("0");
			finoRes.setMessageString("Sending Mock Response");
			ServiceProviderResponseData data = new ServiceProviderResponseData();
			data.setAmountRequested(trxnReq.getAmount());

			data.setChargesDeducted(trxnReq.getAmount());
			data.setTotalAmount(trxnReq.getAmount());
			data.setTransactionDateTime(sdf1.format(new Date()));
			data.setTxnId(getRandomNumber());
			//data.setTxnId("0");
			data.setActCode(StartupCache.getInstance()
					.getMockConfigByKey(getUniqueKey(ConfigurationConstant.MOCK_RESPONSE_FOR_TRANSACTION_RESPONSE_CODE, source))
					.getConfigValue());
			if (data.getActCode().equals("0")) {
				data.setBeneName(names[(int)(Math.random()*names.length)]);
			}

			try {
					finoRes.setResponseData(AES256EncryptionDecription.getInstance().encrypt(mapper.writeValueAsString(data),
							StartupCache.getInstance().getConfigByKey(getUniqueKey(ConfigurationConstant.ENCRYPTION_KEY, source)).getConfigValue()));
				responseBody = mapper.writeValueAsString(finoRes);
			} catch (JsonProcessingException e) {
				logger.info("Exception while consuming mock response");
				e.printStackTrace();
			}catch(IOException e){
				e.printStackTrace();
			}catch(GeneralSecurityException e){
				e.printStackTrace();
			}
			break;
		}
		return responseBody;
	}

	public String getRandomNumber() {
		long E2 = Math.round(Math.random() * 10);
		long E3 = Math.round(Math.random() * 100);
		long E4 = Math.round(Math.random() * 1000);
		String finalNumber = "876" + E2 + E3 + E4;
		return finalNumber;
	}

	public String getRandomBeneId() {
		long E2 = Math.round(Math.random() * 10);
		long E3 = Math.round(Math.random() * 100);
		String finalNumber = "100" + E2 + E3;
		return finalNumber;
	}

	public String getUniqueKey(String key, Integer source) {
		return key + "_" + source;
	}
}
