package synway.module_publicaccount.public_chat.adapter;

import android.content.Context;
import android.view.View;
import synway.module_publicaccount.analytical.obj.Obj_PublicMsgBase;

/**
 * 用于对不同类型item数据到UI的渲染
 */
abstract class AdapterTypeRender {

	protected Context context = null;
	protected IOnPublicChatItemClick onPublicChatItemClick = null;

	public AdapterTypeRender(Context context,IOnPublicChatItemClick onPublicChatItemClick){
		this.context = context;
		this.onPublicChatItemClick = onPublicChatItemClick;
	}

	/**
	 * 返回一个item的convertView，也就是BaseAdapter中getView方法中返回的convertView
	 */
	abstract View getConvertView();

	/**
	 * 初始化Item布局的控件
	 */
	abstract void initView();

	/**
	 * 对指定position的item进行数据的适配
	 * @param position
	 */
	abstract void setData(int position,Obj_PublicMsgBase obj);


}