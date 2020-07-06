package in.eko.service.resources;

import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.apache.log4j.Logger;

import in.eko.service.util.helper.WebInstanceConstants;

@Path("/")
public class BaseResource {
	private static Logger logger = Logger.getLogger(BaseResource.class);

	@Path("{Instance}")
	public BaseTransactions go(@PathParam("Instance") String Instance)
	{
		logger.info("Instance "+Instance);
		
		switch(Instance.toUpperCase()){
			
		case WebInstanceConstants.DEFAULT:
			return new ServiceProviderTransactions();
		
		}
		return null;
	}
	
	

}
