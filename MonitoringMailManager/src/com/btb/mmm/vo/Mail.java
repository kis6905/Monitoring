package com.btb.mmm.vo;


public class Mail {
	
	private String title = null;
	private String contents = null;
	private String responseCode = null;
	private String serviceCode = null;
	private String writer = null;
	private String fromMailAddr = null;
	private String toMailAddr = null;
	
	public Mail() {
	}
	
	public Mail(String title, String contents, String responseCode, String serviceCode) {
		this.title = title;
		this.contents = contents;
		this.responseCode = responseCode;
		this.serviceCode = serviceCode;
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
	}
	
	public String getServiceCode() {
		return serviceCode;
	}

	public void setServiceCode(String serviceCode) {
		this.serviceCode = serviceCode;
	}

	public String getWriter() {
		return writer;
	}

	public void setWriter(String writer) {
		this.writer = writer;
	}

	public String getFromMailAddr() {
		return fromMailAddr;
	}

	public void setFromMailAddr(String fromMailAddr) {
		this.fromMailAddr = fromMailAddr;
	}

	public String getToMailAddr() {
		return toMailAddr;
	}

	public void setToMailAddr(String toMailAddr) {
		this.toMailAddr = toMailAddr;
	}

	@Override
	public String toString() {
		return "Mail [title=" + title
				+ ", contents=" + contents
				+ ", responseCode=" + responseCode
				+ ", serviceCode=" + serviceCode
				+ ", writer=" + writer
				+ ", fromMailAddr="	+ fromMailAddr
				+ ", toMailAddr=" + toMailAddr
				+ "]";
	}
}
