package com.trs.om.security;

import java.io.UnsupportedEncodingException;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.TextEscapeUtils;
import org.springframework.util.Assert;

import com.trs.om.bean.UserKey;
import com.trs.om.common.SystemUtil;
import com.trs.om.exception.TRSOMException;
import com.trs.om.service.UserService;

public class ImgUsernamePasswordAuthenticationFilter extends
		UsernamePasswordAuthenticationFilter {

	public static final String SPRING_SECURITY_FORM_ENCODED_KEY = "j_encoded";
	public static final String SPRING_SECURITY_FORM_CAPTCHA_KEY = "j_captcha";
	public static final String CAPTCHA_SESSION_KEY="CAPTCHA_SESSION_KEY";
	public static final String SPRING_SECURITY_FORM_KEYSN_KEY = "j_keysn";
	public static final String SPRING_SECURITY_FORM_KEYCHA_KEY = "j_keycha";
	public static final String SPRING_SECURITY_FORM_KEYRES_KEY = "j_keyres";
	private boolean postOnly = true;

	/** 登录请求中的密码加密参数名. */
	private String encodedParameter = SPRING_SECURITY_FORM_ENCODED_KEY;

	/** 保存在session中的验证码的属性名. */
	private String captchaSessionKey=CAPTCHA_SESSION_KEY;

	/** 登录请求的验证码参数名. */
	private String captchaParameter=SPRING_SECURITY_FORM_CAPTCHA_KEY;

	private String keySnParameter=SPRING_SECURITY_FORM_KEYSN_KEY;
	private String keyChaParameter=SPRING_SECURITY_FORM_KEYCHA_KEY;
	private String keyResParameter=SPRING_SECURITY_FORM_KEYRES_KEY;

	private UserService userService;

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request,
			HttpServletResponse response) throws AuthenticationException {
        if(request.getParameter("LOGINFORM")!=null){
	        Cookie cookie=new Cookie("LOGINFORM", request.getParameter("LOGINFORM"));
	    	cookie.setSecure(false);
	    	cookie.setPath(request.getContextPath());
	    	cookie.setMaxAge(-1);
	    	response.addCookie(cookie);
    	}
		if(StringUtils.equals("true", SystemUtil.getProperty("security.enableVerifyCode"))){
			String captchaExpected = (String)request.getSession().getAttribute(captchaSessionKey);
			String captchaReceived = request.getParameter(captchaParameter);
			if (captchaReceived == null || !captchaReceived.equalsIgnoreCase(captchaExpected)) {
				throw new BadVerifyCodeException("验证码错误");
			}
		}

		if (postOnly && !request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        }

        String username = obtainUsername(request);
        String password = obtainPassword(request);
        boolean encoded = StringUtils.isNotBlank(obtainEncoded(request));

        if (username == null) {
            username = "";
        }

        if (password == null) {
            password = "";
        }

        username = username.trim();

        MyUsernamePasswordAuthenticationToken authRequest = new MyUsernamePasswordAuthenticationToken(username, password,encoded);

        // Place the last username attempted into HttpSession for views
        HttpSession session = request.getSession(false);

        if (session != null || getAllowSessionCreation()) {
            request.getSession().setAttribute(SPRING_SECURITY_LAST_USERNAME_KEY, TextEscapeUtils.escapeEntities(username));
        }

        // Allow subclasses to set the "details" property
        setDetails(request, authRequest);

        Authentication authenticateToken = this.getAuthenticationManager().authenticate(authRequest);

        if("true".equals(SystemUtil.getProperty("security.useKey"))){
	        // 验证加密锁
			String presentedKeySn=request.getParameter(keySnParameter);
			UserKey userKey=userService.getUserKeyByUserName(username);
			if(userKey==null){
				throw new KeyException("用户未绑定加密锁");
			}
			if(!userKey.getKeySn().equals(presentedKeySn)){
				throw new KeyException("用户与加密锁不匹配");
			}
			String presentedKeyCha=request.getParameter(keyChaParameter);
			String presentedKeyRes=request.getParameter(keyResParameter);
			SrvVerify srvVerify = new SrvVerify();
			byte[] bStrRand;
			try {
				bStrRand = presentedKeyCha.getBytes("ISO-8859-1");
			} catch (UnsupportedEncodingException e) {
				throw new TRSOMException(e);
			}
			String hmacKey=userKey.getHmacKey();
			byte[] key = new byte[20];
			for(int i=0;i<key.length;i++){
				key[i]=Integer.valueOf(hmacKey.substring(i*2, (i+1)*2), 16).byteValue();
			}
			if(!srvVerify.HmacVerify(bStrRand,key,presentedKeyRes)){
				throw new KeyException("用户与认证密钥不匹配");
			}
        }
		return authenticateToken;

	}

	protected String obtainEncoded(HttpServletRequest request) {
        return request.getParameter(encodedParameter);
    }

	protected String obtainCaptcha(HttpServletRequest request) {
        return request.getParameter(encodedParameter);
    }

	@Override
	public void setPostOnly(boolean postOnly) {
		super.setPostOnly(postOnly);
		this.postOnly=postOnly;
	}

	public String getEncodedParameter() {
		return encodedParameter;
	}

	public void setEncodedParameter(String encodedParameter) {
		Assert.hasText(encodedParameter, "Encoded parameter must not be empty or null");
		this.encodedParameter = encodedParameter;
	}

	public void setCaptchaSessionKey(String captchaSessionKey) {
		Assert.hasText(captchaSessionKey, "Captcha session key must not be empty or null");
		this.captchaSessionKey = captchaSessionKey;
	}

	public void setCaptchaParameter(String captchaParameter) {
		Assert.hasText(captchaParameter, "Captcha parameter must not be empty or null");
		this.captchaParameter = captchaParameter;
	}

	public void setKeySnParameter(String keySnParameter) {
		Assert.hasText(keySnParameter, "Key Sn parameter must not be empty or null");
		this.keySnParameter = keySnParameter;
	}

	public void setKeyChaParameter(String keyChaParameter) {
		Assert.hasText(keyChaParameter, "Key cha parameter must not be empty or null");
		this.keyChaParameter = keyChaParameter;
	}

	public void setKeyResParameter(String keyResParameter) {
		Assert.hasText(keyResParameter, "Key res parameter must not be empty or null");
		this.keyResParameter = keyResParameter;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

}
