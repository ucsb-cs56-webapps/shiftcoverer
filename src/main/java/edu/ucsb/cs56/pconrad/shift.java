package shiftcoverer;

import com.google.gson.*;

public class shift{
	// info about the user and shift
	private String job;
	private String time;
	private String name;
	private String email;

	public shift(String job, String time, String name, String email){
		this.job = job;
		this.time = time;
		this.name = name;
		this.email = email;
	}

	public String getJob(){
		return this.job;
	}

	public String getTime(){
		return this.time;
	}

	public String getName(){
		return this.name;
	}

	public String getEmail(){
		return this.email;
	}

	public boolean equals(shift s){
		if( this.job == s.job && this.time == s.time && this.name == s.name && this.email == s.email){
			return true;
		}else{
			return false;
		}
	}

	public String toJson(){
		Gson gson = new Gson();
		return gson.toJson(this);
	}

	public static shift toClass(String json){
		Gson gson = new Gson();
		return gson.fromJson(json, shift.class);
	}

}