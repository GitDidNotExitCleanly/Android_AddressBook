package com.example.addressbook.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;

public class DatabaseHelper extends SQLiteOpenHelper {

	private static DatabaseHelper singleton=null;
	
	private static final String DATABASE_NAME="contacts.db";
	private static final int SCHEMA = 1;
	
	public static final String TABLE_CONTACTS = "contacts";
	
	public static final String ID = "_id";
	public static final String PATH = "path";
	public static final String NAME = "name";
	public static final String PHONE = "phone";
	public static final String EMAIL = "email";
	
	public synchronized static DatabaseHelper getInstance(Context ctx) {
		if (singleton == null) {
			singleton=new DatabaseHelper(ctx.getApplicationContext());
		}
		return singleton;
	}
	
	private DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, SCHEMA);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		try {
			db.beginTransaction();
			db.execSQL("CREATE TABLE "+TABLE_CONTACTS+" ("+ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"+PATH+" TEXT,"+NAME+" TEXT,"+PHONE+" TEXT,"+EMAIL+" TEXT);");		
			
			// test data
			String path;
			String name;
			String phone;
			String email;
			ContentValues cv;
			for (int i=0;i<5;i++) {
				cv = new ContentValues();
				path = "";
				name = "Test User "+i;
				phone = "07999262217";
				email = "testuseri@nottingham.ac.uk";
				cv.put(PATH, path);
				cv.put(NAME, name);
				cv.put(PHONE, phone);
				cv.put(EMAIL, email);
				
				db.insert(TABLE_CONTACTS, null, cv);
			}
		
			db.setTransactionSuccessful();
		}
		finally {
			db.endTransaction();
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}
	
	// update contact information
	public void updateContact(Contact updatedInfo) {
		new UpdateContact().execute(updatedInfo);
	}
	
	private class UpdateContact extends AsyncTask<Contact,Void,Void> {

		@Override
		protected Void doInBackground(Contact... params) {
			
			Contact info = params[0];
			ContentValues cv = new ContentValues();
			String path = info.getPath();
			String name = info.getName();
			String phone = info.getPhone();
			String email = info.getEmail();
			cv.put(PATH, path);
			cv.put(NAME, name);
			cv.put(PHONE, phone);
			cv.put(EMAIL, email);
			
			getWritableDatabase().update(TABLE_CONTACTS, cv, ID+"=?", new String[]{ String.valueOf(info.getId()) });
			
			return null;
		}
		
	}
	
	// add new contact
	public void addContact(Contact newInfo) {
		new AddContact().execute(newInfo);
	}
	
	private class AddContact extends AsyncTask<Contact,Void,Void> {

		@Override
		protected Void doInBackground(Contact... params) {
			
			Contact info = params[0];
			ContentValues cv = new ContentValues();
			String path = info.getPath();
			String name = info.getName();
			String phone = info.getPhone();
			String email = info.getEmail();
			cv.put(PATH, path);
			cv.put(NAME, name);
			cv.put(PHONE, phone);
			cv.put(EMAIL, email);
			
			getWritableDatabase().insert(TABLE_CONTACTS, null, cv);
			
			return null;
		}
		
	}
	
	// delete contact
	public void deleteContact(long id) {
		new DeleteContact().execute(id);
	}
	
	private class DeleteContact extends AsyncTask<Long,Void,Void> {

		@Override
		protected Void doInBackground(Long... params) {

			long id = params[0];
			getWritableDatabase().delete(TABLE_CONTACTS, ID+"=?", new String[]{ String.valueOf(id) });
			return null;
		}
		
	}
}
