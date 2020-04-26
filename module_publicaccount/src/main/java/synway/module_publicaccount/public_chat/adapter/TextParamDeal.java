package synway.module_publicaccount.public_chat.adapter;

import synway.module_publicaccount.Main;
import synway.module_publicaccount.R;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;

public class TextParamDeal {

	/** 位置， 默认=1，靠左=1，剧中=2，靠右=3 */
	static final int getPosition(int position){
		if(position == 1){
			return Gravity.CENTER_HORIZONTAL;
		}else if( position == 2){
			return Gravity.RIGHT;
		}else{
			return Gravity.LEFT;
		}
	}

	static final String getContent(String content){
		if(null == content ){
			return "";
		}
		return content;
	}
	
	static final float getSize(int size){
		if(size == 2){
//			return 18;
			return 15;
		}else if(size == 1){
			return 15;
		}else{
			return 12;
		}
	}
	
	public static final int getColor(String color){
		if(color == null || "".equals(color.trim())){
			return Main.instance().context.getResources().getColor(R.color.mblack);
		}
		try {
			int result = Color.parseColor(color);
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return Main.instance().context.getResources().getColor(R.color.mblack);
		}
		
	}

}
