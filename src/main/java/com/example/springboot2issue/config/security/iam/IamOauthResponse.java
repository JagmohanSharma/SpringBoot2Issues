package com.example.springboot2issue.config.security.iam;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "oauthResponse")
public class IamOauthResponse {

	private String status;

	private String userName;

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	@Override
	public String toString() {
		return "OauthResponse [status=" + status + ", userName=" + userName + "]";
	}

}
