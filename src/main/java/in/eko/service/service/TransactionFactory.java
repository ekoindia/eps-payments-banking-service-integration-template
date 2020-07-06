package in.eko.service.service;

import in.eko.service.service.ServiceProviderTransactionService;
import in.eko.service.util.helper.WebInstanceConstants;

public class TransactionFactory {

	public TransactionService getTransactionFactory(String Instance) {
		TransactionService transactionService = null;
		switch (Instance) {

		case WebInstanceConstants.DEFAULT:
			transactionService = new ServiceProviderTransactionService();
			break;
		}
		return transactionService;
	}
}
