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

public class Delete_Popup extends PopupWindow {

	private View mMenuView;
	private Button btnDelete, btnCancel;
	
	public Delete_Popup(Context context, OnClickListener itemsOnClick) {
		super(context);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mMenuView = inflater.inflate(R.layout.delete_popup, null);
		
		btnDelete = (Button) mMenuView.findViewById(R.id.btn_delete);
		btnCancel = (Button) mMenuView.findViewById(R.id.delete_popup_btn_cancel);
		
		btnCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dismiss();
			}
		});
		btnDelete.setOnClickListener(itemsOnClick);
		
		this.setContentView(mMenuView);	
		this.setWidth(LayoutParams.MATCH_PARENT);
		this.setHeight(LayoutParams.WRAP_CONTENT);	
		this.setFocusable(true);		
		this.setAnimationStyle(R.style.PopupMenuAnimation);
		
		mMenuView.setOnTouchListener(new OnTouchListener() {	
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				int height = mMenuView.findViewById(R.id.delete_pop_layout).getTop();
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
}
