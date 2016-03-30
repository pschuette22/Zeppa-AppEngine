package com.zeppamobile.common.cerealwrapper;

public class AnalyticsDataWrapper {
	
	private String key;
	private Integer value;
	
	public AnalyticsDataWrapper() {}
	
	public AnalyticsDataWrapper(String key, Integer value) {
		this.key = key;
		this.value = value;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public Integer getValue() {
		return value;
	}

	public void setValue(Integer value) {
		this.value = value;
	}
	
	

}
