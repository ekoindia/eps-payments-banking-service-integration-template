package in.eko.service.service;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;
import org.hibernate.exception.ConstraintViolationException;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.SchedulerException;
import org.quartz.impl.matchers.GroupMatcher;

import in.eko.service.exception.ApiRequestPostingException;
import in.eko.service.exception.TransactionPostingNotAllowed;
import in.eko.service.factory.Communication;
import in.eko.service.factory.CommunicationFactory;
import in.eko.service.hibernate.HibernateDataAccess;
import in.eko.service.model.CallBackRequestBO;
import in.eko.service.model.ConfigurationBO;
import in.eko.service.model.MessageLogBO;
import in.eko.service.model.MockConfigBO;
import in.eko.service.model.ResponseCodeBO;
import in.eko.service.model.TaskConfigurationBO;
import in.eko.service.model.TransactionBO;
import in.eko.service.persistence.TransactionPersistence;
import in.eko.service.requestView.ServiceProviderTransactionRequestView;
import in.eko.service.requestView.ServiceProviderTransactionEnquiryRequestView;
import in.eko.service.requestView.TransactionRequestView;
import in.eko.service.responseView.CallbackResponseView;
import in.eko.service.responseView.ServiceProviderResponseData;
import in.eko.service.responseView.ServiceProviderTransactionResponseView;
import in.eko.service.responseView.TransactionResponseView;
import in.eko.service.service.CallbackService;
import in.eko.service.service.MockResponseService;
import in.eko.service.service.TransactionService;
import in.eko.service.tasks.TaskSchedulerConstants;
import in.eko.service.tasks.TaskSchedulerService;
import in.eko.service.util.AES256EncryptionDecription;
import in.eko.service.util.helper.ConfigurationConstant;
import in.eko.service.util.helper.StartupCache;
import in.eko.service.util.helper.TransactionConstant;
import in.eko.service.util.helper.TransactionType;
import in.eko.service.util.helper.WebInstanceConstants;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ServiceProviderTransactionService extends TransactionService {

	private static Logger logger = Logger.getLogger(ServiceProviderTransactionService.class);

	private MessageLogBO messagelog;

	private static final ObjectMapper mapper = new ObjectMapper();

	private static Integer source = WebInstanceConstants.DEFAILT_ID;

	private LocalDateTime startTime;

	private LocalDateTime endTime;

	private static volatile AtomicInteger continuousTimeout = new AtomicInteger();

	private static boolean timeOutReduced = false;

	public Integer getSource() {
		return ServiceProviderTransactionService.source;
	}

	Communication communication = new CommunicationFactory().getCommunicationFactory(WebInstanceConstants.DEFAULT);

	@Override
	public TransactionResponseView sendMoney(TransactionRequestView trxnReq) {

		logger.info("Inside sendMoney method of TransactionService class" + trxnReq.getEkoTrxnId() + "\t "
				+ trxnReq.getRequestType());
		TransactionResponseView response = null;
		TransactionBO transactionBO = null;
		this.startTime = LocalDateTime.now();
		try {
			startTransaction();
			try {
				transactionBO = new TransactionBO(trxnReq, getSource());
				transactionBO.setSource(getSource());
				transactionBO.save();
				HibernateDataAccess.commitTransaction();
				HibernateDataAccess.startTransaction();

			} catch (ConstraintViolationException e) {
				logger.info("Exception while flush..." + e.getMessage() + "\t" + e.getCause().getMessage());
				response = new TransactionResponseView();
				response.setStatus(TransactionConstant.SUSPICIOUS_RESPONSE_RECEIVED);
				response.setErrorReason("duplicate tid");
				return response;
			} catch (Exception e) {
				response = new TransactionResponseView();
				response.setStatus(TransactionConstant.FAIL);
				response.setDescription("Exception while commiting transaction");
				return response;
			} finally {
				HibernateDataAccess.closeSessionTL();
			}

			response = postRequestToServiceProvider(trxnReq);
			startTransaction();

			transactionBO.setStatus(response.getStatus());

			transactionBO.setResponsecode(response.getResponseCode());

			if (response.getRrn() != null && response.getRrn().trim().length() > 0) {
				transactionBO.setTrackingNumber(response.getRrn());
			}

			transactionBO.setUtrNumber(response.getUtrNumber());

			transactionBO.setBankTrxnId(response.getBankTrxnId());
			transactionBO.save();
			HibernateDataAccess.commitTransaction();

			if (transactionBO.getResponsecode() != null
					&& !transactionBO.getResponsecode().equalsIgnoreCase(TransactionConstant.READ_TIME_OUT)) {
				sendRealTimeCallback(response);
			}

		} catch (Exception e) {
			logger.info("Inside exception block of  sendMoney....");
			e.printStackTrace();
			logger.info(e.getClass() + " : " + e.getMessage() + " : " + e.getCause(), e);
		} finally {
			logger.info("Inside finally block of  sendMoney....");
			HibernateDataAccess.closeSessionTL();
		}
		return response;
	}

	@Override
	public TransactionResponseView analyzeResponse(TransactionResponseView responseView, String requestType) {

		ResponseCodeBO responseCodeBo = StartupCache.getInstance()
				.getResponseCodeBoByCode(getUniqueKey(responseView.getResponseCode()));

		if (responseCodeBo != null) {
			if (requestType.equals(TransactionConstant.MONEY_TRANSFER_TYPE)) {
				responseView.setStatus(responseCodeBo.getStatus());
				responseView.setDescription(responseCodeBo.getDescription());
				sendAlertIfRequired(responseCodeBo);
			} else if (requestType.equals(TransactionConstant.ENQUIRY_TYPE)) {
				responseView.setStatus(responseCodeBo.getEnquiryStatus());
				responseView.setDescription(responseCodeBo.getDescription());
			} else {
				responseView.setStatus(TransactionConstant.SUSPICIOUS_RESPONSE_RECEIVED);
				responseView.setDescription("Invalid Reqest Type" + requestType);
			}
		} else {
			responseView.setStatus(TransactionConstant.SUSPICIOUS_RESPONSE_RECEIVED);
			responseView.setDescription("Respose code received not define in our system");
			//TODO
			//Some alert mechanism has to be designed
		}
		return responseView;
	}

	private void sendAlertIfRequired(ResponseCodeBO responseCodeBo) {

		try {
			//do your stuff here
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public String changeMockResponseFlag(Integer flag) {
		try {
			startTransaction();
			if (flag != 0 && flag != 1) {
				return "Invalid Value : Expected Values (0,1)";
			}

			ConfigurationBO mock = StartupCache.getInstance()
					.getConfigByKey(getUniqueKey(ConfigurationConstant.MOCKED_RESPONSE_TRXN_AND_REQUERY));

			if (mock.getConfigValue().equals(String.valueOf(flag))) {
				return "Mock Flag Has been changed";
			}

			mock.setConfigValue(String.valueOf(flag));

			StartupCache.getInstance().getConfigurationMap()
					.put(getUniqueKey(ConfigurationConstant.MOCKED_RESPONSE_TRXN_AND_REQUERY), mock);

			mock.save();
			commitTransaction();
		} catch (Exception e) {
			e.printStackTrace();
			return "Exception Occured";
		} finally {
			closeHibernateSession();
		}
		return "Successfully Updated";
	}

	@Override
	public String changeMockResponse(String responseToset, String key) {
		try {
			startTransaction();
			int invalid = 0;
			MockConfigBO transactionMock = null;
			switch (key) {
			case ConfigurationConstant.MOCK_RESPONSE_FOR_TRANSACTION_RESPONSE_CODE:
				transactionMock = StartupCache.getInstance().getMockConfigByKey(getUniqueKey(key));
				if (transactionMock.getConfigValue().equals(responseToset)) {
					return "Mock response is Already set to " + responseToset + " For " + key;
				}
				transactionMock.setConfigValue(responseToset);
				StartupCache.getInstance().getMockConfigMap().put(getUniqueKey(key), transactionMock);
				break;

			default:
				invalid = 1;
				break;
			}

			if (invalid == 1) {
				return "Invalid Key";
			}

			transactionMock.save();
			commitTransaction();
		} catch (Exception e) {
			e.printStackTrace();
			return "Exception Occured";
		} finally {
			closeHibernateSession();
		}
		return "Bingo Mocked Response has been set to " + responseToset + " For " + key;
	}

	@Override
	public CallbackResponseView doCallBackRequest(String ekoTrxnId) {
		logger.info("Inside doCallBackRequest in PaytmTransactionService .. TID " + ekoTrxnId);
		CallbackResponseView response = new CallbackResponseView();
		try {
			startTransaction();
			TransactionBO transaction = TransactionPersistence.getInstance().getTransactionByEkoTrxnId(ekoTrxnId,
					getSource());

			if (transaction != null) {
				if (transaction.getCallbackStatus() != TransactionConstant.CALLBACK_REQUIRED) {

					CallBackRequestBO callbackRequest = new CallBackRequestBO(transaction.getEkoTrxnId(), new Date(),
							TransactionConstant.CALLBACK_REQUIRED, getSource());
					callbackRequest.save();
					HibernateDataAccess.commitTransaction();
					response.setStatus(TransactionConstant.SUCCESS);
					response.setDescription("Callback request received");
				} else {
					response.setStatus(TransactionConstant.SUCCESS);
					response.setDescription("Callback Already requested");
				}
			} else {
				CallBackRequestBO callbackRequest = new CallBackRequestBO(ekoTrxnId, new Date(),
						TransactionConstant.CALLBACK_REQUIRED, getSource());
				callbackRequest.save();
				HibernateDataAccess.commitTransaction();
				response.setStatus(TransactionConstant.SUCCESS);
				response.setDescription("Callback request received");
			}
		} catch (Exception e) {
			e.printStackTrace();
			response.setStatus(TransactionConstant.FAIL);
			response.setDescription("Problem while processing callback request");
		} finally {
			HibernateDataAccess.closeSessionTL();
		}
		return response;
	}

	@Override
	public String updateTaskStatus(Integer action, String taskKey) {
		String result = "NA";

		try {
			startTransaction();
			TaskConfigurationBO task = StartupCache.getInstance().getTaskByTaskId(taskKey);

			if (task != null && ((action == TaskSchedulerConstants.ACTIVE
					&& task.getStatus().intValue() == TaskSchedulerConstants.INACTIVE)
					|| (action == TaskSchedulerConstants.INACTIVE
							&& task.getStatus().intValue() == TaskSchedulerConstants.ACTIVE))) {
				if (action == TaskSchedulerConstants.ACTIVE) {
					result = activateTask(task);
				}

				if (action == TaskSchedulerConstants.INACTIVE) {
					result = inactivateTask(result, task);
				}
			} else if (task != null) {
				result = "Task is already " + (action == TaskSchedulerConstants.ACTIVE ? "Active" : "Inactive");
			} else {
				result = "Inavlid Request. Please check Task id or Action value";
			}
		} catch (Exception e) {
			e.printStackTrace();
			rollBackTransaction();
			result = "Unable to process request, please try again";
		} finally {
			HibernateDataAccess.closeSessionTL();
		}
		return result;
	}

	private String inactivateTask(String result, TaskConfigurationBO task) {
		try {
			Set<JobKey> key = TaskSchedulerService.getInstance().getScheduler()
					.getJobKeys(GroupMatcher.jobGroupEquals("group1"));
			for (JobKey job : key) {
				if (job.getName().equalsIgnoreCase(task.getTaskName())) {
					logger.info("Job Name " + job.getName() + "\t Group " + job.getGroup());
					boolean b = TaskSchedulerService.getInstance().getScheduler()
							.deleteJob(new JobKey(job.getName(), "group1"));
					if (b) {
						task.setStatus(TaskSchedulerConstants.INACTIVE);
						task.save();
						HibernateDataAccess.commitTransaction();
						StartupCache.getInstance().getTaskConfigurationMap().put(task.getUniqueKey(), task);
						result = "Task Disabled successfully";
					} else {
						result = "Unable to Inactive Task, please try again.";
					}
				}
			}

		} catch (SchedulerException e) {
			e.printStackTrace();
			result = "Unable to Inactive Task, please try again.";
		} catch (Exception e) {
			e.printStackTrace();
			rollBackTransaction();
			result = "Unable to Inactive Task";
		}
		return result;
	}

	private String activateTask(TaskConfigurationBO task) {
		String result;
		JobDetail job = TaskSchedulerService.getInstance().getJobDetail(task.getTaskId());
		try {
			TaskSchedulerService.getInstance().scheduleJob(job, task);
			result = "Job activated successfully";
			task.setStatus(TaskSchedulerConstants.ACTIVE);
			task.save();
			HibernateDataAccess.commitTransaction();
			StartupCache.getInstance().getTaskConfigurationMap().put(task.getUniqueKey(), task);
		} catch (SchedulerException e) {
			logger.info("Unable to schedule job : " + task.getTaskName());
			e.printStackTrace();
			result = "Unable to schedule job";
		} catch (Exception e) {
			rollBackTransaction();
			result = "Unable to schedule job";
		}
		return result;
	}

	@Override
	public String getUniqueKey(String key) {
		return key + "_" + getSource();
	}

	public TransactionResponseView postRequestToServiceProvider(TransactionRequestView requestView) throws Exception {

		logger.info("Inside postRequestToServiceProvider of ServiceProviderTransactionService...");

		TransactionResponseView responseView = new TransactionResponseView();
		//prepare request
		ServiceProviderTransactionRequestView request = formRequest(requestView);
		ServiceProviderTransactionResponseView response = null;
		String requestBody = mapper.writeValueAsString(request);

		String responseBody = null;

		try {
			startTransaction();
			try {
				this.messagelog = new MessageLogBO();
				logger.info("Request body :" + requestBody);
				this.messagelog.setRequest(requestBody);
				this.messagelog.setEkoTrxnId(requestView.getEkoTrxnId());
				this.messagelog.setRequestedAt(new Date());
				this.messagelog.setTypeId(requestView.getRequestTypeId());
				this.messagelog.save();
				HibernateDataAccess.commitTransaction();
			} catch (Exception e1) {
				e1.printStackTrace();
				rollBackTransaction();
				logger.info("Exception while commiting message log before sending remittance request..");
				responseView.setStatus(TransactionConstant.SUSPICIOUS_RESPONSE_RECEIVED);
				responseView.setResponseCode(TransactionConstant.EXCEPTION_NOT_POSTED_TO_SERVICE_PROVIDER);
				responseView.setDescription("Not posted to Service Provider");
				return responseView;
			} finally {
				HibernateDataAccess.closeSessionTL();
			}

			ConfigurationBO mock = StartupCache.getInstance()
					.getConfigByKey(getUniqueKey(ConfigurationConstant.MOCKED_RESPONSE_TRXN_AND_REQUERY));

			if (canPostTransactions()) {
				if (mock.getConfigValue().equals(ConfigurationConstant.MOCKED)) {
					responseBody = new MockResponseService().setMoneyTransferAndRequeryMockResponse(requestView,
							getSource());
					long sleeptime = Long.parseLong(StartupCache.getInstance()
							.getConfigByKey(getUniqueKey(ConfigurationConstant.MOCK_RES_SLEEP_TIME)).getConfigValue());
					logger.info("Sleeping till :" + sleeptime + ": Seconds");
				} else {
					try {
						/*
						 * requestBody = AES256EncryptionDecription.getInstance().encrypt(requestBody,
						 * StartupCache.getInstance()
						 * .getConfigByKey(getUniqueKey(ConfigurationConstant.ENCRYPTION_KEY))
						 * .getConfigValue());
						 */
						//do your stuff here
						requestBody = "";
						
					} catch (Exception e) {
						e.printStackTrace();
						responseView.setStatus(TransactionConstant.FAIL);
						responseView.setDescription("Exception while Encrypting body ");
						return responseView;
					}
					responseBody = communication.postRequest(requestBody, requestView.getRequestTypeId(),
							requestView.getTransactionMode());
				}
			} else {
				throw new TransactionPostingNotAllowed("Transaction posting now allow this time");
			}

			if (responseBody != null && responseBody.trim().length()>1) {
				logger.info("Response Received from Service Provider : " + responseBody);
				try {
					startTransaction();
					if (responseBody.length() > 1900) {
						this.messagelog.setResponse(responseBody.substring(0, 1900));
					} else {
						this.messagelog.setResponse(responseBody);
					}
					this.messagelog.setResponseReceivedAt(new Date());
					this.messagelog.save();
					HibernateDataAccess.commitTransaction();
				} catch (Exception e1) {
					logger.info("Exception while commiting message log after sending request ..." + responseBody);
					e1.printStackTrace();
					rollBackTransaction();
				} finally {
					HibernateDataAccess.closeSessionTL();
				}

				try {

					response = mapper.readValue(responseBody, ServiceProviderTransactionResponseView.class);

					parseResponse(responseView, response);

					responseView = analyzeResponse(responseView, requestView.getRequestType());

					if (TransactionConstant.REQUEST_FAIL.equals(response.getResponseCode())) {
						responseView.setStatus(TransactionConstant.FAIL);
						responseView.setDescription(response.getDisplayMessage());
					}

					if (continuousTimeout.get() > 0) {
						continuousTimeout.set(0);
					}

				} catch (Exception e) {
					responseView.setStatus(TransactionConstant.SUSPICIOUS_RESPONSE_RECEIVED);
					responseView.setDescription("Exception while deserializing object " + responseBody);
					e.printStackTrace();
				}

			} else {
				responseView.setStatus(TransactionConstant.REQUEST_READ_TIME_OUT);
				responseView.setResponseCode(TransactionConstant.READ_TIME_OUT);
				responseView.setDescription("Read time out");
				continuousTimeout.incrementAndGet();
				logger.info("Total continuous timeout :" + continuousTimeout.get());
			}
		} catch (TransactionPostingNotAllowed e) {
			logger.info("Tranaction not posted : " + e.getMessage());
			responseView.setStatus(TransactionConstant.SUSPICIOUS_RESPONSE_RECEIVED);
			responseView.setResponseCode(TransactionConstant.EXCEPTION_NOT_POSTED_TO_SERVICE_PROVIDER);
			responseView.setDescription("Transaction will be processed soon");
		} catch (ApiRequestPostingException e) {
			e.printStackTrace();
			logger.info("Request not posted to Service ProviderTransactionService");
			responseView.setStatus(TransactionConstant.SUSPICIOUS_RESPONSE_RECEIVED);
			responseView.setResponseCode(TransactionConstant.EXCEPTION_NOT_POSTED_TO_SERVICE_PROVIDER);
			responseView.setDescription("Request not posted to Service ProviderTransactionService");
			continuousTimeout.incrementAndGet();
			logger.info("Total continuous timeout :" + continuousTimeout.get());
		} finally {
			HibernateDataAccess.closeSessionTL();
		}

		this.endTime = LocalDateTime.now();

		sendSystemAlertIfRequired(requestView);

		reduceOrIncreaseTimeout();

		return responseView;
	}

	private void parseResponse(TransactionResponseView responseView, ServiceProviderTransactionResponseView response)
			throws JsonParseException, JsonMappingException, IOException {
		// responseView.setBankTrxnId(response.get);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

		responseView.setBankTrxnId(
				response.getRequestID() != null && !response.getRequestID().trim().equals("") ? response.getRequestID()
						: null);

		responseView.setEkoTrxnId(response.getClientUniqueID());

		responseView.setDescription(response.getDisplayMessage());

		if (response.getResponseData() != null) {

			/*
			 * String responseDataString =
			 * AES256EncryptionDecription.getInstance().decrypt(response.getResponseData(),
			 * StartupCache.getInstance().getConfigByKey(getUniqueKey(ConfigurationConstant.
			 * ENCRYPTION_KEY)) .getConfigValue());
			 */
			
			// TODO: do your stuff here
			String responseDataString = "";
			logger.info("Response Data : " + responseDataString);

			ServiceProviderResponseData data = mapper.readValue(responseDataString, ServiceProviderResponseData.class);

			responseView.setResponseCode(data.getActCode());
			
			try {
				responseView.setTransactionDate(sdf.parse(data.getTransactionDateTime()));
			} catch (ParseException e) {
				e.printStackTrace();
				logger.info("Exception while setting date");
				responseView.setTransactionDate(null);
			}

			String utrNumber = extractUtrNumber(data.getTxnDescription());

			logger.info("Utr Number : " + utrNumber);

			responseView.setUtrNumber(utrNumber);

			responseView.setRrn(null != data.getTxnId() && !"".equals(data.getTxnId().trim())
					? (!data.getTxnId().equals("0") ? data.getTxnId() : null)
					: null);
		} else {
			responseView.setStatus(TransactionConstant.SUSPICIOUS_RESPONSE_RECEIVED);

			responseView.setDescription("Respose data not received");
		}

	}

	public static String extractUtrNumber(String txnDescription) {
		String utr = null;
		try {
			if (txnDescription != null && txnDescription.trim().length() > 0 && txnDescription.startsWith("NEFT")) {
				utr = txnDescription.substring(txnDescription.indexOf("/") + 1,
						txnDescription.indexOf("/", txnDescription.indexOf("/") + 1));
			}
		} catch (Exception e) {

			logger.info("Unable to extract utr from message : " + txnDescription);
		}
		return utr;
	}

	public ServiceProviderTransactionRequestView formRequest(TransactionRequestView requestView) {
		ServiceProviderTransactionRequestView request = new ServiceProviderTransactionRequestView();
		request.setAmount(String.valueOf(requestView.getAmount()));
		request.setClientUniqueID(String.valueOf(requestView.getEkoTrxnId()));
		
		return request;
	}

	@Override
	public TransactionResponseView doTransactionEnquiry(String tid) {
		logger.info("Inside postImpsP2BRequest of Service ProviderTransactionService...");
		TransactionRequestView requestView = new TransactionRequestView();
		TransactionResponseView responseView = null;
		try {
			startTransaction();
			responseView = new TransactionResponseView();
			TransactionBO transactionBO = TransactionPersistence.getInstance().getTransactionByEkoTrxnId(tid,
					getSource());
			if (transactionBO != null) {
				requestView.setEkoTrxnId(transactionBO.getEkoTrxnId());
				requestView.setRequestTypeId(TransactionType.TRANSACTION_ENQUIRY_TXTYPEID);

				ServiceProviderTransactionEnquiryRequestView request = formTransactionEnquiryRequest(transactionBO.getEkoTrxnId());
				ServiceProviderTransactionResponseView response = null;

				String requestBody = mapper.writeValueAsString(request);
				String responseBody = null;

				try {
					try {
						this.messagelog = new MessageLogBO();

						logger.info("Request body :" + requestBody);
						this.messagelog.setRequest(requestBody);
						this.messagelog.setEkoTrxnId(requestView.getEkoTrxnId());
						this.messagelog.setRequestedAt(new Date());
						this.messagelog.setTypeId(requestView.getRequestTypeId());
						this.messagelog.save();
						HibernateDataAccess.commitTransaction();
					} catch (Exception e2) {
						e2.printStackTrace();
						rollBackTransaction();
						logger.info("Exception while commiting message log before sending enquiry request..");
						responseView.setStatus(TransactionConstant.SUSPICIOUS_RESPONSE_RECEIVED);
						responseView.setResponseCode(TransactionConstant.EXCEPTION_NOT_POSTED_TO_SERVICE_PROVIDER);
						responseView.setDescription("Exception while commiting message log");
						return responseView;
					} finally {
						HibernateDataAccess.closeSessionTL();
					}

					ConfigurationBO mock = StartupCache.getInstance()
							.getConfigByKey(getUniqueKey(ConfigurationConstant.MOCKED_RESPONSE_TRXN_AND_REQUERY));

					if (mock.getConfigValue().equals(ConfigurationConstant.MOCKED)) {
						requestView.setAmount(transactionBO.getAmount());
						responseBody = new MockResponseService().setMoneyTransferAndRequeryMockResponse(requestView,
								getSource());
					} else {

						try {
							
							/*
							 * requestBody = AES256EncryptionDecription.getInstance().encrypt(requestBody,
							 * StartupCache.getInstance()
							 * .getConfigByKey(getUniqueKey(ConfigurationConstant.ENCRYPTION_KEY))
							 * .getConfigValue());
							 */
							
							// TODO: do your stuff here
							
							requestBody = "";
							
						} catch (Exception e) {
							e.printStackTrace();
							responseView.setStatus(TransactionConstant.FAIL);
							responseView.setDescription("Exception while Encrypting body ");
							return responseView;
						}

						responseBody = communication.postRequest(requestBody, requestView.getRequestTypeId(),
								transactionBO.getTransactionMode());
					}

					if (responseBody != null && responseBody.trim().length()>1) {
						logger.info("Response Received from Service Provider : " + responseBody);
						try {
							startTransaction();
							if (responseBody.length() > 1900) {
								this.messagelog.setResponse(responseBody.substring(0, 1900));
							} else {
								this.messagelog.setResponse(responseBody);
							}
							this.messagelog.setResponseReceivedAt(new Date());
							this.messagelog.save();
							HibernateDataAccess.commitTransaction();
						} catch (Exception e1) {
							logger.info(
									"Exception while logging response in  message log after posting enquiry request...");
							e1.printStackTrace();
							rollBackTransaction();
						} finally {
							HibernateDataAccess.closeSessionTL();
						}

						try {
							response = mapper.readValue(responseBody, ServiceProviderTransactionResponseView.class);

							if (TransactionConstant.REQUEST_SUCCESS.equals(response.getResponseCode())) {

								parseResponse(responseView, response);

								responseView = analyzeResponse(responseView, TransactionConstant.ENQUIRY_TYPE);

							} else {

								parseResponse(responseView, response);

								responseView.setStatus(TransactionConstant.ENQUIRY_FAILED);

								responseView.setDescription(response.getDisplayMessage());
							}

						} catch (Exception e) {
							responseView.setStatus(TransactionConstant.SUSPICIOUS_RESPONSE_RECEIVED);
							responseView.setDescription("Exception while deserializing object " + responseBody);
							e.printStackTrace();
						}

					} else {
						responseView.setStatus(TransactionConstant.REQUEST_READ_TIME_OUT);
						responseView.setResponseCode(TransactionConstant.READ_TIME_OUT);
						responseView.setDescription("Read time out");
					}
				} catch (ApiRequestPostingException e) {
					e.printStackTrace();
					logger.info("Request not posted to Service Provider");
					responseView.setStatus(TransactionConstant.SUSPICIOUS_RESPONSE_RECEIVED);
					responseView.setResponseCode(TransactionConstant.EXCEPTION_NOT_POSTED_TO_SERVICE_PROVIDER);
					responseView.setDescription("Request not posted to Service Provider");
				}
			} else {
				responseView.setStatus(TransactionConstant.SUSPICIOUS_RESPONSE_RECEIVED);
				responseView.setDescription("Invalid Tid (This tid does not exists)");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			HibernateDataAccess.closeSessionTL();
		}
		return responseView;
	}

	private ServiceProviderTransactionEnquiryRequestView formTransactionEnquiryRequest(String ekoTrxnId) {
		ServiceProviderTransactionEnquiryRequestView request = new ServiceProviderTransactionEnquiryRequestView();
		request.setClientUniqueId(ekoTrxnId);
		
		//TODO add more parameters if required
		
		return request;
	}

	@Override
	public TransactionResponseView repostTransaction(TransactionRequestView trxnReq) {
		logger.info("Inside repostTransaction method of Service ProviderTransactionService class" + trxnReq.getEkoTrxnId() + "\t "
				+ trxnReq.getRequestType());
		TransactionResponseView response = null;
		TransactionBO transactionBO = null;
		try {
			this.startTime = LocalDateTime.now();
			startTransaction();

			transactionBO = TransactionPersistence.getInstance().getTransactionByEkoTrxnId(trxnReq.getEkoTrxnId());

			if (transactionBO != null && (transactionBO.getResponsecode()
					.equalsIgnoreCase(TransactionConstant.EXCEPTION_NOT_POSTED_TO_SERVICE_PROVIDER)
					|| transactionBO.getResponsecode().equalsIgnoreCase(TransactionConstant.TID_NOT_FOUND_AT_SERVICE_PROVIDER))
					&& transactionBO.getStatus().intValue() == TransactionConstant.SUSPICIOUS_RESPONSE_RECEIVED) {
				response = postRequestToServiceProvider(trxnReq);
				transactionBO.setStatus(response.getStatus());
				transactionBO.setResponsecode(response.getResponseCode());
				transactionBO.setTrackingNumber(response.getRrn());
				transactionBO.setBankTrxnId(response.getBankTrxnId());
				transactionBO.setUtrNumber(response.getUtrNumber());

				if (!transactionBO.getResponsecode().equalsIgnoreCase(TransactionConstant.EXCEPTION_NOT_POSTED_TO_SERVICE_PROVIDER)
						&& transactionBO.getStatus().intValue() != TransactionConstant.REQUEST_READ_TIME_OUT) {
					transactionBO.setCallbackStatus(TransactionConstant.CALLBACK_REQUIRED);
				}
				transactionBO.save();
				HibernateDataAccess.commitTransaction();
			}

		} catch (Exception e) {
			logger.info("Inside exception block of  doImpsP2ARequest....");
			e.printStackTrace();
		} finally {
			logger.info("Inside finally block of  doImpsP2ARequest....");
			HibernateDataAccess.closeSessionTL();
		}
		return response;
	}

	@Override
	public String pushFile(String fileName) {
		return null;
	}

	@Override
	public String pullFile(String fileName) {
		return null;
	}

	private boolean canPostTransactions() {
		ConfigurationBO transactionPostingHours = StartupCache.getInstance()
				.getConfigByKey(getUniqueKey(ConfigurationConstant.TRANSACTION_POSTING_HOURS));

		if (transactionPostingHours != null) {
			return isWithinTimeWindow(transactionPostingHours.getConfigValue());
		} else {
			this.logger.info("Time window configuration not found...");
			return true;
		}
	}

	private boolean isWithinTimeWindow(String transactionTimeWindow) {

		try {
			if (checkTimeWindow(new Date(), transactionTimeWindow)) {
				return true;
			} else {
				this.logger.info("Transaction cant be send at this time valid time window : " + transactionTimeWindow);
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			this.logger.info("Exception while validating time window : " + transactionTimeWindow);
			return false;
		}
	}

	private boolean checkTimeWindow(final Date currentDate, final String transactionTimeWindow) throws Exception {
		String[] timeWindowParams = transactionTimeWindow.split("_");
		SimpleDateFormat sdf_HHmm = new SimpleDateFormat("HH:mm");
		SimpleDateFormat sdf_HH = new SimpleDateFormat("HH");
		SimpleDateFormat sdf_MM = new SimpleDateFormat("mm");
		Date windowStartTime = sdf_HHmm.parse(timeWindowParams[0]);
		Date windowEndTime = sdf_HHmm.parse(timeWindowParams[1]);
		Date currentTime = sdf_HHmm.parse(sdf_HH.format(currentDate) + ":" + sdf_MM.format(currentDate));
		if (currentTime.after(windowStartTime) && currentTime.before(windowEndTime)) {
			return true;
		}
		return false;
	}

	private void sendRealTimeCallback(TransactionResponseView response) {
		try {
			this.endTime = LocalDateTime.now();

			Duration duration = Duration.between(this.endTime, this.startTime);

			long diff = Math.abs(duration.getSeconds());
			logger.info("Response time :" + diff);

			long simplibankTimeout = Long.parseLong(StartupCache.getInstance()
					.getConfigByKey(getUniqueKey(ConfigurationConstant.SIMPLIBANK_TIMEOUT)).getConfigValue());

			if (diff > simplibankTimeout) {
				response.setRealTimeResponse(TransactionConstant.REAL_TIME_CALLBACK);
				logger.info("RealTime response Notifiying simplibank...");
				List<TransactionResponseView> callback = new ArrayList<TransactionResponseView>();
				callback.add(response);
				new Thread(new CallbackService(callback)).start();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void sendSystemAlertIfRequired(TransactionRequestView requestView) {
		//TODO
	}

	private void reduceOrIncreaseTimeout() {
		try {
			new Thread(new Runnable() {

				@Override
				public void run() {
					if (!timeOutReduced && continuousTimeout.get() > Integer.parseInt(StartupCache.getInstance()
							.getConfigByKey(getUniqueKey(ConfigurationConstant.REDUCE_TIMEOUT_ON_CONTINUOUS_NWTOUT))
							.getConfigValue())) {

						reduceTimeout();

					} else if (timeOutReduced && continuousTimeout.get() < Integer.parseInt(StartupCache.getInstance()
							.getConfigByKey(getUniqueKey(ConfigurationConstant.REDUCE_TIMEOUT_ON_CONTINUOUS_NWTOUT))
							.getConfigValue())) {

						resetTimeout();
					}
				}

				private void resetTimeout() {
					try {
						startTransaction();
						ConfigurationBO timeOutConfigInDb = TransactionPersistence.getInstance()
								.getConfigByConfigKeyAndSource(ConfigurationConstant.CONNECTION_TIME_OUT, getSource());
						StartupCache.getInstance().addToConfigurationMap(timeOutConfigInDb);
						timeOutReduced = false;
						logger.info("Timeout configuration has been reset : " + timeOutConfigInDb.getConfigValue());
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} finally {
						closeHibernateSession();
					}
				}

				private void reduceTimeout() {
					logger.info("Reducing timeout ...");

					ConfigurationBO timeOutConfiguration = StartupCache.getInstance()
							.getConfigByKey(getUniqueKey(ConfigurationConstant.CONNECTION_TIME_OUT));

					int simplibankTimeoutConfiguration = Integer.parseInt(StartupCache.getInstance()
							.getConfigByKey(getUniqueKey(ConfigurationConstant.CONNECTION_TIME_OUT)).getConfigValue());
					timeOutConfiguration.setConfigValue(String.valueOf((simplibankTimeoutConfiguration - 2 * 1000) > 0
							? (simplibankTimeoutConfiguration - 2 * 1000)
							: 0));
					StartupCache.getInstance().addToConfigurationMap(timeOutConfiguration);

					logger.info("Reduced timeout ...:" + timeOutConfiguration.getConfigValue());

					timeOutReduced = true;
				}
			}).start();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
