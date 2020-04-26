package synway.module_publicaccount.notice;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.label.library.LabelView;
import com.amulyakhare.textdrawable.TextDrawable;
import com.nex3z.flowlayout.FlowLayout;
import java.util.ArrayList;
import java.util.HashMap;
import synway.common.Dip2PXDeal;
import synway.module_publicaccount.R;
import synway.module_publicaccount.analytical.obj.Obj_PublicMsgNotice;
import synway.module_publicaccount.analytical.obj.noticeview.ContentItem;
import synway.module_publicaccount.analytical.obj.noticeview.SectionItem;
import synway.module_publicaccount.analytical.obj.noticeview.TagItem;
import synway.module_publicaccount.until.ParseTime;

/**
 * Created by leo on 2018/10/23.
 */

public class NoticeAdapter extends BaseAdapter {


    private ArrayList<Obj_PublicMsgNotice> list;
    private HashMap<String,Obj_PublicMsgNotice> sparseArray;


    private Context context;
    private NoticeItemClick noticeItemClick;


    public NoticeAdapter(Context context,NoticeItemClick noticeItemClick) {
        this.context = context;
        this.noticeItemClick = noticeItemClick;
        this.list = new ArrayList<>();
        this.sparseArray = new HashMap<>();
    }


    @Override public int getCount() {
        return list.size();
    }


    public Obj_PublicMsgNotice getObjByPosition(int position) {
        return list.get(position);
    }


    @Override public Object getItem(int position) {
        return list.get(position);
    }


    @Override public long getItemId(int position) {
        return position;
    }


    @Override public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        Obj_PublicMsgNotice noticeObj = list.get(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(context)
                .inflate(R.layout.notice_list_item, parent, false);

            viewHolder = getViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        setData(noticeObj,viewHolder,position);

        return convertView;
    }


    private void setData(Obj_PublicMsgNotice noticeObj, ViewHolder viewHolder, int position) {


        //标题 和 头像
        if (TextUtils.isEmpty(noticeObj.title)) {
            viewHolder.tvTitle.setText("无");
            TextDrawable drawable = TextDrawable.builder()
                .beginConfig()
                .fontSize(toSp(13))
                .endConfig()
                .buildRound("无", Color.parseColor("#32B16C"));
            viewHolder.ivHead.setImageDrawable(drawable);
        } else {
            viewHolder.tvTitle.setText(noticeObj.title);
            String textDraw = noticeObj.title;
            if (textDraw.length() > 2) {
                textDraw = textDraw.substring(0, 2);
            }
            TextDrawable drawable = TextDrawable.builder()
                .beginConfig()
                .fontSize(toSp(14))
                .endConfig()
                .buildRound(textDraw, Color.parseColor("#32B16C"));
            viewHolder.ivHead.setImageDrawable(drawable);
        }

        //时间
        viewHolder.tvSDFTime.setText(ParseTime.parseDate(noticeObj.localTime));


        //三角标签
        if (noticeObj.triangleTag == null) {
            viewHolder.labelView.setVisibility(View.GONE);
            viewHolder.viewPlaceholder.setVisibility(View.GONE);
        } else {
            if(TextUtils.isEmpty(noticeObj.triangleTag.tagText) ||
                TextUtils.isEmpty(noticeObj.triangleTag.bgColor)){
                viewHolder.labelView.setVisibility(View.GONE);
                viewHolder.viewPlaceholder.setVisibility(View.GONE);
            }else {
                viewHolder.labelView.setVisibility(View.VISIBLE);
                viewHolder.viewPlaceholder.setVisibility(View.VISIBLE);
                String labelText = noticeObj.triangleTag.tagText;
                String labelColor = noticeObj.triangleTag.bgColor;
                if (labelText.length() > 2) {
                    labelText = labelText.substring(0, 2);
                }
                viewHolder.labelView.setTextContent(labelText);
                viewHolder.labelView.setLabelBackGroundColor(ColorParseUtil.parseLabel(labelColor));
            }
        }

        //顶端标签
        if (noticeObj.topTags == null) {
            viewHolder.flowLayoutTopTag.setVisibility(View.GONE);
        }else {
            viewHolder.flowLayoutTopTag.setVisibility(View.VISIBLE);
            //在加之前先remove掉里面子view，不然滑动的时候会无限加
            viewHolder.flowLayoutTopTag.removeAllViews();
            ArrayList<TagItem> topTagList = noticeObj.topTags;
            for (TagItem tagItem : topTagList) {
                if (TextUtils.isEmpty(tagItem.tagText)) {
                    continue;
                } else {
                    TextView textView = getTag(tagItem.tagText, tagItem.bgColor);
                    viewHolder.flowLayoutTopTag.addView(textView);
                }
            }
        }

        //底端标签
        if (noticeObj.bottomTags == null) {
            viewHolder.flowLayoutBottomTag.setVisibility(View.GONE);
        } else {
            viewHolder.flowLayoutBottomTag.setVisibility(View.VISIBLE);
            viewHolder.flowLayoutBottomTag.removeAllViews();
            ArrayList<TagItem> bottomTagList = noticeObj.bottomTags;
            for (TagItem tagItem : bottomTagList) {
                if (TextUtils.isEmpty(tagItem.tagText)) {
                    continue;
                } else {
                    TextView textView = getTag(tagItem.tagText, tagItem.bgColor);
                    viewHolder.flowLayoutBottomTag.addView(textView);
                }
            }
        }

        //中间文本内容
        if (noticeObj.sections == null) {
            viewHolder.llTextArea.setVisibility(View.GONE);
        } else {
            viewHolder.llTextArea.setVisibility(View.VISIBLE);
            viewHolder.llTextArea.removeAllViews();
            ArrayList<SectionItem> sectionItems = noticeObj.sections;

            for (SectionItem sectionItem : sectionItems) {
                if(sectionItem.type == 1){
                    //文字段落
                    if (sectionItem.contentItem == null) {
                        continue;
                    } else {
                        TextView section = getSection(sectionItem.contentItem);
                        if (section != null) {
                            viewHolder.llTextArea.addView(section);
                        }
                    }
                } else if (sectionItem.type == 2) {
                    //分隔符
                    View divider = getDivider();
                    viewHolder.llTextArea.addView(divider);
                } else {
                    continue;
                }
            }

        }

        if (noticeObj.clickItem != null) {
            viewHolder.rlContent.setTag(position);
            viewHolder.rlContent.setOnClickListener(onClickListener);
        }
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override public void onClick(View v) {
            if (v.getId() == R.id.rl_content) {
                int position = (Integer) v.getTag();
                noticeItemClick.onItemClick(position);
            }
        }
    };
    private int toSp(int size) {
        Resources resources = context.getResources();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, size, resources.getDisplayMetrics());
    }

    private TextView getSection(ContentItem contentItem) {
        TextView textView = new TextView(context);
        if (TextUtils.isEmpty(contentItem.contentText)) {
            return null;
        } else {
            textView.setText(contentItem.contentText);
        }
        if (!TextUtils.isEmpty(contentItem.contentColor)) {
            textView.setTextColor(ColorParseUtil.parseFont(contentItem.contentColor));
        }
        if (contentItem.contentSize > 0) {
            textView.setTextSize(contentItem.contentSize);
        } else {
            textView.setTextSize(16);
        }

        if (contentItem.isContentBold) {
            textView.setTypeface(Typeface.DEFAULT,Typeface.BOLD);
        }

        if (contentItem.isContentItalic) {
            textView.setTypeface(Typeface.DEFAULT,Typeface.ITALIC);
        }

        if(contentItem.isContentBold && contentItem.isContentItalic){
            textView.setTypeface(Typeface.DEFAULT,Typeface.BOLD_ITALIC);
        }

        if (contentItem.isContentUnderline) {
            textView.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG );
        }

        return textView;
    }

    private View getDivider(){
        View view = new View(context);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
            Dip2PXDeal.dip2px(context,(float) 0.5));
        layoutParams.setMargins(0, Dip2PXDeal.dip2px(context,(float) 2),0,
            Dip2PXDeal.dip2px(context,(float) 2));
        view.setLayoutParams(layoutParams);
        view.setBackgroundColor(Color.parseColor("#D9D9D9"));
        return view;
    }


    private ViewHolder getViewHolder(View convertView){
        ViewHolder viewHolder = new ViewHolder();
        viewHolder.ivHead = convertView.findViewById(R.id.iv_head);
        viewHolder.tvTitle = convertView.findViewById(R.id.tv_title);
        viewHolder.flowLayoutTopTag = convertView.findViewById(R.id.flowLayout1);
        viewHolder.flowLayoutBottomTag = convertView.findViewById(
            R.id.flowLayout2);
        viewHolder.llTextArea = convertView.findViewById(R.id.ll_textArea);
        viewHolder.labelView = convertView.findViewById(R.id.lableView);
        viewHolder.rlContent = convertView.findViewById(R.id.rl_content);
        viewHolder.tvSDFTime = convertView.findViewById(R.id.tv_sdfTime);
        viewHolder.viewPlaceholder = convertView.findViewById(R.id.view_placeholder);
        return viewHolder;
    }


    public void addNewItem(Obj_PublicMsgNotice notice) {
        this.list.add(notice);
        this.sparseArray.put(notice.noticeMsgID, notice);
    }

    public void addNewItemFromTop(Obj_PublicMsgNotice notice){
        this.list.add(0,notice);
        this.sparseArray.put(notice.noticeMsgID,notice);
    }


    public void refresh() {
        this.notifyDataSetChanged();
    }


    public void reset(ArrayList<Obj_PublicMsgNotice> list) {
        for (Obj_PublicMsgNotice obj_publicMsgNotice : list) {
            this.addNewItem(obj_publicMsgNotice);
        }
    }

    private class ViewHolder{
        TextView tvTitle;
        ImageView ivHead;
        FlowLayout flowLayoutTopTag;
        FlowLayout flowLayoutBottomTag;
        LinearLayout llTextArea;
        LabelView labelView;
        RelativeLayout rlContent;
        TextView tvSDFTime;
        View viewPlaceholder;
    }


    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    private TextView getTag(String tag, String bgcolor) {
        TextView textView = new TextView(context);
        textView.setTextSize(14);
        textView.setBackgroundResource(R.drawable.tag_item);
        if(!TextUtils.isEmpty(bgcolor)){
            GradientDrawable drawable = (GradientDrawable) textView.getBackground().mutate();
            drawable.setColor(ColorParseUtil.parseTag(bgcolor));
            textView.setBackgroundDrawable(drawable);
        }
        textView.setTextColor(Color.parseColor("#ffffff"));
        textView.setText(tag);
        return textView;
    }
}
