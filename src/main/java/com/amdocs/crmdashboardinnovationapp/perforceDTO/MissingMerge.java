package com.amdocs.crmdashboardinnovationapp.perforceDTO;

public class MissingMerge {

	private String from;
	private String to;
	private String cl;
	private String user;

	public MissingMerge() {
		super();
	}

	public MissingMerge(String from, String to, String cl, String user) {
		super();
		this.from = from;
		this.to = to;
		this.cl = cl;
		this.user = user;
	}

	public String getFrom() {
		return from;
	}

	public String getTo() {
		return to;
	}

	public String getCl() {
		return cl;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public void setCl(String cl) {
		this.cl = cl;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

}
