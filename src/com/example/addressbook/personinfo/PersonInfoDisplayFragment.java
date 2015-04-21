package com.example.addressbook.personinfo;

import com.example.addressbook.R;
import com.example.addressbook.database.AppData;
import com.example.addressbook.database.Contact;
import com.example.addressbook.utils.BitmapLoader;

import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class PersonInfoDisplayFragment extends Fragment {

	// data
	private Contact info;
	
	// view
	private ImageView profile;
	private TextView name;
	private TextView phone;
	private TextView email;
	
	private ImageButton dial;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_person_info_display,
				container, false);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		// set up data
		info = AppData.getContactList().get(getActivity().getIntent().getExtras().getInt("index")); 
		
		setGUI();
		
		loadData();
	}

	private void setGUI() {
		this.profile = (ImageView)getActivity().findViewById(R.id.fragment_person_info_display_profile);
		this.name = (TextView)getActivity().findViewById(R.id.fragment_person_info_display_name);
		this.phone = (TextView)getActivity().findViewById(R.id.fragment_person_info_display_phone);
		this.email = (TextView)getActivity().findViewById(R.id.fragment_person_info_display_email);
		
		this.dial = (ImageButton)getActivity().findViewById(R.id.fragment_person_info_display_dial);
		
		this.dial.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// dial
				if (!phone.getText().equals("")) {
					Intent callIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+ phone.getText().toString().trim()));
					startActivity(callIntent);
				}
			}
			
		});
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
