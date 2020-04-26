package synway.module_publicaccount.map.view;


import android.app.Activity;
import android.app.ActionBar;
import android.app.FragmentTransaction;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.ExpandableListView;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import qyc.synjob.SynJobLoader;
import synway.common.download.SynDownload;
import synway.module_interface.config.BaseUtil;
import synway.module_interface.config.netConfig.NetConfig;
import synway.module_interface.config.netConfig.Sps_NetConfig;
import synway.module_publicaccount.R;
import synway.module_publicaccount.analytical.obj.Obj_PublicMsgBase;
import synway.module_publicaccount.analytical.obj.Obj_PublicMsgTrail;
import synway.module_publicaccount.analytical.obj.Obj_PulibcMsgDynamicLocation;
import synway.module_publicaccount.map.Adapter.DynamiclocationAdapter;
import synway.module_publicaccount.map.Adapter.UserlistExpandableAdapter;
import synway.module_publicaccount.map.Bean.GroupUserPoint;
import synway.module_publicaccount.map.Bean.UserPointBean;
import synway.module_publicaccount.map.DeleteDynamic;
import synway.module_publicaccount.map.RefreshAdapter;
import synway.module_publicaccount.map.SelectAllDynamicLocation;
import synway.module_publicaccount.push.PushUtil;
import synway.module_publicaccount.until.StringUtil;

import static synway.module_publicaccount.until.PicUtil.getIpPortFromUrl;

public class DynamicLocationList extends Activity {
    // ================广播
    private NewMsgReceiver onNewMsgReceive = null;
    private ViewPager viewPager;
    private View viewFriend, viewAnimy, viewEquip;
    private List<View> viewList;
    private List<String> titleList;
    private RadioGroup rgChannel = null;
    private HorizontalScrollView hvChannel;
    public static final String TOTAL_GROUP = "receiveact_total_group";
    public static final String READ_GROUP = "receiveact_read_group";
    //    private ListView listViewFrinend,listViewAnimy,listViewEquip;
    private ExpandableListView listViewFrinend, listViewAnimy, listViewEquip;
    private boolean flag = false;//是否所有的人已读
    //    private DynamiclocationAdapter friendViewAdapter;
//    private DynamiclocationAdapter animyViewAdapter;
//    private DynamiclocationAdapter equipViewAdapter;
    private UserlistExpandableAdapter friendViewAdapter;
    private UserlistExpandableAdapter animyViewAdapter;
    private UserlistExpandableAdapter equipViewAdapter;
    private SynJobLoader synJobLoader;
    private TextView btnok = null;
    private Obj_PulibcMsgDynamicLocation obj_pulibcMsgDynamicLocation;
    public String publicid;
    private NetConfig netConfig;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        obj_pulibcMsgDynamicLocation = (Obj_PulibcMsgDynamicLocation) getIntent().getSerializableExtra("Obj_PulibcMsgDynamicLocation");
        publicid = getIntent().getStringExtra("publicid");
        setContentView(R.layout.model_public_account_dynamic_location_list);
        initView();
        netConfig= Sps_NetConfig.getNetConfigFromSpf(this);
        //初始化任务加载器
        synJobLoader = new SynJobLoader(this, null);
        onNewMsgReceive = new NewMsgReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(PushUtil.PublicNewMsg.getGisAction(publicid));
        registerReceiver(onNewMsgReceive, filter);
        //加载查询数据库任务，查询用户的实时轨迹数据
        synJobLoader.loadJobLast("loadDynamicLocationDB", new SelectAllDynamicLocation(1, obj_pulibcMsgDynamicLocation));
        synJobLoader.loadJobLast("deleteDynamic", DeleteDynamic.class);
        synJobLoader.loadJobLast("refreshAdapter", new RefreshAdapter(friendViewAdapter, animyViewAdapter, equipViewAdapter));
//        synJobLoader.loadJobLast("refreshAdapter", new RefreshAdapter(friendViewAdapter,animyViewAdapter,equipViewAdapter));
        synJobLoader.synJobContext().startJob("loadDynamicLocationDB", "");

    }

    private void initView() {
//        friendViewAdapter = new DynamiclocationAdapter(this);
//        animyViewAdapter = new DynamiclocationAdapter(this);
//        equipViewAdapter = new DynamiclocationAdapter(this);
        friendViewAdapter = new UserlistExpandableAdapter(this);
        animyViewAdapter = new UserlistExpandableAdapter(this);
        equipViewAdapter = new UserlistExpandableAdapter(this);
        rgChannel = super.findViewById(R.id.rgChannel);
        hvChannel = super.findViewById(R.id.hvChannel);
        btnok = findViewById(R.id.btnok);
        btnok.setOnClickListener(onClickListener);
        rgChannel.setOnCheckedChangeListener(
                new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        viewPager.setCurrentItem(checkedId);
                        for (int i = 0; i < 3; i++) {
                            RadioButton rb = (RadioButton) rgChannel.getChildAt(i);
                            if (i == checkedId) {
                                rb.setTextColor(getResources().getColor(R.color.mgreen_light));
                            } else {
                                rb.setTextColor(Color.GRAY);
                            }
                        }
                    }
                });

        titleList = new ArrayList<>();
        titleList.add("友方");
        titleList.add("敌方");
        titleList.add("设备");
        ImageButton btnBack = findViewById(R.id.back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        viewPager = findViewById(R.id.reciver_viewpager);
        LayoutInflater inflater = LayoutInflater.from(this);
        //友方
        viewFriend = inflater.inflate(R.layout.model_public_account_activity_receiver_listview, null);
//        listViewFrinend = (ListView) viewFriend.findViewById(R.id.receiver_list);
        listViewFrinend = viewFriend.findViewById(R.id.receiver_list);
//        listViewFrinend.setGroupIndicator(null);// 去掉向下的箭頭
        listViewFrinend.setAdapter(friendViewAdapter);
        //敌方
        viewAnimy = inflater.inflate(R.layout.model_public_account_activity_receiver_listview, null);
//        listViewAnimy = (ListView) viewAnimy.findViewById.(R.id.receiver_list);
        listViewAnimy = viewAnimy.findViewById(R.id.receiver_list);
//        listViewAnimy.setGroupIndicator(null);// 去掉向下的箭頭
        listViewAnimy.setAdapter(animyViewAdapter);
        //设备
        viewEquip = inflater.inflate(R.layout.model_public_account_activity_receiver_listview, null);
//        listViewEquip = (ListView) viewEquip.findViewById(R.id.receiver_list);
        listViewEquip = viewEquip.findViewById(R.id.receiver_list);
//        listViewEquip.setGroupIndicator(null);// 去掉向下的箭頭
        listViewEquip.setAdapter(equipViewAdapter);
        viewList = new ArrayList<>();
        viewList.add(viewFriend);
        viewList.add(viewAnimy);
        viewList.add(viewEquip);
        viewPager.addOnPageChangeListener(pageChangeListener);
        initTab();//动态产生RadioButton
        viewPager.setAdapter(pagerAdapter);
        if (flag) {
            RadioButton rb = (RadioButton) rgChannel.getChildAt(1);
            rb.setChecked(true);
        } else {
            rgChannel.check(0);
        }

    }

    private void initTab() {
        for (int i = 0; i < titleList.size(); i++) {
            RadioButton rb = (RadioButton) LayoutInflater.from(this).inflate(R.layout.model_tab_radio, null);
            rb.setId(i);
            rb.setText(titleList.get(i));
            RadioGroup.LayoutParams params = new RadioGroup.LayoutParams(getResources().getDisplayMetrics().widthPixels / 3,
                    RadioGroup.LayoutParams.WRAP_CONTENT);
            rgChannel.addView(rb, params);
        }
    }

    ViewPager.OnPageChangeListener pageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            setTab(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    private void setTab(int idx) {
        RadioButton rb = (RadioButton) rgChannel.getChildAt(idx);
        rb.setChecked(true);
        int left = rb.getLeft();
        int width = rb.getMeasuredWidth();
        DisplayMetrics metrics = new DisplayMetrics();
        super.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int screenWidth = metrics.widthPixels;
        int len = left + width / 3 - screenWidth / 3;
        hvChannel.smoothScrollTo(len, 0);//滑动ScroollView
    }

    private PagerAdapter pagerAdapter = new PagerAdapter() {
        @Override
        public int getCount() {
            return viewList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
//            super.destroyItem(container, position, object);
            container.removeView(viewList.get(position));
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(viewList.get(position));
            return viewList.get(position);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titleList.get(position);
        }
    };

    @Override
    protected void onDestroy() {
        synJobLoader.removeAll();
        if (null != onNewMsgReceive) {
            unregisterReceiver(onNewMsgReceive);
        }
        super.onDestroy();
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.equals(btnok)) {//确定选中点
//                int length=friendViewAdapter.getCount();
//                ArrayList<GroupUserPoint> equipuserPointBeen=equipViewAdapter.getMlist();//选中的设备数据
                ArrayList<UserPointBean> friendPointBeen = getCheckGroupList(friendViewAdapter.getMlist());
                ArrayList<UserPointBean> animyuserPointBeen = getCheckGroupList(animyViewAdapter.getMlist());
                ArrayList<UserPointBean> equipuserPointBeen = getCheckGroupList(equipViewAdapter.getMlist());
                Log.i("testy", "得到的數據是");
                Intent intent = new Intent();
                intent.putExtra("friendPointBeens", friendPointBeen);
                intent.putExtra("animyuserPointBeens", animyuserPointBeen);
                intent.putExtra("equipuserPointBeens", equipuserPointBeen);
                setResult(3, intent);
                finish();
            }
        }
    };

    /****
     * 得到选中的人
     *
     * @param oldUserPointBeen
     * @return
     */
    public ArrayList<UserPointBean> getCheckList(ArrayList<UserPointBean> oldUserPointBeen) {
        ArrayList<UserPointBean> newUserPointBeens = new ArrayList<UserPointBean>();
        for (UserPointBean userPointBean : oldUserPointBeen) {
            if (userPointBean.isCheck) {//如果是被選中的
                newUserPointBeens.add(userPointBean);
            }
        }
        return newUserPointBeens;
    }

    /****
     * 从群组中得到选中的人
     *
     * @param oldGroupPointBeen
     * @return
     */
    public ArrayList<UserPointBean> getCheckGroupList(ArrayList<GroupUserPoint> oldGroupPointBeen) {
        ArrayList<UserPointBean> newUserPointBeens = new ArrayList<UserPointBean>();
        for (GroupUserPoint groupUserPoint : oldGroupPointBeen) {
            for (UserPointBean userPointBean : groupUserPoint.list_child) {
                if (userPointBean.isCheck) {//如果是被選中的
                    newUserPointBeens.add(userPointBean);
                }
            }
        }
        return newUserPointBeens;
    }

    public ArrayList<UserPointBean> getAllUserList(ArrayList<GroupUserPoint> oldGroupPointBeen) {
        ArrayList<UserPointBean> newUserPointBeens = new ArrayList<UserPointBean>();
        for (GroupUserPoint groupUserPoint : oldGroupPointBeen) {
            for (UserPointBean userPointBean : groupUserPoint.list_child) {
                newUserPointBeens.add(userPointBean);
            }
        }
        return newUserPointBeens;
    }

    /**
     * 新的公众推送消息
     */
    private class NewMsgReceiver extends BroadcastReceiver {
        long receiveLong;

        @Override
        public void onReceive(Context context, Intent intent) {
            receiveLong = System.currentTimeMillis();
            Obj_PulibcMsgDynamicLocation obj = (Obj_PulibcMsgDynamicLocation) intent.getSerializableExtra(PushUtil.PublicNewMsg.EXTRA_PUBLIC_NEWMSG_SOBJ);
            ArrayList<UserPointBean> useridListBeanAdd = getuserlist(obj);
            ArrayList<String> useridListDelelt = getuserlistDelelt(obj);//如果推送是删除点的
            ArrayList<UserPointBean> friendListBeans = new ArrayList<UserPointBean>();//新推送点的友方集合
            ArrayList<UserPointBean> animyListBeans = new ArrayList<UserPointBean>();//新推送点的敌方集合
            ArrayList<UserPointBean> equiopListBeans = new ArrayList<UserPointBean>();//新推送点的设备集合
            if(useridListBeanAdd.size()>0) {
                for (UserPointBean userPointBean : useridListBeanAdd) {
                    if (userPointBean.DataType==1&&userPointBean.type == 1) {//敌方
                        animyListBeans.add(userPointBean);
                    } else if (userPointBean.DataType==1&&userPointBean.type == 2) {//友方
                        friendListBeans.add(userPointBean);
                    } else if (userPointBean.DataType == 2) {//设备
                        equiopListBeans.add(userPointBean);
                    }
                }
            }
            ArrayList<UserPointBean> friendPointBeens = getAllUserList(friendViewAdapter.getMlist());
            ArrayList<UserPointBean> animyuserPointBeen = getAllUserList(animyViewAdapter.getMlist());
            ArrayList<UserPointBean> equipuserPointBeen = getAllUserList(equipViewAdapter.getMlist());
            if(useridListDelelt.size()>0){
                for (String userid : useridListDelelt) {
                  for(int i=0;i<friendPointBeens.size();i++){
                        if(userid.equals(friendPointBeens.get(i).userid)){
                            friendPointBeens.remove(i);
                        }
                    }
                    for(int i=0;i<animyuserPointBeen.size();i++){
                        if(userid.equals(animyuserPointBeen.get(i).userid)){
                            animyuserPointBeen.remove(i);
                        }
                    }
                    for(int i=0;i<equipuserPointBeen.size();i++){
                        if(userid.equals(equipuserPointBeen.get(i).userid)){
                            equipuserPointBeen.remove(i);
                        }
                    }
                }
            }
            if (friendPointBeens.size() > 0||friendListBeans.size()>0) {
                friendViewAdapter.resetMlist(getGroupUserList(getnewUserPointBeans(friendPointBeens, friendListBeans)));
                friendViewAdapter.refresh();
            }
            if (animyuserPointBeen.size() > 0||animyListBeans.size()>0) {
                animyViewAdapter.resetMlist(getGroupUserList(getnewUserPointBeans(animyuserPointBeen, animyListBeans)));
                animyViewAdapter.refresh();
            }
            if (equipuserPointBeen.size() > 0||equiopListBeans.size()>0) {
                equipViewAdapter.resetMlist(getGroupUserList(getnewUserPointBeans(equipuserPointBeen, equiopListBeans)));
                equipViewAdapter.refresh();
            }

        }

    }

    public ArrayList<UserPointBean> getnewUserPointBeans(ArrayList<UserPointBean> oldUserPointBeens, ArrayList<UserPointBean> pushUserbeans) {
        ArrayList<UserPointBean> newUserPointBeens = new ArrayList<UserPointBean>();
        for (UserPointBean userPointBean : pushUserbeans) {
            Boolean iffriend = false;
            for (UserPointBean friPointBean : oldUserPointBeens) {
                if (userPointBean.userid.equals(friPointBean.userid)) {
                    iffriend = true;
                    friPointBean.points = userPointBean.points;
                    friPointBean.picurl = userPointBean.picurl;
                    friPointBean.picurl = userPointBean.picurl;
                    friPointBean.username = userPointBean.username;
                }
            }
            if (!iffriend) {
                userPointBean.isCheck = false;
                oldUserPointBeens.add(userPointBean);
            }
        }
        return oldUserPointBeens;
    }

    public ArrayList<UserPointBean> getuserlist(Obj_PulibcMsgDynamicLocation obj) {        //收集当前推送的人
        ArrayList<UserPointBean> userlist = new ArrayList<UserPointBean>();//存放当前推送里面的人的ID
        if (obj.points.size() > 0) {        //所有的点，取出某一个点
            for (Obj_PulibcMsgDynamicLocation.Point p : obj.points) {
                UserPointBean userPointBeanSing = new UserPointBean();
                    if (p.type == 1) {
                        //如果是新增的点，不是要删除的，还是有有必要判断，虽然在推送的时候已经判断过一遍，0的时候删除这个点
                        if(obj.DataType==1){//普通数据
                            if (p.tag.type == 1) {//敵方 enemyTag
                                Obj_PulibcMsgDynamicLocation.enemyTag enemyTag = (Obj_PulibcMsgDynamicLocation.enemyTag) p.tag;
                                ArrayList<Obj_PublicMsgTrail.Point> pointtypelist = new ArrayList<Obj_PublicMsgTrail.Point>();
                                pointtypelist.add(p);
                                userPointBeanSing.userid = enemyTag.publiccase + enemyTag.number;
                                userPointBeanSing.username = enemyTag.publicObject;
                                userPointBeanSing.points = pointtypelist;
                                userPointBeanSing.DataType = 1;
                                userPointBeanSing.type = 1;
                                userPointBeanSing.group = enemyTag.publiccase;
                                if (p.pType == 2) {//有头像，没有则为空
                                    Obj_PulibcMsgDynamicLocation.Picture picture = (Obj_PulibcMsgDynamicLocation.Picture) p.pointInfo;
                                    userPointBeanSing.picurl = picture.picURL;

                                }
                            } else if (p.tag.type == 2) {//友方 friendTag
                                Obj_PulibcMsgDynamicLocation.friendTag friendTag = (Obj_PulibcMsgDynamicLocation.friendTag) p.tag;
                                    ArrayList<Obj_PublicMsgTrail.Point> pointtypelist = new ArrayList<>();
                                    pointtypelist.add(p);
                                    userPointBeanSing.userid =  friendTag.group+friendTag.policeNum;
                                    userPointBeanSing.username = friendTag.name;
                                    userPointBeanSing.points = pointtypelist;
                                    userPointBeanSing.DataType = 1;
                                    userPointBeanSing.type = 2;
                                    userPointBeanSing.group = friendTag.group;//可能为空
                                    if (p.pType == 2) {//有头像，没有则为空
                                        Obj_PulibcMsgDynamicLocation.Picture picture = (Obj_PulibcMsgDynamicLocation.Picture) p.pointInfo;
                                        userPointBeanSing.picurl = getIpPortFromUrl(picture.picURL, netConfig.functionIP, netConfig.functionPort);
                                    }

                            }
                        }else if(obj.DataType==2){//设备数据
                            ArrayList<Obj_PublicMsgTrail.Point> pointtypelist = new ArrayList<Obj_PublicMsgTrail.Point>();
                            pointtypelist.add(p);
                            userPointBeanSing.userid = p.tag.equip.locId;
                            userPointBeanSing.username = p.tag.equip.equipName;
                            userPointBeanSing.points = pointtypelist;
                            userPointBeanSing.DataType = 2;
                            userPointBeanSing.type = p.tag.type;
                            userPointBeanSing.group = p.group;//可能为空
                            if (p.pType == 2) {//有头像，没有则为空
                                Obj_PulibcMsgDynamicLocation.Picture picture = (Obj_PulibcMsgDynamicLocation.Picture) p.pointInfo;
                                userPointBeanSing.picurl = picture.picURL;
                            }
                        }


                    }
                    userlist.add(userPointBeanSing);

            }

        }
        return userlist;
    }
    public ArrayList<String> getuserlistDelelt(Obj_PulibcMsgDynamicLocation obj) {//收集当前推送中被删除的人
        ArrayList<String>  userlistDelet=new ArrayList<String>();
         for(Obj_PulibcMsgDynamicLocation.Point point:obj.points){
             if(point.type==0){
                 if (obj_pulibcMsgDynamicLocation.type == 1) {//敌方
                         Obj_PulibcMsgDynamicLocation.enemyTag enemyTag = (Obj_PulibcMsgDynamicLocation.enemyTag) point.tag;
                     userlistDelet.add(enemyTag.publiccase + enemyTag.number);
                 } else if (obj_pulibcMsgDynamicLocation.type == 2) {//友方
                         Obj_PulibcMsgDynamicLocation.friendTag friendTag = (Obj_PulibcMsgDynamicLocation.friendTag) point.tag;
                         if (StringUtil.isEmpty(friendTag.policeNum)) {//警号为空，用equip中locId判断,是設備類型
                             userlistDelet.add(friendTag.equip.locId);
                         } else {//警号不为空，用警号判断，是友方类型
                             userlistDelet.add(friendTag.policeNum);
                         }
                 }
             }
         }
        return  userlistDelet;
    }

    public ArrayList<GroupUserPoint> getGroupUserList(ArrayList<UserPointBean> userPointBeens) {
        ArrayList<GroupUserPoint> groupUserPoints = new ArrayList<GroupUserPoint>();
        for (UserPointBean userPointBean : userPointBeens) {
            if (StringUtil.isNotEmpty(userPointBean.group)) {//分组信息不为空
                if (groupUserPoints.size() == 0) {//数列里面还没有分组数据
                    GroupUserPoint groupUserPoint = new GroupUserPoint();
                    groupUserPoint.groupName = userPointBean.group;
                    groupUserPoint.list_child.add(userPointBean);
                    groupUserPoints.add(groupUserPoint);
                } else {//数列不是空的，循环数列把当前的用户加到该组里
//                 groupUserPoints=addGroupUser(groupUserPoints,userPointBean,userPointBean.group);
                    Boolean ifexit = false;//标识是否存在这个组
                    for (GroupUserPoint groupUserPoint : groupUserPoints) {
                        if (StringUtil.isNotEmpty(groupUserPoint.groupName) && groupUserPoint.groupName.equals(userPointBean.group)) {
                            groupUserPoint.list_child.add(userPointBean);
                            ifexit = true;
                        }
                    }
                    if (!ifexit) {//如果组里不存在这个组，則創建
                        List<UserPointBean> list_child = new ArrayList<UserPointBean>();
                        list_child.add(userPointBean);
                        GroupUserPoint groupUserPoint = new GroupUserPoint();
                        groupUserPoint.groupName = userPointBean.group;
                        groupUserPoint.list_child = list_child;
                        groupUserPoints.add(groupUserPoint);
                    }
                }
            } else {//分组信息为空
//                groupUserPoints=addGroupUser(groupUserPoints,userPointBean,"无分组");
                Boolean ifexit = false;//标识是否存在这个组
                for (GroupUserPoint groupUserPoint1 : groupUserPoints) {
                    if (groupUserPoint1.groupName.equals("无分组")) {
                        groupUserPoint1.list_child.add(userPointBean);
                        ifexit = true;
                    }
                }
                if (!ifexit) {  //如果组里不存在这个组，則創建)
                    List<UserPointBean> list_child = new ArrayList<UserPointBean>();
                    list_child.add(userPointBean);
                    GroupUserPoint groupUserPoint = new GroupUserPoint();
                    groupUserPoint.list_child = list_child;
                    groupUserPoint.groupName = "无分组";
                    groupUserPoints.add(groupUserPoint);
                }

            }
        }
        for (GroupUserPoint groupUserPoint : groupUserPoints) {
            Boolean ifcheck = true;
            for (UserPointBean userPointBean : groupUserPoint.list_child) {
                if (!userPointBean.isCheck) {//如果有一个没有选中，就整组不选中
                    ifcheck = false;
                }
            }
            groupUserPoint.ifcheck = ifcheck;
        }
        return groupUserPoints;
    }
}
