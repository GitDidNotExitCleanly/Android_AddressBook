package com.example.addressbook.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.example.addressbook.R;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class CropActivity extends Activity {

	// data
	private String imagePath;
	
	// views
	private CropView cropView;
	
	private TextView btnCancel;
	private TextView btnChoose;
	
	private ProgressDialog progressDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_crop);
		
		// set up data
		imagePath = getIntent().getExtras().getString("imagePath");
		
		setGUI();
		
		setListener();
	}

	private void setGUI() {
		// set the image to display
		cropView = (CropView)findViewById(R.id.activity_crop_cropview);
		Point size = new Point();
		getWindowManager().getDefaultDisplay().getSize(size);
		Bitmap bmp = BitmapLoader.decodeBitmapFromFile(imagePath, size.x, size.y);
		cropView.setBmp(bmp,size.x,size.y);
		
		this.btnCancel = (TextView)findViewById(R.id.activity_crop_cancel);
		this.btnChoose = (TextView)findViewById(R.id.activity_crop_choose);
	}

	private void setListener() {
		
		// cancel
		this.btnCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent returnIntent = new Intent();
				setResult(RESULT_CANCELED, returnIntent);
				finish();
			}
			
		});
	
		// get cropped image
		this.btnChoose.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				Bitmap bmp = cropView.getCroppedImage();
				new StoreTempFile().execute(bmp);
			}
			
		});
		
	}
	
	// store cropped image in a temporary directory as a temporary file
	private class StoreTempFile extends AsyncTask<Bitmap, Void, String> {

		@Override
		protected void onPreExecute() {
			progressDialog = ProgressDialog.show(CropActivity.this, "", "Generating ...", true, false);
		}
		
		@Override
		protected String doInBackground(Bitmap... params) {
			
			Bitmap bmp = params[0];
			
		    File tempDir = new File(Environment.getExternalStorageDirectory() + "/Android/data/" + getApplicationContext().getPackageName() + "/temp");
		    if (! tempDir.exists()){
		        if (! tempDir.mkdirs()){
		            return null;
		        }
		    }
		    
		    File image = new File(tempDir,"temp.png");
		    
		    try {
		    	FileOutputStream fos = new FileOutputStream(image);
		    	bmp.compress(Bitmap.CompressFormat.PNG, 100, fos);
				fos.close();
		    } catch (FileNotFoundException e) {
		        Log.d("File not found", e.getMessage());
		    } catch (IOException e) {
		        Log.d("Error accessing file", e.getMessage());
		    }
			
			return image.getAbsolutePath();
		}
		
		@Override
		protected void onPostExecute(String path) {
			progressDialog.dismiss();
			
			// return path
			Intent returnIntent = new Intent();
			returnIntent.putExtra("path", path);
			setResult(RESULT_OK,returnIntent);
			finish();
		}
	}
}
