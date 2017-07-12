package com.trs.om.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.security.web.PortMapper;
import org.springframework.security.web.PortResolverImpl;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.trs.om.common.SystemUtil;
import com.trs.otm.authentication.MD5;

public class HttpRequestUtils {
	public static Integer getIntParameter(HttpServletRequest request,String name){
		return getIntParameter(request, name, null);
	}
	public static Integer getIntParameter(HttpServletRequest request,String name,Integer defaultValue){
		String value=request.getParameter(name);
		if(StringUtils.isBlank(value))
			return defaultValue;
		else
			return Integer.valueOf(value.trim());
	}
	public static Long getLongParameter(HttpServletRequest request,String name){
		return getLongParameter(request, name, null);
	}
	public static Long getLongParameter(HttpServletRequest request,String name,Long defaultValue){
		String value=request.getParameter(name);
		if(StringUtils.isBlank(value))
			return defaultValue;
		else
			return Long.valueOf(value.trim());
	}
	public static Boolean getBooleanParameter(HttpServletRequest request,String name){
		return getBooleanParameter(request, name, null);
	}
	public static Boolean getBooleanParameter(HttpServletRequest request,String name,Boolean defaultValue){
		String value=request.getParameter(name);
		if(StringUtils.isBlank(value))
			return defaultValue;
		else
			return Boolean.valueOf(value.trim());
	}
	public static String getStringParameter(HttpServletRequest request,String name){
		return getStringParameter(request, name, null);
	}
	public static String getStringParameter(HttpServletRequest request,String name,String defaultValue){
		String value=request.getParameter(name);
		if(StringUtils.isBlank(value))
			return defaultValue;
		else
			return value.trim();
	}
	/**
	 * 获取请求路径。
	 * 例如：
	 * 请求的url为http://host:port/foo/page.html，应用的上下文路径为/foo，则返回的请求路径为/page.html。
	 * @param request
	 * @return
	 */
	public static String getRequestPath(HttpServletRequest request){
		if(request==null)
			return null;
		return request.getRequestURI().substring(request.getContextPath().length());
	}
	public static String getRequestPathWithQueryString(HttpServletRequest request){
		String path=getRequestPath(request);
		if(StringUtils.isNotBlank(path)){
			String queryString=request.getQueryString();
			if(StringUtils.isNotBlank(queryString)) return path+"?"+queryString;
			else return path;
		}else return null;
	}
	/** 表单令牌的有效时间为24小时。 */
	private static long formTokenExpiration=24*3600*1000;
	private static String formTokenKey="hdwy71ki";
	private static final String SESSION_PASSWORD_KEY="SESSION_PASSWORD_KEY";
	public static String createFormToken(HttpServletRequest request){
		String formToken=null;
		try {
			StringBuilder tokenBuilder=new StringBuilder();
			tokenBuilder.append(System.currentTimeMillis()+formTokenExpiration).append(':')
				.append(request.getSession().getId());
			String plainPart=tokenBuilder.toString();
			String sessionPassword=(String) request.getSession().getAttribute(SESSION_PASSWORD_KEY);
			if(null==sessionPassword){
				sessionPassword=Double.toString(Math.random());
				request.getSession().setAttribute(SESSION_PASSWORD_KEY, sessionPassword);
			}
			tokenBuilder.append(':').append(sessionPassword).append(':').append(formTokenKey);
			String md5Part=MD5.md5(tokenBuilder.toString());
			String fullToken=plainPart+":"+md5Part;
			formToken=new String(Base64.encodeBase64(fullToken.getBytes("UTF-8")),"US-ASCII");
		} catch (UnsupportedEncodingException e) {
			//do nothing
		}
		return formToken;
	}
	public static boolean validateFormToken(HttpServletRequest request,String formToken){
		try {
			String fullToken=new String(Base64.decodeBase64(formToken.getBytes("US-ASCII")),"UTF-8");
			int index=StringUtils.lastIndexOf(fullToken, ':');
			String plainPart=StringUtils.substring(fullToken, 0, index);
			String md5Part=StringUtils.substring(fullToken,index+1);
			index=StringUtils.indexOf(plainPart, ':');
			long expirationTime=Long.valueOf(StringUtils.substring(plainPart, 0,index));
			String sessionId=StringUtils.substring(plainPart, index+1);
			if(!StringUtils.equals(sessionId, request.getSession().getId())) return false;//session id错误或伪造的令牌
			if(System.currentTimeMillis()>expirationTime) return false;//令牌过期或伪造的令牌
			StringBuilder tokenBuilder=new StringBuilder();
			tokenBuilder.append(plainPart);
			String sessionPassword=(String) request.getSession().getAttribute(SESSION_PASSWORD_KEY);
			if(null==sessionPassword){
				return false;//session过期或伪造的令牌
			}
			tokenBuilder.append(':').append(sessionPassword).append(':').append(formTokenKey);
			String md5Part2=MD5.md5(tokenBuilder.toString());
			if(!StringUtils.equals(md5Part, md5Part2)) return false;//伪造的令牌
			return true;
		} catch (Exception e) {
			return false;//程序异常
		}
	}
	public static boolean validateFormToken(HttpServletRequest request){
		String formToken=getStringParameter(request, "fmtk");
		return validateFormToken(request,formToken);
	}
	public static boolean isAjaxRequest(HttpServletRequest request){
		return "XMLHttpRequest".equals(request.getHeader("x-requested-with"))||
			null!=request.getHeader("x-flash-version");
	}
	public static String resolveToAbsoluteUrl(HttpServletRequest request,String relativePath){
		StringBuilder builder=new StringBuilder();
		builder.append(request.getScheme())
			.append("://")
			.append(request.getServerName())
			.append(':')
			.append(request.getServerPort())
			.append(request.getContextPath())
			.append(relativePath);
		return builder.toString();
	}
	public static boolean isUrlInDomain(String url,HttpServletRequest request){
		try {
			URI uri=new URI(url);
			if(StringUtils.isBlank(uri.getHost())||StringUtils.equalsIgnoreCase(uri.getHost(), request.getServerName())){
				return true;
			}
			return false;
		} catch (URISyntaxException e) {
			return false;
		}
	}
	/**
     * Send an HTTP error response code.
     *
     * @param request  the HttpServletRequest object.
     * @param response the HttpServletResponse object.
     * @param code     the HttpServletResponse error code (see {@link javax.servlet.http.HttpServletResponse} for possible error codes).
     * @param e        the Exception that is reported.
     * @param ctx      the ServletContext object.
     * @see org.apache.struts2.dispatcher.Dispatcher#sendError(HttpServletRequest, HttpServletResponse, ServletContext, int, Exception)
     */
    public static void sendError(HttpServletRequest request, HttpServletResponse response,
            int code, Exception e) {
        try {
            // WW-1977: Only put errors in the request when code is a 500 error
            if (code == HttpServletResponse.SC_INTERNAL_SERVER_ERROR) {
                // send a http error response to use the servlet defined error handler
                // make the exception availible to the web.xml defined error page
                request.setAttribute("javax.servlet.error.exception", e);

                // for compatibility
                request.setAttribute("javax.servlet.jsp.jspException", e);
            }

            // send the error response
            response.sendError(code, e.getMessage());
        } catch (IOException e1) {
            // we're already sending an error, not much else we can do if more stuff breaks
        }
    }

    public static String getSecureUrl(ServletContext servletContext,HttpServletRequest request,HttpServletResponse response,String path){
		ApplicationContext applicationContext=WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);

		String secureUrl=request.getContextPath()+path;
		if(!StringUtils.equals("true", SystemUtil.getProperty("security.enableHTTPS"))) return secureUrl;
        PortMapper portMapper=applicationContext.getBean(PortMapper.class);
        PortResolverImpl portResolver=new PortResolverImpl();
        portResolver.setPortMapper(portMapper);
        Integer currentPort = new Integer(portResolver.getServerPort(request));
        Integer redirectPort = portMapper.lookupHttpsPort(currentPort);
        int standardPort=443;
        String scheme="https://";

        if (redirectPort != null) {
            boolean includePort = redirectPort.intValue() != standardPort;

            secureUrl = scheme + request.getServerName() + ((includePort) ? (":" + redirectPort) : "") + secureUrl;
        }

        return response.encodeRedirectURL(secureUrl);

	}

	public static String getInsecureUrl(ServletContext servletContext,HttpServletRequest request,HttpServletResponse response,String path){
		ApplicationContext applicationContext=WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);

		String insecureUrl=request.getContextPath()+path;
        PortMapper portMapper=applicationContext.getBean(PortMapper.class);
        PortResolverImpl portResolver=new PortResolverImpl();
        portResolver.setPortMapper(portMapper);
        Integer currentPort = new Integer(portResolver.getServerPort(request));
        Integer redirectPort = portMapper.lookupHttpPort(currentPort);
        int standardPort=80;
        String scheme="http://";

        if (redirectPort != null) {
            boolean includePort = redirectPort.intValue() != standardPort;

            insecureUrl = scheme + request.getServerName() + ((includePort) ? (":" + redirectPort) : "") + insecureUrl;
        }

        return response.encodeRedirectURL(insecureUrl);

	}
}
