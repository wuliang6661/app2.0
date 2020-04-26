package synway.module_publicaccount.public_chat.adapter;

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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.label.library.LabelView;
import com.amulyakhare.textdrawable.TextDrawable;
import com.nex3z.flowlayout.FlowLayout;
import java.util.ArrayList;
import synway.common.Dip2PXDeal;
import synway.module_publicaccount.R;
import synway.module_publicaccount.analytical.obj.Obj_PublicMsgBase;
import synway.module_publicaccount.analytical.obj.Obj_PublicMsgTaskNotice;
import synway.module_publicaccount.analytical.obj.noticeview.ContentItem;
import synway.module_publicaccount.analytical.obj.noticeview.SectionItem;
import synway.module_publicaccount.analytical.obj.noticeview.TagItem;
import synway.module_publicaccount.notice.ColorParseUtil;
import synway.module_publicaccount.until.ParseTime;

/**
 * Created by leo on 2019/1/17.
 */

public class ViewHolder_TaskNotice extends AdapterTypeRender {

    private Context context;
    private View convertView;
    private TextView tvTitle;
    private ImageView ivHead;
    private FlowLayout flowLayoutTopTag;
    private FlowLayout flowLayoutBottomTag;
    private LinearLayout llTextArea;
    private LabelView labelView;
    private RelativeLayout rlContent;
    private TextView tvSDFTime;
    private View viewPlaceholder;
    public ViewHolder_TaskNotice(Context context,IOnPublicChatItemClick onPublicChatItemClick) {
        super(context, onPublicChatItemClick);
        this.context = context;
        convertView = LayoutInflater.from(context).inflate(R.layout.model_public_task_notice_list_item,null);
    }


    @Override View getConvertView() {
        return convertView;
    }


    @Override void initView() {
        ivHead = convertView.findViewById(R.id.iv_head);
        tvTitle = convertView.findViewById(R.id.tv_title);
        flowLayoutTopTag = convertView.findViewById(R.id.flowLayout1);
        flowLayoutBottomTag = convertView.findViewById(R.id.flowLayout2);
        llTextArea = convertView.findViewById(R.id.ll_textArea);
        labelView = convertView.findViewById(R.id.lableView);
        rlContent = convertView.findViewById(R.id.rl_content);
        tvSDFTime = convertView.findViewById(R.id.tv_sdfTime);
        viewPlaceholder = convertView.findViewById(R.id.view_placeholder);

    }


    @Override void setData(int position, Obj_PublicMsgBase obj) {
        
        Obj_PublicMsgTaskNotice taskNotice = (Obj_PublicMsgTaskNotice) obj;
        //标题 和 头像
        if (TextUtils.isEmpty(taskNotice.title)) {
            tvTitle.setText("无");
            TextDrawable drawable = TextDrawable.builder()
                .beginConfig()
                .fontSize(toSp(13))
                .endConfig()
                .buildRound("无", Color.parseColor("#32B16C"));
            ivHead.setImageDrawable(drawable);
        } else {
            tvTitle.setText(taskNotice.title);
            String textDraw = taskNotice.title;
            if (textDraw.length() > 2) {
                textDraw = textDraw.substring(0, 2);
            }
            TextDrawable drawable = TextDrawable.builder()
                .beginConfig()
                .fontSize(toSp(14))
                .endConfig()
                .buildRound(textDraw, Color.parseColor("#32B16C"));
            ivHead.setImageDrawable(drawable);
        }

        //时间
        tvSDFTime.setText(ParseTime.parseDate(taskNotice.localTime));

        //三角标签
        if (taskNotice.triangleTag == null) {
            labelView.setVisibility(View.GONE);
            viewPlaceholder.setVisibility(View.GONE);
        } else {
            if(TextUtils.isEmpty(taskNotice.triangleTag.tagText) ||
                TextUtils.isEmpty(taskNotice.triangleTag.bgColor)){
                labelView.setVisibility(View.GONE);
                viewPlaceholder.setVisibility(View.GONE);
            }else {
                labelView.setVisibility(View.VISIBLE);
                viewPlaceholder.setVisibility(View.VISIBLE);
                String labelText = taskNotice.triangleTag.tagText;
                String labelColor = taskNotice.triangleTag.bgColor;
                if (labelText.length() > 2) {
                    labelText = labelText.substring(0, 2);
                }
                labelView.setTextContent(labelText);
                labelView.setLabelBackGroundColor(ColorParseUtil.parseLabel(labelColor));
            }
        }

        //顶端标签
        if (taskNotice.topTags == null) {
            flowLayoutTopTag.setVisibility(View.GONE);
        }else {
            flowLayoutTopTag.setVisibility(View.VISIBLE);
            //在加之前先remove掉里面子view，不然滑动的时候会无限加
            flowLayoutTopTag.removeAllViews();
            ArrayList<TagItem> topTagList = taskNotice.topTags;
            for (TagItem tagItem : topTagList) {
                if (TextUtils.isEmpty(tagItem.tagText)) {
                    continue;
                } else {
                    TextView textView = getTag(tagItem.tagText, tagItem.bgColor);
                    flowLayoutTopTag.addView(textView);
                }
            }
        }

        //底端标签
        if (taskNotice.bottomTags == null) {
            flowLayoutBottomTag.setVisibility(View.GONE);
        } else {
            flowLayoutBottomTag.setVisibility(View.VISIBLE);
            flowLayoutBottomTag.removeAllViews();
            ArrayList<TagItem> bottomTagList = taskNotice.bottomTags;
            for (TagItem tagItem : bottomTagList) {
                if (TextUtils.isEmpty(tagItem.tagText)) {
                    continue;
                } else {
                    TextView textView = getTag(tagItem.tagText, tagItem.bgColor);
                    flowLayoutBottomTag.addView(textView);
                }
            }
        }

        //中间文本内容
        if (taskNotice.sections == null) {
            llTextArea.setVisibility(View.GONE);
        } else {
            llTextArea.setVisibility(View.VISIBLE);
            llTextArea.removeAllViews();
            ArrayList<SectionItem> sectionItems = taskNotice.sections;

            for (SectionItem sectionItem : sectionItems) {
                if(sectionItem.type == 1){
                    //文字段落
                    if (sectionItem.contentItem == null) {
                        continue;
                    } else {
                        TextView section = getSection(sectionItem.contentItem);
                        if (section != null) {
                            llTextArea.addView(section);
                        }
                    }
                } else if (sectionItem.type == 2) {
                    //分隔符
                    View divider = getDivider();
                    llTextArea.addView(divider);
                } else {
                    continue;
                }
            }

        }

        if (taskNotice.clickItem != null) {
            rlContent.setTag(position);
            rlContent.setOnClickListener(onClickListener);
        }
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override public void onClick(View v) {
            if (v == rlContent) {
                int position = (Integer) v.getTag();
                onPublicChatItemClick.onTaskNoticeItemClick(position);
            }
        }
    };

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

    private View getDivider(){
        View view = new View(context);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
            Dip2PXDeal.dip2px(context,(float) 0.5));
        layoutParams.setMargins(0,Dip2PXDeal.dip2px(context,(float) 2),0,Dip2PXDeal.dip2px(context,(float) 2));
        view.setLayoutParams(layoutParams);
        view.setBackgroundColor(Color.parseColor("#D9D9D9"));
        return view;
    }

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
}
