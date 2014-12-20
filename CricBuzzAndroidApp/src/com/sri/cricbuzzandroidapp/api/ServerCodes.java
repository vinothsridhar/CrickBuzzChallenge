package com.sri.cricbuzzandroidapp.api;

public class ServerCodes {

	//Error codes
	public static final int UNKNOWN_EXCEPTION=700;
	public static final int URL_EXCEPTION=701;;
	public static final int CONNECTION_EXCEPTION=702;
	
	
	//Status Codes
	
	public static String getErrorMsg(int errorCode) {
		String message = null;
		switch(errorCode) {
		case URL_EXCEPTION:
			message="The url you are trying not found.";
			break;
		case CONNECTION_EXCEPTION:
			message="Unable to connect to internet. Check your connection.";
			break;
		
		default:
			message="Unknown error occured.";
			break;
		}
		return message;
	}
}
