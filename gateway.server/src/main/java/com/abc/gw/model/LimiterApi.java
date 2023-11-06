package com.abc.gw.model;

@SuppressWarnings("serial")
public class LimiterApi implements java.io.Serializable {

	private String apiPattern;
	private String limit;

	public String getApiPattern() {
		return apiPattern;
	}

	public void setApiPattern(String apiPattern) {
		this.apiPattern = apiPattern;
	}

	public String getLimit() {
		return limit;
	}

	public void setLimit(String limit) {
		this.limit = limit;
	}

}
