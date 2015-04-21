package com.example.addressbook.adapter;

import com.example.addressbook.R;
import com.example.addressbook.database.AppData;
import com.example.addressbook.database.Contact;
import com.example.addressbook.database.DatabaseHelper;
import com.example.addressbook.utils.BitmapLoader;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ContactList extends BaseAdapter {

	private LayoutInflater inflater;
	
	public ContactList(Context ctx) {
		this.inflater = (LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	@Override
	public int getCount() {
		return AppData.getContactList().size();
	}

	@Override
	public Object getItem(int index) {
		return AppData.getContactList().get(index);
	}

	@Override
	public long getItemId(int index) {
		return index;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		ViewHolder viewHolder = null;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.contact_list,parent,false);
			viewHolder = new ViewHolder();
			viewHolder.photo = (ImageView)convertView.findViewById(R.id.contact_list_profile);
			viewHolder.name = (TextView)convertView.findViewById(R.id.contact_list_name);
			convertView.setTag(viewHolder);
		}
		else {
			viewHolder = (ViewHolder)convertView.getTag();
		}
		
		// set data
		if ( !AppData.getContactList().get(position).getPath().equals("")) {
			viewHolder.photo.setImageBitmap(BitmapLoader.decodeBitmapFromFile(AppData.getContactList().get(position).getPath(), 40, 40));
		}
		viewHolder.name.setText(AppData.getContactList().get(position).getName());
		
		return convertView;
	}

	private class ViewHolder {
		ImageView photo;
		TextView name;
	}
	
	public void loadData(Context ctx) {
		if (!AppData.isDataLoaded()) {
			new LoadData().execute(ctx);
		}
	}
	
	// load data from database to the memory
	private class LoadData extends AsyncTask<Context,Void,Boolean> {

		@Override
		protected Boolean doInBackground(Context... params) {
			
			SQLiteDatabase db = DatabaseHelper.getInstance(params[0]).getReadableDatabase();
			
			Cursor cursor = db.query(DatabaseHelper.TABLE_CONTACTS, null, null, null, null, null, null);
			
			long lastContactID = -1;
			while (cursor.moveToNext()) {
				int IDIndex = cursor.getColumnIndex(DatabaseHelper.ID);
				int pathIndex = cursor.getColumnIndex(DatabaseHelper.PATH);
				int nameIndex = cursor.getColumnIndex(DatabaseHelper.NAME);
				int phoneIndex = cursor.getColumnIndex(DatabaseHelper.PHONE);
				int emailIndex = cursor.getColumnIndex(DatabaseHelper.EMAIL);
				
				long ID = cursor.getLong(IDIndex);
				String path = cursor.getString(pathIndex);
				String name = cursor.getString(nameIndex);
				String phone = cursor.getString(phoneIndex);
				String email = cursor.getString(emailIndex);
				
				if (ID > lastContactID) {
					lastContactID = ID;
				}
				
				Contact contact = new Contact(ID,path,name,phone,email);
				AppData.getContactList().add(contact);
			}
			
			if (lastContactID != -1) {
				AppData.setLastContactID(lastContactID);
			}
			
			cursor.close();
			
			if (lastContactID == -1) {
				return false;
			} 
			else {
				return true;
			}
		}
		
		@Override
		protected void onPostExecute(Boolean isUpdated) {
			if (isUpdated) {
				AppData.finishDataLoading();
				notifyDataSetChanged();
			}
		}
	}
}
