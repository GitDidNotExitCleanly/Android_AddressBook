package com.example.addressbook.database;

import java.util.ArrayList;

public class AppData {

	private static long lastContactID = -1;
	private static boolean isDataLoaded = false;
	private static ArrayList<Contact> contactList = new ArrayList<Contact>();
	
	public static long getLastContactID() {
		return lastContactID;
	}
	
	public static void setLastContactID(long id) {
		lastContactID = id;
	}
	
	public static void increaseLastContactID() {
		lastContactID++;
	}
	
	public static boolean isDataLoaded() {
		return isDataLoaded;
	}
	
	public static void finishDataLoading() {
		isDataLoaded = true;
	}
	
	public static ArrayList<Contact> getContactList() {
		return contactList;
	}
}
