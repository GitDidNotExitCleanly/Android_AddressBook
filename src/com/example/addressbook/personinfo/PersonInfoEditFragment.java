package com.example.addressbook.personinfo;

import com.example.addressbook.MainActivity;
import com.example.addressbook.PersonInfoActivity;
import com.example.addressbook.R;
import com.example.addressbook.database.AppData;
import com.example.addressbook.database.Contact;
import com.example.addressbook.database.DatabaseHelper;
import com.example.addressbook.utils.BitmapLoader;
import com.example.addressbook.utils.CropActivity;

import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore.MediaColumns;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.database.Cursor;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class PersonInfoEditFragment extends Fragment {
	
	// data
	private Contact info;
	
	// views
	private ImageView profile;
	private EditText name;
	private EditText phone;
	private EditText email;
	
	private Button delete;
	
	// delete popup window
	private Delete_Popup delete_popup;
	private Photo_Popup photo_popup;
	private RelativeLayout popup_background;
	
	private boolean isPhotoAdded;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_person_info_edit, container,
				false);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		// set up data
		info = AppData.getContactList().get(getActivity().getIntent().getExtras().getInt("index")); 
		
		if (info.getPath().equals("")) {
			this.isPhotoAdded = false;
		}
		else {
			this.isPhotoAdded = true;
		}
		
		setGUI();
		
		loadData();
	}

	private void setGUI() {
		this.popup_background = (RelativeLayout)getActivity().findViewById(R.id.edit_popup_background);
		
		this.profile = (ImageView)getActivity().findViewById(R.id.fragment_person_info_edit_profile);
		this.name = (EditText)getActivity().findViewById(R.id.fragment_person_info_edit_name);
		this.phone = (EditText)getActivity().findViewById(R.id.fragment_person_info_edit_phone);
		this.email = (EditText)getActivity().findViewById(R.id.fragment_person_info_edit_email);
		
		this.profile.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				// create a popup window for selecting photos and deleting photos
				if (photo_popup == null) {
					photo_popup = new Photo_Popup(getActivity(),photoListener);
				}
				if (isPhotoAdded) {
					photo_popup.showDeletePhotoButton();
				}
				else {
					photo_popup.hideDeletePhotoButton();
				}
				photo_popup.showAtLocation(popup_background, Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
			}
    		
    	});
		
		this.delete = (Button)getActivity().findViewById(R.id.fragment_person_info_edit_delete);
		
		this.delete.setOnClickListener(new OnClickListener() {

			// delete contact
			@Override
			public void onClick(View arg0) {
				if (delete_popup == null) {
					delete_popup = new Delete_Popup(getActivity(),deleteListener);
				}
				delete_popup.showAtLocation(popup_background, Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
			}
			
		});
	}
	
	// listeners used for popup window
	private OnClickListener deleteListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if (v.getId() == R.id.btn_delete) {
				
				AppData.getContactList().remove(getActivity().getIntent().getExtras().getInt("index"));
				DatabaseHelper.getInstance(getActivity()).deleteContact(info.getId());
				
				Intent intent = new Intent(getActivity(),MainActivity.class);
				startActivity(intent);
				getActivity().overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
				getActivity().finish();
			}
		}
	};
	
	private OnClickListener photoListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			photo_popup.dismiss();
			
			switch (v.getId()) {
				case R.id.btn_choose_photo:
					Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
					startActivityForResult(intent, 0);
					break;
				case R.id.btn_delete_photo:
					profile.setImageResource(R.drawable.default_profile);
					((PersonInfoActivity)getActivity()).setPhotoPath("");
					isPhotoAdded = false;
					break;
			}
		}
		
	};
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 0 && resultCode == Activity.RESULT_OK) {

			// get the selected photo path
			Uri selectedImage = data.getData();
			String[] filePathColumn = { MediaColumns.DATA };
			Cursor cursor = getActivity().getContentResolver().query(selectedImage,filePathColumn, null, null, null);
			cursor.moveToFirst();
			int columnIndex = cursor.getColumnIndex(filePathColumn[0]);    

			String imagePath = cursor.getString(columnIndex);
			cursor.close();
			
			// crop
			Intent intent = new Intent(getActivity(), CropActivity.class);
			intent.putExtra("imagePath", imagePath);
			startActivityForResult(intent, 1);
			getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
		}
		if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
			
			String path = data.getStringExtra("path");
			
			profile.setImageBitmap(BitmapLoader.decodeBitmapFromFile(path, 60, 60));
			((PersonInfoActivity)getActivity()).setPhotoPath(path);
			
			isPhotoAdded = true;
		}
	}

	private void loadData() {
		if (!info.getPath().equals("")) {
			this.profile.setImageBitmap(BitmapLoader.decodeBitmapFromFile(info.getPath(), 60, 60));
		}
		this.name.setText(info.getName());
		this.phone.setText(info.getPhone());
		this.email.setText(info.getEmail());
	}

}
