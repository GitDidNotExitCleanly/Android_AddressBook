package com.example.addressbook;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.example.addressbook.database.AppData;
import com.example.addressbook.database.DatabaseHelper;
import com.example.addressbook.utils.BitmapLoader;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class PersonInfoActivity extends Activity {

	// image file storage path
	private String imageFileStorage;
	
	private ActionBar actionBar;
	
	// display and edit fragment
	private Fragment display;
	private Fragment edit;
	
	// button switch
	private boolean isEditable = false;
	private ImageView leftButton;
	private ImageView rightButton;
	
	// selected photo path
	private String photoPath;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_person_info);
		
		// set image storage path
		imageFileStorage =  Environment.getExternalStorageDirectory()+ "/Android/data/"+ getApplicationContext().getPackageName()+ "/ContactImages";
		
		setFragment();
		
        setActionBar();
	}
	
	private void setFragment() {
		this.display = getFragmentManager().findFragmentById(R.id.activity_info_display);
		this.edit = getFragmentManager().findFragmentById(R.id.activity_info_edit);
		
		getFragmentManager().beginTransaction().hide(this.edit).hide(this.display).commit();
		getFragmentManager().beginTransaction().show(this.display).commit();
	}

	private void setActionBar() {
		actionBar = getActionBar();
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);		
		actionBar.setCustomView(R.layout.activity_person_info_action_bar);
	
    	this.leftButton = (ImageView)findViewById(R.id.activity_info_left_button);
    	this.rightButton = (ImageView)findViewById(R.id.activity_info_right_button);
  
    	leftButton.setImageResource(R.drawable.back_button_effect);
		rightButton.setImageResource(R.drawable.edit_button_effect);
		
    	this.leftButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (isEditable) {
					// edit cancel
					isEditable = false;
					
					leftButton.setImageResource(R.drawable.back_button_effect);
					rightButton.setImageResource(R.drawable.edit_button_effect);
					
					getFragmentManager().beginTransaction().hide(display).hide(edit).commit();
					getFragmentManager().beginTransaction().show(display).commit();
				}
				else {
					// button back
					Intent intent = new Intent(PersonInfoActivity.this,MainActivity.class);
					startActivity(intent);
					overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
					finish();
				}
			}
    		
    	});
    	
    	this.rightButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (isEditable) {
					// edit finished
					String newPath = null;
					if (photoPath != null) {
						if (!photoPath.equals("")) {
							long id = AppData.getContactList().get(getIntent().getExtras().getInt("index")).getId();
							
							// store image
							new StoreImage().execute(id);
							newPath = imageFileStorage+File.separator+id+".png";
						}
						else {
							newPath = "";
						}
					}
					
					// determine whether fields are changed
					EditText nameEdit = (EditText)findViewById(R.id.fragment_person_info_edit_name);
					EditText phoneEdit = (EditText)findViewById(R.id.fragment_person_info_edit_phone);
					EditText emailEdit = (EditText)findViewById(R.id.fragment_person_info_edit_email);
					
					ImageView photo = (ImageView)findViewById(R.id.fragment_person_info_display_profile);
					TextView nameDisplay = (TextView)findViewById(R.id.fragment_person_info_display_name);
					TextView phoneDisplay = (TextView)findViewById(R.id.fragment_person_info_display_phone);
					TextView emailDisplay = (TextView)findViewById(R.id.fragment_person_info_display_email);
					
					String newName = null;
					String newPhone = null;
					String newEmail = null;
					if (!nameEdit.getText().toString().equals(nameDisplay.getText().toString())) {
						newName = nameEdit.getText().toString();
					}
					if (!phoneEdit.getText().toString().equals(phoneDisplay.getText().toString())) {
						newPhone = phoneEdit.getText().toString();
					}
					if (!emailEdit.getText().toString().equals(emailDisplay.getText().toString())) {
						newEmail = emailEdit.getText().toString();
					}
					if (newName != null || newPhone != null || newEmail != null || newPath != null) {
						
						// update data in memory and database
						AppData.getContactList().get(getIntent().getExtras().getInt("index")).update(newPath, newName, newPhone, newEmail);
						DatabaseHelper.getInstance(PersonInfoActivity.this).updateContact(AppData.getContactList().get(getIntent().getExtras().getInt("index")));	
						
						// update information displayed in display fragment
						if (newName != null) {
							nameDisplay.setText(newName);
						}
						if (newPhone != null) {
							phoneDisplay.setText(newPhone);						
						}
						if (newEmail != null) {
							emailDisplay.setText(newEmail);
						}
						if (newPath != null) {
							if (newPath.equals("")) {
								photo.setImageResource(R.drawable.default_profile);
							}
							else {
								photo.setImageBitmap(BitmapLoader.decodeBitmapFromFile(photoPath, 60, 60));	
							}
						}
					}
					
					isEditable = false;
					
					leftButton.setImageResource(R.drawable.back_button_effect);
					rightButton.setImageResource(R.drawable.edit_button_effect);
					
					getFragmentManager().beginTransaction().hide(display).hide(edit).commit();
					getFragmentManager().beginTransaction().show(display).commit();
					
				}
				else {
					// edit
					isEditable = true;
					
					leftButton.setImageResource(R.drawable.edit_cancel_button_effect);
					rightButton.setImageResource(R.drawable.edit_done_button_effect);
					
					getFragmentManager().beginTransaction().hide(display).hide(edit).commit();
					getFragmentManager().beginTransaction().show(edit).commit();
				}
			}
    		
    	});
	}
	
	public void setPhotoPath(String path) {
		this.photoPath = path;
	}
	
	// store the selected image file
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
