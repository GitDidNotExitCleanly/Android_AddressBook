package com.example.addressbook;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.example.addressbook.database.AppData;
import com.example.addressbook.database.Contact;
import com.example.addressbook.database.DatabaseHelper;
import com.example.addressbook.personinfo.Photo_Popup;
import com.example.addressbook.utils.BitmapLoader;
import com.example.addressbook.utils.CropActivity;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore.MediaColumns;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class AddContactActivity extends Activity {

	// image storage path
	private String imageFileStorage;
	
	// views
	private ImageView btnCancel;
	private ImageView btnDone;
	
	private ImageView profile;
	private String photoPath;
	private EditText name;
	private EditText phone;
	private EditText email;
	
	// popup window for selecting photos
	private Photo_Popup photo_popup;
	private RelativeLayout photo_popup_background;

	private boolean isPhotoAdded = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_contact);
		
		imageFileStorage =  Environment.getExternalStorageDirectory()+ "/Android/data/"+ getApplicationContext().getPackageName()+ "/ContactImages";
		
        setActionBar();
        
        setGUI();
	}

	private void setActionBar() {
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);		
		actionBar.setCustomView(R.layout.activity_add_contact_action_bar);
	}

	private void setGUI() {
    	this.btnCancel = (ImageView)findViewById(R.id.activity_add_contact_cancel);
    	this.btnDone = (ImageView)findViewById(R.id.activity_add_contact_done);
    	
		this.profile = (ImageView)findViewById(R.id.activity_add_contact_profile);
		this.name = (EditText)findViewById(R.id.activity_add_contact_name);
		this.phone = (EditText)findViewById(R.id.activity_add_contact_phone);
		this.email = (EditText)findViewById(R.id.activity_add_contact_email);
    	
		// cancel adding new contact
    	this.btnCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(AddContactActivity.this,MainActivity.class);
				startActivity(intent);
				finish();
			}
    		
    	});
    	
    	// finish adding new contact (name field cannot be empty)
    	this.btnDone.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (name.getText().toString().equals("")) {
					Toast toast = Toast.makeText(AddContactActivity.this, "Name can not be empty !", 1000);
					toast.show();
				}
				else {
					String newName = name.getText().toString().trim();
					String newPhone = phone.getText().toString().trim();
					String newEmail = email.getText().toString().trim();

					AppData.increaseLastContactID();
					long newID = AppData.getLastContactID();
					
					String newPath;
					if (photoPath == null) {
						newPath = "";
					}
					else {
						newPath = imageFileStorage+"/"+newID+".png";
						
						// store the image
						new StoreImage().execute(newID);
					}
					
					// update data in memory and database
					Contact newContact = new Contact(newID,newPath,newName,newPhone,newEmail);
					AppData.getContactList().add(newContact);
					DatabaseHelper.getInstance(AddContactActivity.this).addContact(newContact);
					
					// return to main activity
					Intent intent = new Intent(AddContactActivity.this,MainActivity.class);
					startActivity(intent);
					finish();
				}
			}
    		
    	});
    	
    	// for selecting photo and deleting photo
		this.profile.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (photo_popup == null) {
					photo_popup = new Photo_Popup(AddContactActivity.this,photoListener);
					photo_popup_background = (RelativeLayout)findViewById(R.id.add_contact_popup_background);
				}
				if (isPhotoAdded) {
					photo_popup.showDeletePhotoButton();
				}
				else {
					photo_popup.hideDeletePhotoButton();
				}
				photo_popup.showAtLocation(photo_popup_background, Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
			}
    		
    	});
	}
	
	private OnClickListener photoListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			photo_popup.dismiss();
			
			switch (v.getId()) {
				case R.id.btn_choose_photo:
					// choose from photo album
					Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
					startActivityForResult(intent, 0);
					break;
				case R.id.btn_delete_photo:
					// delete existing photo
					profile.setImageResource(R.drawable.default_profile);
					photoPath = null;
					isPhotoAdded = false;
					break;
			}
		}
		
	};
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		// get the selected photo path
		if (requestCode == 0 && resultCode == Activity.RESULT_OK) {

			Uri selectedImage = data.getData();
			String[] filePathColumn = { MediaColumns.DATA };
			Cursor cursor = getContentResolver().query(selectedImage,filePathColumn, null, null, null);
			cursor.moveToFirst();
			int columnIndex = cursor.getColumnIndex(filePathColumn[0]);    

			String imagePath = cursor.getString(columnIndex);
			cursor.close();

			// crop the photo
			Intent intent = new Intent(this, CropActivity.class);
			intent.putExtra("imagePath", imagePath);
			startActivityForResult(intent, 1);
			overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
		}
		if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
			
			photoPath = data.getStringExtra("path");
			
			// display the photo
			profile.setImageBitmap(BitmapLoader.decodeBitmapFromFile(photoPath, 60, 60));
			
			isPhotoAdded = true;
		}
	}
	
	// for storing a copy of the selected image in internal storage
	private class StoreImage extends AsyncTask<Long, Void, Void> {

		@Override
		protected Void doInBackground(Long... params) {
			
			long id = params[0];
			
		    File mediaStorageDir = new File(imageFileStorage);
		    
		    // Create the storage directory if it does not exist
		    if (! mediaStorageDir.exists()){
		        if (! mediaStorageDir.mkdirs()){
		            return null;
		        }
		    }
		    
		    File image = new File(mediaStorageDir+File.separator+id+".png");
		    
		    try {
		    	FileOutputStream fos = new FileOutputStream(image);
		    	BitmapLoader.decodeBitmapFromFile(photoPath, 60, 60).compress(Bitmap.CompressFormat.PNG, 100, fos);
			    fos.close();
		    } 
		    catch (FileNotFoundException e) {
		    	Log.d("File not found", e.getMessage());
		    } 
		    catch (IOException e) {
		        Log.d("Error accessing file", e.getMessage());
		    }  
		    
			return null;
		}
		
	}
}
