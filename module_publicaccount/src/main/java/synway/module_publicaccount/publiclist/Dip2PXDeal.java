package synway.module_publicaccount.publiclist;

import android.content.Context;

class Dip2PXDeal {

	/** 根据手机的分辨率从 dp 的单位 转成为 px(像素) */
	static final int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}
	
	static int px2dip(Context context, float pxValue){                  
        final float scale = context.getResources().getDisplayMetrics().density;                   
        return (int)(pxValue / scale + 0.5f);           
    }   

}
