package com.abc.gw.model;

@SuppressWarnings("serial")
public class BreakerApi implements java.io.Serializable {

	private String apiPattern;
	private Integer timeout;

	public String getApiPattern() {
		return apiPattern;
	}

	public void setApiPattern(String apiPattern) {
		this.apiPattern = apiPattern;
	}

	public Integer getTimeout() {
		return timeout;
	}

	public void setTimeout(Integer timeout) {
		this.timeout = timeout;
	}
}
