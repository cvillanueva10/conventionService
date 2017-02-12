package org.akpsi.conventionapp.objects;

public class Response {

	boolean success;
	String message = null;
	
	public Response(boolean success){
		this.success = success;
		this.message = null;
	}
	
	public Response(boolean success, String message){
		this.success = success;
		this.message = message;
	}

	public boolean issuccess() {
		return success;
	}

	public void setsuccess(boolean success) {
		this.success = success;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
}
