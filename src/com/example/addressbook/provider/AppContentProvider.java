package com.example.addressbook.provider;

import com.example.addressbook.database.DatabaseHelper;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.provider.BaseColumns;

public class AppContentProvider extends ContentProvider {
	
	private static final String AUTHORITY = "com.example.addressbook.provider";

	public static final class Contacts implements BaseColumns {
		public static final Uri CONTENT_URI=
	            Uri.parse("content://"+AUTHORITY+"/contacts");
		
		public static final String TABLE_NAME = "contacts";

		public static final String PATH = "path";
		public static final String NAME = "name";
		public static final String PHONE = "phone";
		public static final String EMAIL = "email";
	}
	
	private static final int COLLECTION_INDICATOR = 1;
	private static final int SINGLE_INDICATOR  = 2;
	
	private static final UriMatcher matcher;
	static{
		matcher = new UriMatcher(UriMatcher.NO_MATCH);
		matcher.addURI(AUTHORITY,"contacts",
				COLLECTION_INDICATOR);
		matcher.addURI(AUTHORITY,"contacts/#",
				SINGLE_INDICATOR);
	}
	
	private DatabaseHelper helper;
	
	@Override
	public boolean onCreate() {
		helper = DatabaseHelper.getInstance(getContext());
		return ((helper == null)? false : true);
	}
	
	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		
	    SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

	    qb.setTables(Contacts.TABLE_NAME);

	    switch (matcher.match(uri)) {
	    	case COLLECTION_INDICATOR:
	    		break;
	    	case SINGLE_INDICATOR:
	    		// adding the ID to the original query
	    		qb.appendWhere(BaseColumns._ID + "="
	    				+ uri.getLastPathSegment());
	    		break;
	    	default:
	    		throw new IllegalArgumentException("Unknown URI: " + uri);
	    }

	    Cursor cursor = qb.query(helper.getReadableDatabase(), projection, selection,
	                 selectionArgs, null, null, sortOrder);
	    
	    cursor.setNotificationUri(getContext().getContentResolver(), uri);
	    
	    return cursor;
	}
	
	@Override
	public String getType(Uri uri) {
		return null;
	}
	
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		return 0;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		return null;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		return 0;
	}
}
