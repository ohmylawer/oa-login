package com.trs.om.security;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;

public class DigestAuthenticationProvider extends DaoAuthenticationProvider {

	@Override
	protected void additionalAuthenticationChecks(UserDetails userDetails,
			UsernamePasswordAuthenticationToken authentication)
			throws AuthenticationException {

		if (authentication.getCredentials() == null) {
			throw new BadCredentialsException(messages.getMessage(
					"AbstractUserDetailsAuthenticationProvider.badCredentials",
					"Bad credentials"), isIncludeDetailsObject() ? userDetails
					: null);
		}

		String presentedPassword = authentication.getCredentials().toString();

		MyUsernamePasswordAuthenticationToken auth = (MyUsernamePasswordAuthenticationToken) authentication;

		if (auth.isCredentialsAlreadyEncoded()) {
			if (!presentedPassword.equals(userDetails.getPassword())) {
				throw new BadCredentialsException(
						messages.getMessage(
										"AbstractUserDetailsAuthenticationProvider.badCredentials",
										"Bad credentials"),
						isIncludeDetailsObject() ? userDetails : null);
			}
		} else {
			Object salt = null;

			if (getSaltSource() != null) {
				salt = getSaltSource().getSalt(userDetails);
			}

			if (!getPasswordEncoder().isPasswordValid(
					userDetails.getPassword(), presentedPassword, salt)) {
				throw new BadCredentialsException(
						messages.getMessage(
										"AbstractUserDetailsAuthenticationProvider.badCredentials",
										"Bad credentials"),
						isIncludeDetailsObject() ? userDetails : null);
			}
		}

	}

	@Override
	public boolean supports(Class<? extends Object> authentication) {
		return authentication == MyUsernamePasswordAuthenticationToken.class;
	}

}
