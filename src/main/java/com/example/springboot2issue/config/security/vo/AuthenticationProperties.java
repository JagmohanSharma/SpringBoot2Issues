package com.example.springboot2issue.config.security.vo;

public class AuthenticationProperties {

	private boolean enabled;

	private String[] requestMatchers;

	private CsrfProperties csrf;

	private AuthenticationFallback fallback;

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public String[] getRequestMatchers() {
		return requestMatchers;
	}

	public void setRequestMatchers(String[] requestMatchers) {
		this.requestMatchers = requestMatchers;
	}

	public CsrfProperties getCsrf() {
		return csrf;
	}

	public void setCsrf(CsrfProperties csrf) {
		this.csrf = csrf;
	}

	public AuthenticationFallback getFallback() {
		return fallback;
	}

	public void setFallback(AuthenticationFallback fallback) {
		this.fallback = fallback;
	}

}
