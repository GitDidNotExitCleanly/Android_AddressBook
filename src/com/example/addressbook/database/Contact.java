package com.example.addressbook.database;

public class Contact {

	private long id;
	private String path;
	private String name;
	private String phone;
	private String email;
	
	public Contact(long id,String path,String name,String phone,String email) {
		this.id = id;
		this.path = path;
		this.name = name;
		this.phone = phone;
		this.email = email;
	}

	public long getId() {
		return id;
	}

	public String getPath() {
		return path;
	}

	public String getName() {
		return name;
	}

	public String getPhone() {
		return phone;
	}

	public String getEmail() {
		return email;
	}
	
	public void update(String path,String name,String phone,String email) {
		if (path != null) {
			this.path = path;
		}
		if (name != null) {
			this.name = name;
		}
		if (phone != null) {
			this.phone = phone;
		}
		if (email != null) {
			this.email = email;
		}
	}
}
