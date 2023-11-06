package com.abc.gw.model;

import java.util.ArrayList;
import java.util.List;

import com.abc.gw.util.BreakerUtil;

@SuppressWarnings("serial")
public class BreakerConfig  implements java.io.Serializable {
	private String timeouts;
	private List<BreakerApi> timeoutList = new ArrayList<BreakerApi>();

	public String getTimeouts() {
		return timeouts;
	}

	public BreakerConfig setTimeouts(String timeouts) {
		this.timeouts = timeouts;
		if (timeouts != null && timeouts.trim().length() > 0) {
			this.timeoutList.clear();
			String[] array = timeouts.split(";");
			for (String str : array) {
				String[] arr = str.split("=");
				if (arr.length == 2) {
					BreakerApi api = new BreakerApi();
					api.setApiPattern(arr[0]);
					String ss = arr[1];
					api.setTimeout(Integer.parseInt(ss));
					this.timeoutList.add(api);
				}
			}
			BreakerUtil.refreshBreaker();
		}
		return this;
	}

	public List<BreakerApi> getBreakerApis() {
		return timeoutList;
	}
}
