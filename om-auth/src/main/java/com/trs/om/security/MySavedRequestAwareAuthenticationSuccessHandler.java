package com.trs.om.security;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.util.StringUtils;
import org.springframework.web.context.ServletContextAware;

import com.trs.om.util.HttpRequestUtils;

public class MySavedRequestAwareAuthenticationSuccessHandler extends
		SavedRequestAwareAuthenticationSuccessHandler implements ServletContextAware {

	// fields ---------------------------------------------------------------
	private boolean defaultHttps=false;
	private boolean defaultHttp=false;
	private boolean useReferer = false;
	private ServletContext servletContext;

	// methods --------------------------------------------------------------
	protected String determineDefaultTargetUrl(HttpServletRequest request,
			HttpServletResponse response){
		if(!request.isSecure()&&defaultHttps){
			return HttpRequestUtils.getSecureUrl(servletContext, request, response, getDefaultTargetUrl());
		}else if(request.isSecure()&&defaultHttp){
			return HttpRequestUtils.getInsecureUrl(servletContext, request, response, getDefaultTargetUrl());
		}else
			return getDefaultTargetUrl();
	}
	@Override
	protected String determineTargetUrl(HttpServletRequest request,
			HttpServletResponse response) {
		if (isAlwaysUseDefaultTargetUrl()) {
            return determineDefaultTargetUrl(request,response);
        }

        // Check for the parameter and use that if available
        String targetUrl = request.getParameter(getTargetUrlParameter());

        if (StringUtils.hasText(targetUrl)) {
            try {
                targetUrl = URLDecoder.decode(targetUrl, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                throw new IllegalStateException("UTF-8 not supported. Shouldn't be possible");
            }

            logger.debug("Found targetUrlParameter in request: " + targetUrl);

            return targetUrl;
        }

        if (useReferer && !StringUtils.hasLength(targetUrl)) {
            targetUrl = request.getHeader("Referer");
            logger.debug("Using Referer header: " + targetUrl);
        }

        if (!StringUtils.hasText(targetUrl)) {
            targetUrl = determineDefaultTargetUrl(request,response);
            logger.debug("Using default Url: " + targetUrl);
        }

        return targetUrl;

	}
	public void setServletContext(ServletContext servletContext) {
		this.servletContext=servletContext;

	}

	// accessors ------------------------------------------------------------
	public boolean isDefaultHttps() {
		return defaultHttps;
	}
	public void setDefaultHttps(boolean defaultHttps) {
		this.defaultHttps = defaultHttps;
	}
	public boolean isDefaultHttp() {
		return defaultHttp;
	}
	public void setDefaultHttp(boolean defaultHttp) {
		this.defaultHttp = defaultHttp;
	}
	public boolean isUseReferer() {
		return useReferer;
	}
	public void setUseReferer(boolean useReferer) {
		this.useReferer = useReferer;
	}
	public ServletContext getServletContext() {
		return servletContext;
	}


}
