package org.akpsi.conventionapp.objects;

public class Times {

	String date;
	String time;
	String activity;
	String description;
	boolean canRegister;
	
	public boolean isCanRegister() {
		return canRegister;
	}
	public void setCanRegister(boolean canRegister) {
		this.canRegister = canRegister;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getActivity() {
		return activity;
	}
	public void setActivity(String activity) {
		this.activity = activity;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	
}
