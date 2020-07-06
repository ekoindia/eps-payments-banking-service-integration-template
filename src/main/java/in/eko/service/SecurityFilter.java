package in.eko.service;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

import org.apache.log4j.Logger;

import in.eko.service.model.ConfigurationBO;
import in.eko.service.util.helper.ConfigurationConstant;
import in.eko.service.util.helper.StartupCache;
import in.eko.service.util.helper.WebInstanceConstants;

@Provider
public class SecurityFilter implements ContainerRequestFilter {

	private static Logger logger = Logger.getLogger(SecurityFilter.class);
	// private static final String HOST="host";
	@Context
	private HttpServletRequest servletRequest;

	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {

		String sourceIp = servletRequest.getRemoteAddr();
		logger.info("Source Ip address " + sourceIp);
		ConfigurationBO ipConfig = StartupCache.getInstance().getConfigByKey(ConfigurationConstant.VALIDATE_IP_ON_OFF + "_" + WebInstanceConstants.GLOBAL);

		try {
			if (ipConfig.getConfigValue().equals("0") || servletRequest.getMethod().equals("GET")) {
				return;
			}

			if (sourceIp != null && isAuthorizedIpAddress(sourceIp)) {
				return;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		Response unauthorizedHostIp = Response.status(Response.Status.UNAUTHORIZED)
				.entity("Unauthorized Request : You are now allowed to make reuqest here").build();
		requestContext.abortWith(unauthorizedHostIp);

	}

	private boolean isAuthorizedIpAddress(String sourceIp) {
		ConfigurationBO ipConfig = StartupCache.getInstance().getConfigByKey(ConfigurationConstant.AUTHORIZED_IP_LIST + "_" + WebInstanceConstants.GLOBAL);
		List<String> authenticatediplist = Arrays.asList(ipConfig.getConfigValue().split(","));
		return authenticatediplist.contains(sourceIp);
	}
	

}
