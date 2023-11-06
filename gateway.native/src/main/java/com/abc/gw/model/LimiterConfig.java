package com.abc.gw.model;

import java.util.ArrayList;
import java.util.List;

import com.abc.gw.util.LimiterUtil;

@SuppressWarnings("serial")
public class LimiterConfig implements java.io.Serializable {
	private String limits;
	private List<LimiterApi> limitList = new ArrayList<LimiterApi>();

	public String getLimits() {
		return limits;
	}

	public LimiterConfig setLimits(String limits) {
		this.limits = limits;
		if (limits != null && limits.trim().length() > 0) {
			this.limitList.clear();
			String[] array = limits.split(";");
			for (String str : array) {
				String[] arr = str.split("=");
				if (arr.length == 2) {
					LimiterApi api = new LimiterApi();
					api.setApiPattern(arr[0]);
					api.setLimit(arr[1]);
					this.limitList.add(api);
				}
			}
			LimiterUtil.refreshLimiter();
		}
		return this;
	}

	public List<LimiterApi> getLimiterApis() {
		return limitList;
	}
}
