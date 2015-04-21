package com.example.addressbook;

import com.example.addressbook.adapter.ContactList;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;


public class MainActivity extends Activity {

	// view
	private ImageView addContact;
	
	private ListView contactList;
	private ContactList adapter;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // set up listview adapter
        adapter = new ContactList(this);
        adapter.loadData(this);
        
        setActionBar();
       
        setGUI();
    }

	private void setActionBar() {
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);		
		actionBar.setCustomView(R.layout.activity_main_action_bar);
	}

    private void setGUI() {
		
    	this.addContact = (ImageView)findViewById(R.id.activity_main_add_contact);
    	
    	this.addContact.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				// go to activity for adding contact
				Intent intent = new Intent(MainActivity.this,AddContactActivity.class);
				startActivity(intent);
				finish();
			}
    		
    	});
    	
    	this.contactList = (ListView)findViewById(R.id.activity_main_listview);
    	this.contactList.setAdapter(adapter);
    	
    	this.contactList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				
				// go to information activity 
				Intent intent = new Intent(MainActivity.this,PersonInfoActivity.class);
				intent.putExtra("index", position);
				startActivity(intent);
				overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
				finish();
			}
    		
    	});
	}
}
