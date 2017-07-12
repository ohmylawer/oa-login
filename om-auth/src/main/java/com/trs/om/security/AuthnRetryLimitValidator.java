package com.trs.om.security;

import org.apache.commons.lang.StringUtils;

import com.trs.om.common.EnvironmentVariableValidator;

public class AuthnRetryLimitValidator implements EnvironmentVariableValidator {

	public String getEditor() {
		return "<input type=\"text\" name=\"value\" style=\"width:100px;\"/>";
	}

	public String validate(String value) {
		if(StringUtils.isBlank(value))
			return "登录重试次数不能为空";
		Long longValue;
		try {
			longValue = Long.valueOf(value);
		} catch (NumberFormatException e) {
			return "\""+value+"\"不是一个合法的整数";
		}
		if(longValue<1){
			return "登录重试次数必须是一个大于等于1的整数";
		}else
			return null;
	}

}
