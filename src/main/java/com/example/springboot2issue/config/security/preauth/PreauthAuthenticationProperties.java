package com.example.springboot2issue.config.security.preauth;

import com.example.springboot2issue.config.security.vo.AuthenticationProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "preauth.authentication")
public class PreauthAuthenticationProperties extends AuthenticationProperties {

	private String header;

	private boolean secondaryIamEnabled;

	public String getHeader() {
		return header;
	}

	public void setHeader(String header) {
		this.header = header;
	}

	public boolean isSecondaryIamEnabled() {
		return secondaryIamEnabled;
	}

	public void setSecondaryIamEnabled(boolean secondaryIamEnabled) {
		this.secondaryIamEnabled = secondaryIamEnabled;
	}

}
