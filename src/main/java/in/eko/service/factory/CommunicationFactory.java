package in.eko.service.factory;
import in.eko.service.service.CommunicationService;
import in.eko.service.util.helper.WebInstanceConstants;

public class CommunicationFactory {

	public Communication getCommunicationFactory(String factoryName){
		Communication communication = null;
		switch(factoryName){
		
		case WebInstanceConstants.DEFAULT:
			communication = CommunicationService.getInstance();
			break;
		
		}
		return communication;
		
	}

}
