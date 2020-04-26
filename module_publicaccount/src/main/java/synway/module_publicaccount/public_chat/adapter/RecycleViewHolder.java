package synway.module_publicaccount.public_chat.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import synway.module_interface.config.BaseUtil;
import synway.module_interface.config.userConfig.Sps_RWLoginUser;
import synway.module_publicaccount.Main;
import synway.module_publicaccount.R;
import synway.module_publicaccount.analytical.obj.Obj_PublicMsgPicTxt;
import synway.module_publicaccount.analytical.obj.Obj_PublicMsgTrail;
import synway.module_publicaccount.analytical.obj.Obj_PulibcMsgDynamicLocation;
import synway.module_publicaccount.analytical.obj.view.Obj_ViewBase;
import synway.module_publicaccount.analytical.obj.view.Obj_ViewDivider;
import synway.module_publicaccount.analytical.obj.view.Obj_ViewPicTxt;
import synway.module_publicaccount.analytical.obj.view.Obj_ViewTxt;
import synway.module_publicaccount.analytical.obj.view.Obj_ViewTxtArea;
import synway.module_publicaccount.analytical.obj.view.Obj_ViewUrlTxt;
import synway.module_publicaccount.map.Bean.UserPointBean;
import synway.module_publicaccount.map.publicrtgis.GisMapAct;
import synway.module_publicaccount.map.publicrtgis.TrailMapAct;
import synway.module_publicaccount.public_chat.LBSMapAct;
import synway.module_publicaccount.public_chat.PAWebViewAct;
import synway.module_publicaccount.public_chat.RTMPEvent;
import synway.module_publicaccount.public_chat.interfaces.UrlTextItemInterface;
import synway.module_publicaccount.until.DialogTool;
import synway.module_publicaccount.until.StringUtil;

import static synway.module_publicaccount.public_chat.adapter.Public_To_Intelligence_Click.locationChangeIntellgence;
import static synway.module_publicaccount.public_chat.adapter.Public_To_Intelligence_Click.urlChangeIntellgence;
import static synway.module_publicaccount.public_chat.util.ConfigUtil.CDR;
import static synway.module_publicaccount.public_chat.util.ConfigUtil.DIANWEI;
import static synway.module_publicaccount.public_chat.util.ConfigUtil.LBS;
import static synway.module_publicaccount.public_chat.util.ConfigUtil.LOCATION;
import static synway.module_publicaccount.public_chat.util.ConfigUtil.OWNER;

/**
 * Created by QSJH on 2016/4/15 0015.
 */
class RecycleViewHolder {

    private ArrayList<LinearLayout> lines = new ArrayList<>();// 所有已经创建的行，这些行有严格的顺序要求
    private View convertView;
    private TextView tvTitle, locationType, pointnum, usernameView;// 消息标题
    private TextView tvTime;// 消息时间
    private LinearLayout textlinearLayout, localtionlinearLayout, trailLayout, bglinearlayout, linearlayout_map;// 容纳"行"的布局
    private String publicGuid;// 公众号GUID
    private Context context;
    private Activity activity;
    private LayoutInflater layoutInflater;
    private ImageView pushlocation;
    private Obj_PublicMsgPicTxt obj_publicMsgPicTxt;
    private UrlTextItemInterface urlTextItemInterface;
    public void setUrlTextItemListener(UrlTextItemInterface listener) {
        this.urlTextItemInterface = listener;
    }
    public RecycleViewHolder(Context context, String publicGuid, Activity activity) {
        layoutInflater = LayoutInflater.from(context);
        convertView = layoutInflater.inflate(R.layout.model_public_account_chat_item_pictxt, null);
//        convertView = layoutInflater.inflate(R.layout.model_public_account_chat_item_pictxt, null);
        bglinearlayout = convertView.findViewById(R.id.bglinearLayout);
        linearlayout_map = convertView.findViewById(R.id.linearlayout_map);
        textlinearLayout = convertView.findViewById(R.id.textlinearLayout);
        pushlocation = convertView.findViewById(R.id.pushlocation);
        // tvTitle = (TextView) convertView.findViewById(R.id.tvtitle);
        tvTime = convertView.findViewById(R.id.time);
        locationType = convertView.findViewById(R.id.locationtype);
        localtionlinearLayout = convertView.findViewById(R.id.localtionlinearLayout);
        trailLayout = convertView.findViewById(R.id.trailLayout);
        pointnum = convertView.findViewById(R.id.pointnum);
        usernameView = convertView.findViewById(R.id.username);
        this.publicGuid = publicGuid;
        this.context = context;
        this.activity = activity;
    }


    public void render(Obj_PublicMsgPicTxt dataObj, Boolean ifbright) {
        this.obj_publicMsgPicTxt = dataObj;
        try {
            if (ifbright) {
                bglinearlayout.setBackgroundResource(R.drawable.view_public_bg_circle_sp_bright);
            } else {
                bglinearlayout.setBackgroundResource(R.drawable.view_public_bg_circle_sp);
            }
            textlinearLayout.setVisibility(View.VISIBLE);
            linearlayout_map.setVisibility(View.GONE);
            usernameView.setVisibility(View.GONE);
            pushlocation.setVisibility(View.GONE);
            locationType.setVisibility(View.GONE);
            trailLayout.setVisibility(View.GONE);
            localtionlinearLayout.setVisibility(View.GONE);
            pointnum.setVisibility(View.GONE);
            // 标题
            if (StringUtil.isEmpty(dataObj.titleUrl)) {
                tvTitle.setClickable(false);
                tvTitle.setText(dataObj.title);
//            tvTitle.setTextColor(context.getResources().getColor(R.color.mblack));
//            tvTitle.setTextColor(TextParamDeal.getColor("#869ABE"));
            } else {
                tvTitle.setClickable(true);
                tvTitle.setText(Html.fromHtml("<u>" + dataObj.title + "</u>"));
//            tvTitle.setTextColor(context.getResources().getColor(R.color.mblue));
//            tvTitle.setTextColor(TextParamDeal.getColor("#869ABE"));
                PublicAccountURL publicAccountURL = PublicAccountURLFactory.getPublicAccountURL(dataObj.titleUrl);
                tvTitle.setTag(publicAccountURL);
                View.OnClickListener clickListener;
                switch (publicAccountURL.getUrlType()) {
                    case PublicAccountURL.URL_TYPE_GPS:
                        clickListener = onClickGoMap;
                        break;
                    case PublicAccountURL.URL_TYPE_MEDIA:
                        clickListener = onClickPlay;
                        ((PublicAccountURL.URL_MEDIA) publicAccountURL).name = dataObj.titleUrlName;
                        break;
                    case PublicAccountURL.URL_TYPE_RTMP:
                        clickListener = onClickVideo;
                        break;
                    default:
                        clickListener = onClickListener;
                        ((PublicAccountURL.URL_SUPERLINK) publicAccountURL).name = dataObj.titleUrlName;
                }
                tvTitle.setOnClickListener(clickListener);

            }
//        tvTitle.setTextSize(TextParamDeal.getSize(dataObj.titleSize));
            tvTitle.setTextSize(18);
            tvTitle.setGravity(TextParamDeal.getPosition(dataObj.titlePostiton));
            //不管服务端传什么过来，标题始终居左
//        tvTitle.setGravity(Gravity.LEFT);
            // 时间
            tvTime.setVisibility(dataObj.showSDFTime != null ? View.VISIBLE : View.GONE);
            tvTime.setText(dataObj.showSDFTime != null ? dataObj.showSDFTime : "");
            int lineCount;
            if (dataObj.dataLines == null) {
                lineCount = 0;
            } else {
                lineCount = dataObj.dataLines.size();
            }

            // 如果已经创建的行不足以显示所有内容，那么就创建新行
            while (lines.size() < lineCount) {
                LinearLayout line = createNewLine();
                lines.add(line);
                textlinearLayout.addView(line);
            }

            // 隐藏已经创建的多余行
            if (lines.size() > lineCount) {
                for (int i = lineCount - 1; i < lines.size(); i++) {
                    View line = lines.get(i);
                    // 这个判断是为了提高效率
                    if (line.getVisibility() != View.GONE) {
                        line.setVisibility(View.GONE);
                    }
                }
            }
            // 渲染
            for (int i = 0; i < lineCount; i++) {
                Obj_ViewBase obj_viewBase = dataObj.dataLines.get(i);
                LinearLayout lineView;
                if (lines.size() < i + 1) {
                    lineView = createNewLine();
                    lines.add(lineView);
                    textlinearLayout.addView(lineView, getLineLayoutParagrams(obj_viewBase));
                } else {
                    lineView = lines.get(i);
                    lineView.setLayoutParams(getLineLayoutParagrams(obj_viewBase));
                    if (lineView.getVisibility() != View.VISIBLE) {
                        lineView.setVisibility(View.VISIBLE);
                    }
                }
                setonLongClick(dataObj, lineView);
                renderLine(obj_viewBase, lineView);
            }
        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(context, "报错。。。。"+e.toString(), Toast.LENGTH_LONG).show();
        }

    }

    private Obj_PublicMsgTrail obj_publicMsgTrail;

    //添加轨迹消息(5)的推送
    public void renderTypeTrail(Obj_PublicMsgTrail obj_publicMsgTrail, Boolean ifbright) {
        if (ifbright) {
            bglinearlayout.setBackgroundResource(R.drawable.view_public_bg_circle_sp_bright);
        } else {
            bglinearlayout.setBackgroundResource(R.drawable.view_public_bg_circle_sp);
        }
        textlinearLayout.setVisibility(View.GONE);
        linearlayout_map.setVisibility(View.VISIBLE);
        this.obj_publicMsgTrail = obj_publicMsgTrail;
        localtionlinearLayout.setVisibility(View.GONE);
        locationType.setVisibility(View.GONE);
        tvTitle.setClickable(false);
        tvTitle.setText("历史轨迹消息");
        pointnum.setVisibility(View.VISIBLE);
        // 时间
        tvTime.setVisibility(obj_publicMsgTrail.showSDFTime != null ? View.VISIBLE : View.GONE);
        tvTime.setText(obj_publicMsgTrail.showSDFTime != null ? obj_publicMsgTrail.showSDFTime : "");
        trailLayout.setVisibility(View.VISIBLE);
        pushlocation.setVisibility(View.VISIBLE);
        locationType.setVisibility(View.VISIBLE);
        if (obj_publicMsgTrail.Type == 1) {
            locationType.setText("GIS类型:敌方");
        } else if (obj_publicMsgTrail.Type == 2) {
            locationType.setText("GIS类型:友方");
        }
        String username = "";
        if (obj_publicMsgTrail.Type == 1) {//敌方
            Obj_PulibcMsgDynamicLocation.enemyTag enemyTag = (Obj_PulibcMsgDynamicLocation.enemyTag) obj_publicMsgTrail.Points.get(0).tag;
            username = enemyTag.publicObject;
        } else if (obj_publicMsgTrail.Type == 2) {//友方
            Obj_PulibcMsgDynamicLocation.friendTag friendTag = (Obj_PulibcMsgDynamicLocation.friendTag) obj_publicMsgTrail.Points.get(0).tag;
            if (StringUtil.isEmpty(friendTag.policeNum)) {//警号为空，用equip中locId判断,是設備類型
                username = friendTag.equip.equipName;
            } else {
                username = friendTag.name;
            }
        }
        usernameView.setVisibility(View.VISIBLE);
        usernameView.setText("用户名称:" + username);
        pointnum.setText("历史轨迹点个数:" + obj_publicMsgTrail.Points.size());
        trailLayout.setOnClickListener(onClicTrailkMap);
        pushlocation.setOnClickListener(onClicTrailkMap);
    }

    private Obj_PulibcMsgDynamicLocation dataObj;

    //添加动态消息(6)的推送
    public void renderTypeGis(Obj_PulibcMsgDynamicLocation dataObj, Boolean ifbright) {
        if (ifbright) {
            bglinearlayout.setBackgroundResource(R.drawable.view_public_bg_circle_sp_bright);
        } else {
            bglinearlayout.setBackgroundResource(R.drawable.view_public_bg_circle_sp);
        }
        textlinearLayout.setVisibility(View.GONE);
        linearlayout_map.setVisibility(View.VISIBLE);
        usernameView.setVisibility(View.GONE);
        localtionlinearLayout.setVisibility(View.VISIBLE);
        pointnum.setVisibility(View.GONE);
        localtionlinearLayout.removeAllViews();
        localtionlinearLayout.removeAllViewsInLayout();
        trailLayout.setVisibility(View.VISIBLE);
        this.dataObj = dataObj;
        tvTitle.setClickable(false);
        tvTitle.setText("实时轨迹消息");
        // 时间
        tvTime.setVisibility(dataObj.showSDFTime != null ? View.VISIBLE : View.GONE);
        tvTime.setText(dataObj.showSDFTime != null ? dataObj.showSDFTime : "");
        pushlocation.setVisibility(View.GONE);
        locationType.setVisibility(View.VISIBLE);
        if (dataObj.type == 1) {
            locationType.setText("GIS类型:敌方");
        } else if (dataObj.type == 2) {
            locationType.setText("GIS类型:友方");
        }
//          pointnum.setText("实时轨迹点个数:"+dataObj.points.size());
//          pushlocation.setOnClickListener(onClickMap);
        ArrayList<UserPointBean> userPointBeens = getUserList(dataObj);
        if (userPointBeens.size() > 0) {
            for (int i = 0; i < userPointBeens.size(); i++) {
                if (dataObj.points.get(i).type == 1) {
                    LinearLayout linearLayoutpoint = new LinearLayout(activity);
                    linearLayoutpoint.setOrientation(LinearLayout.VERTICAL);
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dip2px(activity, 50));
                    layoutParams.setMargins(0, 0, dip2px(activity, 10), dip2px(activity, 10));
                    linearLayoutpoint.setLayoutParams(layoutParams);
                    linearLayoutpoint.setBackgroundResource(R.drawable.textarea_bg_circle_sp_selector);
                    linearLayoutpoint.removeAllViews();
                    linearLayoutpoint.removeAllViewsInLayout();
                    //添加轨迹名字
                    TextView textView = new TextView(activity);
                    textView.setText("GIS 分组名称:" + userPointBeens.get(i).username);
                    textView.setTextSize(15);
                    textView.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);//下划线
                    LinearLayout.LayoutParams layoutParamsTextView = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    textView.setLayoutParams(layoutParamsTextView);
                    TextView time = new TextView(activity);
                    time.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);//下划线
                    time.setText("轨迹点时间秒:" + userPointBeens.get(i).points.get(0).time);
                    time.setTextSize(15);
                    LinearLayout.LayoutParams layoutParamstime = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    time.setLayoutParams(layoutParamstime);
                    linearLayoutpoint.addView(textView);
                    linearLayoutpoint.addView(time);
                    linearLayoutpoint.setOnClickListener(onClickMap);
                    localtionlinearLayout.addView(linearLayoutpoint);
                }
            }
        }

    }

    /**
     * @param obj_viewBase 行的类型
     */
    private LayoutParams getLineLayoutParagrams(Obj_ViewBase obj_viewBase) {
        LayoutParams layoutParams;
        if (obj_viewBase instanceof Obj_ViewTxt) {
            layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT,
                    LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(0, 0, 0, 0);
        } else if (obj_viewBase instanceof Obj_ViewDivider) {
            layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT,
                    dip2px(1));
            layoutParams.setMargins(0, dip2px(10), 0, dip2px(10));
        } else if (obj_viewBase instanceof Obj_ViewTxtArea) {
            layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT,
                    LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(0, dip2px(5), 0, dip2px(5));
        } else if (obj_viewBase instanceof Obj_ViewPicTxt) {
            layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT,
                    LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(0, dip2px(5), 0, dip2px(5));
        } else {
            layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT,
                    LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(0, dip2px(5), 0, dip2px(5));
        }
        return layoutParams;
    }


    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    private int dip2px(float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    private String getPath(String loginUserid, String publicID) {
        return BaseUtil.ChatFileUtil.getChatPicSmallPath(loginUserid, publicID);
    }


    /**
     * 获取当前已经创建的总行数
     *
     * @return 当前已经创建的总行数
     */
    public int getLineCount() {
        return lines.size();
    }

    /**
     * 获取某一行的View
     *
     * @param lineCount 哪一行
     * @return view
     */
    public View getLine(int lineCount) {
        return lines.get(lineCount);
    }

    /**
     * 在底部创建一个新的行
     *
     * @return view
     */
    private LinearLayout createNewLine() {
        return (LinearLayout) layoutInflater.inflate(R.layout.model_public_account_chat_view_imgtxt, null);
    }

    private void clearLineView(LinearLayout lineView) {
        lineView.setTag(null);
        lineView.setBackgroundColor(context.getResources().getColor(R.color.mwhite));
        lineView.setOnClickListener(null);
    }

    /**
     * 对某一行的控件进行渲染
     *
     * @param objBase 该行的数据模型
     * @return 渲染成功后的View
     */
    private View renderLine(Obj_ViewBase objBase, LinearLayout lineView) {
        clearLineView(lineView);
        // 纯文本
        if (objBase instanceof Obj_ViewTxt) {
            Obj_ViewTxt viewTxt = (Obj_ViewTxt) objBase;
            TextView tv = lineView.findViewById(R.id.textView1);
            tv.setVisibility(View.VISIBLE);
            tv.setPadding(0, dip2px(3), 0, 0);
//            tv.setTextColor(TextParamDeal.getColor("#869ABE"));
            tv.setText(TextParamDeal.getContent(viewTxt.content));
            tv.setTextSize(TextParamDeal.getSize(viewTxt.size));
            ImageView iv = lineView.findViewById(R.id.imageView1);
            iv.setVisibility(View.GONE);
            View publicline = lineView.findViewById(R.id.publicline);
            publicline.setVisibility(View.GONE);
            if (StringUtil.isNotEmpty(viewTxt.url)) {
                View.OnClickListener clickListener;// 跳转到地图还是跳转到链接
                PublicAccountURL publicAccountURL = PublicAccountURLFactory.getPublicAccountURL(viewTxt.url);
//                Toast.makeText(MainApp.getInstance(), "UrlType=" + publicAccountURL.getUrlType(), Toast.LENGTH_LONG).show();
                switch (publicAccountURL.getUrlType()) {
                    case PublicAccountURL.URL_TYPE_GPS:
                        clickListener = onClickGoMap;
                        break;
                    case PublicAccountURL.URL_TYPE_MEDIA:
                        clickListener = onClickPlay;
                        ((PublicAccountURL.URL_MEDIA) publicAccountURL).name = viewTxt.urlName;
                        break;
                    case PublicAccountURL.URL_TYPE_RTMP:
                        clickListener = onClickVideo;
                        break;
                    default:
                        clickListener = onClickListener;
                        ((PublicAccountURL.URL_SUPERLINK) publicAccountURL).name = viewTxt.urlName;
                }

                // 超链接类似的效果
                tv.setText(Html.fromHtml("<u>" + TextParamDeal.getContent(viewTxt.content) + "</u>"));
//                tv.setTextColor(context.getResources().getColor(R.color.mblue));


                lineView.setTag(publicAccountURL);
                lineView.setOnClickListener(clickListener);
                lineView.setBackgroundResource(R.drawable.item_click);

            }
        }
        // 图文
        else if (objBase instanceof Obj_ViewPicTxt) {
            Obj_ViewPicTxt picTxt = (Obj_ViewPicTxt) objBase;
//            lineView.setBackgroundResource(R.drawable.textarea_bg_circle_nomal_sp);
            TextView tv = lineView.findViewById(R.id.textView1);
            tv.setVisibility(View.VISIBLE);
            int visibility = tv.getVisibility();
            ImageView iv = lineView.findViewById(R.id.imageView1);
            iv.setVisibility(View.VISIBLE);
            iv.setMaxWidth(40);
            iv.setMaxHeight(40);
            View publicline = lineView.findViewById(R.id.publicline);
            publicline.setVisibility(View.VISIBLE);
            tv.setText(picTxt.content);
            tv.setTextColor(context.getResources().getColor(R.color.mblack));
            String path = getPath(Sps_RWLoginUser.readUserID(context), publicGuid) + "/" + picTxt.picName;
            Drawable drawable = Drawable.createFromPath(path);
            if (null != drawable) {
                iv.setImageDrawable(drawable);
            } else {
                iv.setImageResource(R.drawable.chat_send_pic_fail_png);
            }
//		//测试
//		view_MsgImgArea.url = "http://172.16.1.117:8081/ZKService/main/myDocument.html";
//		//测试
            if (StringUtil.isNotEmpty(picTxt.url)) {
                Object[] objTag = new Object[]{picTxt.url,
                        picTxt.urlName};
//              lineView.setBackgroundResource(R.drawable.textarea_bg_circle_sp_selector);
                lineView.setTag(objTag);
                lineView.setFocusable(true);
                lineView.setFocusableInTouchMode(true);
                lineView.setClickable(true);
                lineView.setOnClickListener(onClickListener);
            }
        }//URL域
        else if (objBase instanceof Obj_ViewUrlTxt) {
            final Obj_ViewUrlTxt viewUrlTxt = (Obj_ViewUrlTxt) objBase;
            TextView tv = lineView.findViewById(R.id.textView1);
            tv.setVisibility(View.VISIBLE);
            ImageView iv = lineView.findViewById(R.id.imageView1);
            iv.setMaxWidth(16);
            iv.setMaxHeight(16);
            iv.setVisibility(View.INVISIBLE);
            View publicline = lineView.findViewById(R.id.publicline);
            publicline.setVisibility(View.GONE);
            tv.setText(viewUrlTxt.content);
            tv.setTextColor(context.getResources().getColor(R.color.mblack));
            if (StringUtil.isNotEmpty(viewUrlTxt.url)) {
                iv.setVisibility(View.VISIBLE);
                iv.setImageResource(R.drawable.ico_jump);
                lineView.setFocusable(true);
                lineView.setFocusableInTouchMode(true);
                lineView.setClickable(true);
                lineView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        urlTextItemInterface.UrlTextItemClick(viewUrlTxt.url,viewUrlTxt.urlName,viewUrlTxt.data);
                    }
                });
            }
        }
        // 分割线
        else if (objBase instanceof Obj_ViewDivider) {
            TextView tv = lineView.findViewById(R.id.textView1);
            tv.setVisibility(View.GONE);
            ImageView iv = lineView.findViewById(R.id.imageView1);
            iv.setVisibility(View.GONE);
            lineView.setBackgroundResource(R.color.mgary);
            lineView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 1));
        }
        // 文本域
        else if (objBase instanceof Obj_ViewTxtArea) {
            Obj_ViewTxtArea viewTxtArea = (Obj_ViewTxtArea) objBase;
            lineView.setBackgroundResource(R.drawable.textarea_bg_circle_sp_selector);
            TextView tv = lineView.findViewById(R.id.textView1);
            tv.setVisibility(View.VISIBLE);
            ImageView iv = lineView.findViewById(R.id.imageView1);
            iv.setVisibility(View.GONE);
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < viewTxtArea.content.length; i++) {
                sb.append(viewTxtArea.content[i]);
                // 最后一行的时候不添加换行符
                if (i != viewTxtArea.content.length - 1) {
//                sb.append("\n");
                    sb.append("<br/>");// html中的换行符
                }
            }
            tv.setTextSize(TextParamDeal.getSize(viewTxtArea.size[0]));
            tv.setTextColor(TextParamDeal.getColor(viewTxtArea.color[0]));
            tv.setLineSpacing(dip2px(3), 1);
            tv.setPadding(dip2px(15), dip2px(3), dip2px(3), dip2px(3));
            // 设置成超链接一样的效果
            tv.setText(Html.fromHtml("<u>" + sb.toString() + "</u>"));
            tv.setTextColor(Main.instance().context.getResources().getColor(R.color.mblue));

//		// 测试
//		view_MsgTxtArea.url = "http://172.16.1.117:8081/ZKService/main/myDocument.html";
//		// 测试
            if (StringUtil.isNotEmpty(viewTxtArea.url)) {
                iv.setImageResource(R.drawable.public_account_item_link);
                View.OnClickListener clickListener;// 跳转到地图还是跳转到链接
                PublicAccountURL publicAccountURL = PublicAccountURLFactory.getPublicAccountURL(viewTxtArea.url);
                switch (publicAccountURL.getUrlType()) {
                    case PublicAccountURL.URL_TYPE_RTMP:
                        lineView.setTag(publicAccountURL);
                        clickListener = onClickVideo;
                        break;
                    default:
                        Object objs[] = new Object[]{viewTxtArea.url, viewTxtArea.urlName};
                        lineView.setTag(objs);
                        clickListener = onClickListener;
                        ((PublicAccountURL.URL_SUPERLINK) publicAccountURL).name = viewTxtArea.urlName;
                }
                lineView.setOnClickListener(clickListener);
            }
        }
        return lineView;
    }

    /**
     * 获取整个渲染完成后的View
     *
     * @return view
     */
    public View getConvertView() {
        return convertView;
    }


    // 超链接监听
    private View.OnClickListener onClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {

            Object tag = v.getTag();
            String url = "";
            String name = "";
            if (tag instanceof PublicAccountURL.URL_SUPERLINK) {
                PublicAccountURL.URL_SUPERLINK superlink = (PublicAccountURL.URL_SUPERLINK) v.getTag();
                url = superlink.superLinkUrl;
                name = superlink.name;
            } else if (tag instanceof Object[]) {
                Object[] objs = (Object[]) v.getTag();
                url = (String) objs[0];
                name = (String) objs[1];
            } else {
                //Toast.makeText()
                return;
            }

            Intent intent = new Intent();
//            intent.putExtra("URL", superlink.superLinkUrl);
//            intent.putExtra("NAME", superlink.name);
            intent.putExtra("URL", url);
            intent.putExtra("NAME", name);
            intent.setClass(context, PAWebViewAct.class);
            context.startActivity(intent);
        }
    };

    // lbs地图跳转
    private View.OnClickListener onClickGoMap = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            PublicAccountURL.URL_GPS url_gps = (PublicAccountURL.URL_GPS) v.getTag();
            Intent intent = new Intent();
            if (url_gps.gpsList != null ) {
                //lbs新协议，先取list中第一个参数，之后可以做到多点
                intent.putExtra("LON", url_gps.gpsList.get(0).getLon());
                intent.putExtra("LAT", url_gps.gpsList.get(0).getLat());
            } else {
                // lbs旧协议兼容
                intent.putExtra("LON", url_gps.lon);
                intent.putExtra("LAT", url_gps.lat);
            }
            intent.setClass(context, LBSMapAct.class);
            context.startActivity(intent);

        }
    };

    // 播放语音
    private View.OnClickListener onClickPlay = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            PublicAccountURL.URL_MEDIA url_media = (PublicAccountURL.URL_MEDIA) v.getTag();
            RTMPEvent event = new RTMPEvent();
            event.postURL = url_media.postUrl;
            event.rtmpURL = url_media.rtmpUrl;
            event.urlName = url_media.name;
            EventBus.getDefault().post(event);
        }
    };

    private View.OnClickListener onClickVideo = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            PublicAccountURL.URL_RTMP url_rtmp = (PublicAccountURL.URL_RTMP) v.getTag();
//            Intent intent = new Intent();
//            intent.putExtra("videoPath", url_rtmp.rtmpUrl);
//            intent.setClass(context, VideoPlayerActivity.class);
//            context.startActivity(intent);
            Main.instance().handlerVideoPlayer.goVideoPlayerAct(activity, url_rtmp.rtmpUrl);

        }
    };
    private View.OnClickListener onClickMap = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Bundle mBundle = new Bundle();
            Intent intent = new Intent();
            intent.putExtra("publicGUID", publicGuid);
            mBundle.putSerializable("Obj_PulibcMsgDynamicLocation", dataObj);
            intent.putExtras(mBundle);
            intent.setClass(context, GisMapAct.class);
            context.startActivity(intent);
//            Intent intent=new Intent();
//            intent.setClass(context, Map_Ready.class);
//            context.startActivity(intent);

        }
    };
    private View.OnClickListener onClicTrailkMap = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            PublicAccountURL.URL_RTMP url_rtmp = (PublicAccountURL.URL_RTMP) v.getTag();
            Bundle mBundle = new Bundle();
            Intent intent = new Intent();
            intent.putExtra("publicGUID", publicGuid);
            mBundle.putSerializable("obj_publicMsgTrail", obj_publicMsgTrail);
            intent.putExtras(mBundle);
            intent.setClass(context, TrailMapAct.class);
            context.startActivity(intent);
//            Intent intent=new Intent();
//            intent.setClass(context, Map_Ready.class);
//            context.startActivity(intent);

        }
    };

    /**
     * k dp ^jλ px()
     */
    private int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public ArrayList<UserPointBean> getUserList(Obj_PulibcMsgDynamicLocation obj_pulibcMsgDynamicLocation) {
        ArrayList<UserPointBean> userlist = new ArrayList<UserPointBean>();//存放当前推送里面的人的ID
        for (Obj_PulibcMsgDynamicLocation.Point point : obj_pulibcMsgDynamicLocation.points) {
            if (point.type == 1) {
                UserPointBean userPointBeanSing = new UserPointBean();
                ArrayList<Obj_PublicMsgTrail.Point> points = new ArrayList<Obj_PublicMsgTrail.Point>();
                if (obj_pulibcMsgDynamicLocation.type == 1) {//敌方
                    Obj_PulibcMsgDynamicLocation.enemyTag enemyTag = (Obj_PulibcMsgDynamicLocation.enemyTag) point.tag;
                    userPointBeanSing.username = enemyTag.publicObject;
                } else if (obj_pulibcMsgDynamicLocation.type == 2) {//友方
                    Obj_PulibcMsgDynamicLocation.friendTag friendTag = (Obj_PulibcMsgDynamicLocation.friendTag) point.tag;
                    if (StringUtil.isEmpty(friendTag.policeNum)) {//警号为空，用equip中locId判断,是設備類型
                        userPointBeanSing.username = friendTag.equip.equipName;
                    } else {//警号不为空，用警号判断，是友方类型
                        userPointBeanSing.username = friendTag.name;
                    }
                }
                points.add(point);
                userPointBeanSing.points = points;
                userlist.add(userPointBeanSing);
            }
        }
        return userlist;
    }

    /***
     * 方位查询，LBS查询，话单查询，机主查询，公安网查询，电围查询
     *
     * @param dataObj
     */
    public void setonLongClick(Obj_PublicMsgPicTxt dataObj, LinearLayout linearLayout) {
        View.OnLongClickListener onClickListener = null;
        switch (dataObj.title) {
            case "方位查询":
                onClickListener = locationLongClick;
                break;
            case "LBS查询":
                onClickListener = lbsonLongClick;
                break;
            case "话单查询":
                onClickListener=cdrLongClick;
                break;
            case "机主信息":
               onClickListener=ownerLongClick;
                break;
            case "公安网查询":

                break;
            case "电围查询":
                onClickListener = theonLongClick;
                break;
        }
        linearLayout.setOnLongClickListener(onClickListener);
    }
    View.OnLongClickListener locationLongClick = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            Window window = DialogTool.dialog(context, R.layout.model_public_dialog);
            LinearLayout uploadIn = window.findViewById(R.id.uploadIntellgence);
            uploadIn.setOnClickListener(uploadInClick);
            uploadIn.setTag(LOCATION);
            return true;
        }
    };
    View.OnLongClickListener lbsonLongClick = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            Window window = DialogTool.dialog(context, R.layout.model_public_dialog);
            LinearLayout uploadIn = window.findViewById(R.id.uploadIntellgence);
            uploadIn.setOnClickListener(uploadInClick);
            uploadIn.setTag(LBS);
            return true;
        }
    };
    View.OnLongClickListener theonLongClick = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            Window window = DialogTool.dialog(context, R.layout.model_public_dialog);
            LinearLayout uploadIn = window.findViewById(R.id.uploadIntellgence);
            uploadIn.setOnClickListener(uploadInClick);
            uploadIn.setTag(DIANWEI);
            return true;
        }
    };
    View.OnLongClickListener ownerLongClick = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            Window window = DialogTool.dialog(context, R.layout.model_public_dialog);
            LinearLayout uploadIn = window.findViewById(R.id.uploadIntellgence);
            uploadIn.setOnClickListener(uploadInClick);
            uploadIn.setTag(OWNER);
            return true;
        }
    };
    View.OnLongClickListener cdrLongClick = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            Window window = DialogTool.dialog(context, R.layout.model_public_dialog);
            LinearLayout uploadIn = window.findViewById(R.id.uploadIntellgence);
            uploadIn.setOnClickListener(uploadInClick);
            uploadIn.setTag(CDR);
            return true;
        }
    };
    View.OnClickListener uploadInClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getTag().equals(LOCATION)) {
                locationChangeIntellgence(obj_publicMsgPicTxt,activity,publicGuid,"方位查询");
            } else if (v.getTag().equals(LBS)) {
                locationChangeIntellgence(obj_publicMsgPicTxt,activity,publicGuid,"LBS查询");
            } else if (v.getTag().equals(DIANWEI)) {
                urlChangeIntellgence(obj_publicMsgPicTxt,activity,publicGuid,"电围查询");
            } else if (v.getTag().equals(OWNER)) {
                urlChangeIntellgence(obj_publicMsgPicTxt,activity,publicGuid,"机主查询");
            }else if(v.getTag().equals(CDR)){
                urlChangeIntellgence(obj_publicMsgPicTxt,activity,publicGuid,"话单查询");
            }
//            Toast.makeText(context, "上报情报", Toast.LENGTH_LONG).show();
        }
    };




}


