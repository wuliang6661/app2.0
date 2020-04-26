package cn.synway.app.ui.main.publicappcenter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;
import java.util.Map;

import cn.synway.app.bean.NativeBo;
import cn.synway.app.bean.NetAPPBO;
import cn.synway.app.bean.event.FaceAuthEvent;
import cn.synway.app.config.Config;
import cn.synway.app.db.dbmanager.UserIml;
import cn.synway.app.db.table.UserEntry;
import cn.synway.app.ui.messagedetails.MessageDetailsActivity;
import cn.synway.app.ui.recognize.YiTuLivenessActivity;
import cn.synway.app.ui.web.SynWebBuilder;
import cn.synway.app.ui.weex.entity.WxMapData;
import cn.synway.app.widget.lgrecycleadapter.OnRVItemClickListener;
import im.utils.StringUtil;
import synway.module_stack.hm_leave.PersonListAct;

public class PublicAccountItemInterface implements OnRVItemClickListener<NetAPPBO> {
    private Context context;
    private NetAPPBO mNetAPPBO;

    PublicAccountItemInterface() {
        EventBus.getDefault().register(this);
    }

    @Override
    public void ItemClick(View view, int position, NetAPPBO entity) {
        this.context = view.getContext();
        faceNeed(view.getContext(), entity);
        if (entity.getNoReadCount() > 0) {
            //TODO:HttpServerImpl.setMessageRead()
        }
    }

    /**
     * 是否需要活体检测
     */
    private void faceNeed(Context context, NetAPPBO entity) {
        mNetAPPBO = entity;
        if (Config.isOpenLive && entity.getFaceNeed() == 1) {
            YiTuLivenessActivity.startForApplicationCenter(context);
        } else {
            handlerClick(context, entity);
        }
    }


    public void handlerClick(Context context, NetAPPBO entity) {

        String businessType = entity.getBusinessType();
        if (StringUtil.isEmpty(businessType)) {
            ToastUtils.showShort("未知类型！");
            return;
        }
        int type = Integer.valueOf(businessType);
        UserEntry userBO = UserIml.getUser();

        if (type == 0) {
            //跳转html页面
            String url;
            url = entity.getBusinessUrl() + "?userName=" + userBO.getUserName() + "&phoneNumber=" +
                    userBO.getMobilePhoneNo() + "&userID=" + userBO.getUserID() + "&loginCode=" + userBO.getCode() +
                    "&LoginOragian=" + userBO.getOrganName() + "&LoginOragianCode=" + userBO.getOrgan();
            SynWebBuilder.builder()
                    .setUrl(url)
                    .setId(entity.getId())
                    .setName(entity.getName())
                    .setIsShowTitle(Integer.valueOf(entity.getIsDisplayBanner()))
                    .setUrlParam(entity.getSourceUrl())
                    .start(context);
        } else if (type == 1) {
            //跳转weex页面
            String url = entity.getBusinessUrl();
            Intent intent = new Intent(Intent.ACTION_VIEW);
            StringBuilder builder = new StringBuilder();
            builder.append(url);
            Uri uri = Uri.parse(builder.toString());
            if (TextUtils.equals("http", uri.getScheme()) || TextUtils.equals("https", uri.getScheme())) {
                intent.setData(uri);
                intent.putExtra("Title", entity.getName());
                intent.putExtra("id", entity.getId());
                intent.putExtra("IsShowTitle", Integer.valueOf(entity.getIsDisplayBanner()));
                intent.addCategory(Intent.CATEGORY_DEFAULT);
                intent.addCategory("com.SynwayOsc.android.intent.category.WEEX");
                Map<String, Object> params = new HashMap<>();
                params.put("SourceUrl", entity.getSourceUrl());
                params.put("UserId", userBO.getUserID());
                params.put("PaId", entity.getId());
                WxMapData map = new WxMapData();
                map.setWxMapData(params);
                intent.putExtra("DATA", map);
                context.startActivity(intent);
            } else {
                Toast.makeText(context, "跳转地址格式错误,请检查格式", Toast.LENGTH_LONG).show();
            }

        } else if (type == 2) {
            //本地原生，

            if (StringUtils.isEmpty(entity.getSourceUrl())) {
                Intent intent = new Intent(context, MessageDetailsActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("fcName", entity.getName());
                bundle.putString("fcId", entity.getId());
                intent.putExtras(bundle);
                context.startActivity(intent);
            } else {
                NativeBo nativeBo = new Gson().fromJson(entity.getSourceUrl(), NativeBo.class);
                if (nativeBo.getType() == 1) {   //证件识别
                    //Intent intent = new Intent(context, DocumentActivity.class);
                    //context.startActivity(intent);
                } else if (nativeBo.getType() == 2) {   //请销假
                    Intent intent = new Intent(context, PersonListAct.class);
                    context.startActivity(intent);
                } else if (nativeBo.getType() == 3) {  //通知消息
                    Intent intent = new Intent(context, MessageDetailsActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("fcName", entity.getName());
                    bundle.putString("fcId", entity.getId());
                    intent.putExtras(bundle);
                    context.startActivity(intent);
                } else {
                    Toast.makeText(context, "请先在配置中心完善配置参数！", Toast.LENGTH_LONG).show();
                }
            }

        }


    }

    /**
     * 活体登录成功
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void liveSuress(FaceAuthEvent event) {
        if (event.isSuress && mNetAPPBO != null) {
            handlerClick(context, mNetAPPBO);
        }
    }
}
