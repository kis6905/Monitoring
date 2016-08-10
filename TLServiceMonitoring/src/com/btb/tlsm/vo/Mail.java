package com.btb.tlsm.vo;

import org.json.simple.JSONObject;

public class Mail {
	
	private String title = null;
	private String contents = null;
	private String responseCode = null;
	private String serviceCode = null;
	
	public Mail() {
		this.title = "T map Link Service Error!!";
		this.serviceCode = "TL";
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContents() {
		return contents;
	}

	public void setContents(String contents) {
		this.contents = contents;
	}

	public String getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
		this.contents = "An error occurred!! Response Code: " + responseCode;
	}
	
	public String getServiceCode() {
		return serviceCode;
	}

	public void setServiceCode(String serviceCode) {
		this.serviceCode = serviceCode;
	}
	
	public void clean() {
		this.contents = null;
		this.responseCode = null;
	}
	
	@SuppressWarnings("unchecked")
	public JSONObject toJSONObject() {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("title", title);
		jsonObject.put("contents", contents);
		jsonObject.put("responseCode", responseCode);
		jsonObject.put("serviceCode", serviceCode);
		
		return jsonObject;
	}

	@Override
	public String toString() {
		return "Mail [title=" + title + ", contents=" + contents + ", responseCode=" + responseCode + ", serviceCode=" + serviceCode + "]";
	}
}
