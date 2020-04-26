// package synway.module_publicaccount.public_chat.adapter;
//
// import android.content.Context;
// import android.content.Intent;
// import android.graphics.drawable.Drawable;
// import android.text.Html;
// import android.view.LayoutInflater;
// import android.view.View;
// import android.view.View.OnClickListener;
// import android.widget.ImageView;
// import android.widget.LinearLayout;
// import android.widget.LinearLayout.LayoutParams;
// import android.widget.TextView;
//
// import java.util.ArrayList;
//
//
// import synway.module_publicaccount.Main;
// import synway.module_publicaccount.R;
// import synway.module_publicaccount.analytical.obj.Obj_PublicMsgBase;
// import synway.module_publicaccount.analytical.obj.Obj_PublicMsgPicTxt;
// import synway.module_publicaccount.analytical.obj.view.Obj_ViewBase;
// import synway.module_publicaccount.analytical.obj.view.Obj_ViewPicTxt;
// import synway.module_publicaccount.analytical.obj.view.Obj_ViewTxt;
// import synway.module_publicaccount.analytical.obj.view.Obj_ViewDivider;
// import synway.module_publicaccount.analytical.obj.view.Obj_ViewTxtArea;
// import synway.module_publicaccount.public_chat.LBSMapAct;
// import synway.module_publicaccount.public_chat.PAWebViewAct;
// import synway.module_publicaccount.until.StringUtil;
//
// /**
//  * 从字面意思上来看，这个类是用来表示图文消息的ViewHold;
//  * ViewHolder的职责应该仅仅只是负责承载Content对应的View;
//  * 但是这个ViewHold却把什么context,loginUserID都包含进来了;
//  */
// public class ViewHolder_PublicPicTxt extends AdapterTypeRender {
//
//     private View convertView = null;
//     private String loginUserID = null;
//
//     @SuppressWarnings("unused")
//     private int screenWidth = 0;
//
//     public ViewHolder_PublicPicTxt(Context context, String loginUserID,
//                                    int screenWidth) {
//         super(context);
//         convertView = LayoutInflater.from(context).inflate(
//                 R.layout.model_public_account_chat_item_pictxt, null);
//         this.loginUserID = loginUserID;
//         this.screenWidth = screenWidth;
//     }
//
//     @Override
//     View getConvertView() {
//         return convertView;
//     }
//
//
//     @Override void initView() {
//
//     }
//
//
//     @Override void setData(int position, Obj_PublicMsgBase obj) {
//
//     }
//
//
//     /**
//      * 这个方法实际上干了两件事：
//      * 第一件事：从xml布局文件构建view对象
//      * 第二件事：向view填充数据
//      * 说白了，实际上就是把adapter.getView()里的工作迁移到这里来了
//      *
//      * @param position
//      * @param obj
//      * @param publicGUID
//      */
//     public void getAndSetHolder(int position, Obj_PublicMsgPicTxt obj,
//                                 String publicGUID) {
//         // 从XML布局文件加载View对象
//         LinearLayout linearLayout = (LinearLayout) convertView
//                 .findViewById(R.id.linearLayout2);
//         TextView tvTime = (TextView) convertView.findViewById(R.id.time);
//         TextView tvTitle = (TextView) convertView.findViewById(R.id.textView1);
//         tvTitle.setText(obj.titile);
//         tvTitle.setTextSize(TextParamDeal.getSize(obj.titleSize));
//         tvTitle.setGravity(TextParamDeal.getPosition(obj.titlePostiton));
//         ArrayList<Obj_ViewBase> viewList = obj.dataLines;
//         // 内容列表是空的，那么就显示一条灰色的分割线
//         if (null == viewList) {
//             View view = new View(context);
//             view.setBackgroundResource(R.color.mgary);
//             LayoutParams paramLine = new LayoutParams(LayoutParams.MATCH_PARENT,
//                     dip2px(1));
//             paramLine.setMargins(0, dip2px(10), 0, dip2px(10));
//             linearLayout.addView(view, paramLine);
//             return;
//         }
//
//         LayoutParams layoutParams = null;
//         View lineView = null;
//         for (int i = 0; i < viewList.size(); i++) {
//             Obj_ViewBase viewContent = viewList.get(i);
//             // 纯文本
//             if (viewContent instanceof Obj_ViewTxt) {
//                 lineView = getViewFromTxt((Obj_ViewTxt) viewContent);
//                 layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT,
//                         LayoutParams.WRAP_CONTENT);
//                 layoutParams.setMargins(dip2px(15), 0, 0, 0);
//             }
//             // 分隔符
//             else if (viewContent instanceof Obj_ViewDivider) {
//                 lineView = getViewFromDivider((Obj_ViewDivider) viewContent);
//                 layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT,
//                         dip2px(1));
//                 layoutParams.setMargins(0, dip2px(10), 0, dip2px(10));
//             }
//             // 文字域
//             else if (viewContent instanceof Obj_ViewTxtArea) {
//                 lineView = getViewFromTxtArea((Obj_ViewTxtArea) viewContent);
//                 layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT,
//                         LayoutParams.WRAP_CONTENT);
//                 layoutParams.setMargins(0, dip2px(5), 0, dip2px(5));
//             }
//             // 图片域
//             else if (viewContent instanceof Obj_ViewPicTxt) {
//                 lineView = getViewFromImgArea((Obj_ViewPicTxt) viewContent, publicGUID);
//                 layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT,
//                         LayoutParams.WRAP_CONTENT);
//                 layoutParams.setMargins(0, dip2px(5), 0, dip2px(5));
//             }
//             linearLayout.addView(lineView, layoutParams);
//
//             // 是否显示时间
//             if (obj.showSDFTime != null) {
//                 tvTime.setVisibility(View.VISIBLE);
//                 tvTime.setText(obj.showSDFTime);
//             } else {
//                 tvTime.setVisibility(View.GONE);
//                 tvTime.setText("");
//             }
//         }
//     }
//
//     // 构建分割线
//     // 分割线有实线和虚线两种类型，不过现有情况下用到的只有实线，所以Obj_ViewDivier这个对象在这里暂时也没有用到
//     private View getViewFromDivider(Obj_ViewDivider view_divider) {
// //        View view = new View(context);
// //        view.setBackgroundResource(R.color.mgary);
//         LinearLayout view = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.model_public_account_chat_view_imgtxt, null);
//         TextView tv = (TextView) view.findViewById(R.id.textView1);
//         tv.setVisibility(View.GONE);
//         ImageView iv = (ImageView) view.findViewById(R.id.imageView1);
//         iv.setVisibility(View.GONE);
//         view.setBackgroundResource(R.color.mgary);
//         view.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 1));
//
//
//         return view;
//     }
//
//     // 构建文本视图
//     private View getViewFromTxt(Obj_ViewTxt view_MsgTxT) {
//         View view = LayoutInflater.from(context).inflate(R.layout.model_public_account_item_map, null);
//         TextView textView1 = (TextView) view.findViewById(R.id.textView);
//         textView1.setPadding(0, dip2px(3), 0, 0);
//         textView1.setTextColor(TextParamDeal.getColor(view_MsgTxT.color));
//         textView1.setText(TextParamDeal.getContent(view_MsgTxT.content));
//         textView1.setTextSize(TextParamDeal.getSize(view_MsgTxT.size));
//         ImageView iv = (ImageView) view.findViewById(R.id.imageView);
//         iv.setVisibility(View.GONE);
//         if (StringUtil.isNotEmpty(view_MsgTxT.url)) {
//             String isLbs[] = IsLBSMap.isMap(view_MsgTxT.url);
//             Object[] tag;// 存放经纬度
//             OnClickListener clickListener;// 跳转到地图还是跳转到链接
//             int imageResource;// url对应的跳转类型
//             if (isLbs != null) {
//                 tag = new Object[]{isLbs[0], isLbs[1]};
//                 clickListener = onClickGoMap;
//                 imageResource = R.drawable.public_account_item_map;
//             } else {
//                 tag = new Object[]{view_MsgTxT.url, view_MsgTxT.urlName};
//                 clickListener = onClickListener;
//                 imageResource = R.drawable.public_account_item_link;
//             }
// //            textView1.setTag(tag);
// //            textView1.setOnClickListener(clickListener);
// //            textView1.setClickable(true);
// //            textView1.setFocusable(true);
// //            textView1.setFocusableInTouchMode(true);
// //            textView1.setBackgroundResource(R.drawable.item_click);
//
//             // 超链接类似的效果
//             textView1.setText(Html.fromHtml("<u>" + TextParamDeal.getContent(view_MsgTxT.content) + "</u>"));
//             textView1.setTextColor(Main.instance().context.getResources().getColor(R.color.mblue));
//
//             iv.setImageResource(imageResource);
// //            iv.setVisibility(View.VISIBLE);
//             view.setTag(tag);
//             view.setOnClickListener(clickListener);
//             view.setBackgroundResource(R.drawable.item_click);
//
//         }
//         return view;
//     }
//
//     // 构建文字域视图
//     private View getViewFromTxtArea(Obj_ViewTxtArea view_MsgTxtArea) {
//         View view = LayoutInflater.from(context).inflate(R.layout.model_public_account_item_map, null);
//         view.setBackgroundResource(R.drawable.textarea_bg_circle_sp_selector);
//         TextView textView = (TextView) view.findViewById(R.id.textView);
//         ImageView imageView = (ImageView) view.findViewById(R.id.imageView);
//         imageView.setVisibility(View.GONE);
//         StringBuilder sb = new StringBuilder();
//         for (int i = 0; i < view_MsgTxtArea.content.length; i++) {
//             sb.append(view_MsgTxtArea.content[i]);
//             // 最后一行的时候不添加换行符
//             if (i != view_MsgTxtArea.content.length - 1) {
// //                sb.append("\n");
//                 sb.append("<br/>");// html中的换行符
//             }
//         }
//         textView.setTextSize(TextParamDeal.getSize(view_MsgTxtArea.size[0]));
//         textView.setTextColor(TextParamDeal.getColor(view_MsgTxtArea.color[0]));
//         textView.setLineSpacing(dip2px(3), 1);
//         textView.setPadding(dip2px(15), dip2px(3), dip2px(3), dip2px(3));
//         // 设置成超链接一样的效果
//         textView.setText(Html.fromHtml("<u>" + sb.toString() + "</u>"));
//         textView.setTextColor(Main.instance().context.getResources().getColor(R.color.mblue));
// //        textView.setFocusable(true);
// //        textView.setFocusableInTouchMode(true);
// //        textView.setClickable(true);
// //        textView.setBackgroundResource(R.drawable.textarea_bg_circle_sp_selector);
//
// //		// 测试
// //		view_MsgTxtArea.url = "http://172.16.1.117:8081/ZKService/main/myDocument.html";
// //		// 测试
//         if (StringUtil.isNotEmpty(view_MsgTxtArea.url)) {
//             Object objs[] = new Object[]{view_MsgTxtArea.url, view_MsgTxtArea.urlName};
// //            textView.setTag(objs);
// //            textView.setOnClickListener(onClickListener);
//             view.setTag(objs);
//             view.setOnClickListener(onClickListener);
//             imageView.setImageResource(R.drawable.public_account_item_link);
//         }
//         return view;
//     }
//
//     // 构建图片视图
//     private LinearLayout getViewFromImgArea(Obj_ViewPicTxt view_MsgImgArea,
//                                             String publicID) {
//         LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(context).inflate(
//                 R.layout.model_public_account_chat_view_imgtxt, null);
//         linearLayout.setBackgroundResource(R.drawable.textarea_bg_circle_nomal_sp);
//         TextView tv1 = (TextView) linearLayout.findViewById(R.id.textView1);
//         ImageView img1 = (ImageView) linearLayout.findViewById(R.id.imageView1);
//         tv1.setText(view_MsgImgArea.content);
//         String path = getPath(loginUserID, publicID) + "/" + view_MsgImgArea.picName;
//         Drawable drawable = Drawable.createFromPath(path);
//         if (null != drawable) {
//             img1.setImageDrawable(drawable);
//         } else {
//             img1.setImageResource(R.drawable.chat_send_pic_fail_png);
//         }
// //		//测试
// //		view_MsgImgArea.url = "http://172.16.1.117:8081/ZKService/main/myDocument.html";
// //		//测试
//         if (StringUtil.isNotEmpty(view_MsgImgArea.url)) {
//             Object[] objTag = new Object[]{view_MsgImgArea.url,
//                     view_MsgImgArea.urlName};
//             linearLayout.setBackgroundResource(R.drawable.textarea_bg_circle_sp_selector);
//             linearLayout.setTag(objTag);
//             linearLayout.setFocusable(true);
//             linearLayout.setFocusableInTouchMode(true);
//             linearLayout.setClickable(true);
//             linearLayout.setOnClickListener(onClickListener);
//         }
//         return linearLayout;
//     }
//
//     // 超链接监听
//     private OnClickListener onClickListener = new OnClickListener() {
//
//         @Override
//         public void onClick(View v) {
//             Intent intent = new Intent();
//
//             Object objs[] = (Object[]) v.getTag();
//             String url = (String) objs[0];
//             String urlName = (String) objs[1];
//
//             intent.putExtra("URL", url);
//             intent.putExtra("NAME", urlName);
//             intent.setClass(context, PAWebViewAct.class);
//             context.startActivity(intent);
//         }
//     };
//
//     // lbs地图跳转
//     private OnClickListener onClickGoMap = new OnClickListener() {
//         @Override
//         public void onClick(View v) {
//             Object objs[] = (Object[]) v.getTag();
//             String lon = (String) objs[0];
//             String lat = (String) objs[1];
//             Intent intent = new Intent();
//             intent.putExtra("LON", lon);
//             intent.putExtra("LAT", lat);
//             intent.setClass(context, LBSMapAct.class);
//             context.startActivity(intent);
//
//         }
//     };
//
// }