package com.example.addressbook.personinfo;

import com.example.addressbook.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.PopupWindow;

public class Photo_Popup extends PopupWindow {

	private View mMenuView;
	private Button btnChoose, btnDelete, btnCancel;
	
	public Photo_Popup(Context context, OnClickListener itemsOnClick) {
		super(context);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mMenuView = inflater.inflate(R.layout.photo_related_popup, null);
		
		btnChoose = (Button) mMenuView.findViewById(R.id.btn_choose_photo);
		btnDelete = (Button) mMenuView.findViewById(R.id.btn_delete_photo);
		btnCancel = (Button) mMenuView.findViewById(R.id.photo_related_popup_btn_cancel);
		
		btnCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dismiss();
			}
		});
		btnChoose.setOnClickListener(itemsOnClick);
		btnDelete.setOnClickListener(itemsOnClick);
		
		this.setContentView(mMenuView);	
		this.setWidth(LayoutParams.MATCH_PARENT);
		this.setHeight(LayoutParams.WRAP_CONTENT);	
		this.setFocusable(true);		
		this.setAnimationStyle(R.style.PopupMenuAnimation);
		
		mMenuView.setOnTouchListener(new OnTouchListener() {	
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				int height = mMenuView.findViewById(R.id.photo_related_pop_layout).getTop();
				int y=(int) event.getY();
				if(event.getAction() == MotionEvent.ACTION_UP){
					if(y<height){
						dismiss();
					}
				}				
				return true;
			}
		});
	}

	public void hideDeletePhotoButton() {
		this.btnDelete.setVisibility(View.GONE);
	}
	
	public void showDeletePhotoButton() {
		this.btnDelete.setVisibility(View.VISIBLE);
	}
}
