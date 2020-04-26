package synway.module_publicaccount.map.publicrtgis;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;

import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


/**
 * Created by 13itch on 2016/7/18.
 */
public class RTGISPresenterImp implements RTGISPresenterI {
    private Handler handler;
    private final static long removeTime=10000;
    private  RTGISViewI rtgisViewI;
    private List<Obj_RTGis_Point> list=null;
    private Context context;
    private Timer timer=null;
    public RTGISPresenterImp(Context context, RTGISViewI rtgisViewI){
        handler=new Handler();
        this.context=context;
        this.rtgisViewI=rtgisViewI;
    }
    @Override
    public void start(String PublicGUID) {
        getDataFromDB(PublicGUID);
        receivePush();
        removeByTimer();
    }

    @Override
    public void getDataFromDB(String PublicGUID) {
        list= Open_PublicAccountRTGis.getPointsByDB(PublicGUID);
        if(rtgisViewI!=null){
            rtgisViewI.setPoint(list);
        }
    }

    @Override
    public void receivePush() {
        if(context!=null){
            context.registerReceiver(RTGISPointBC,new IntentFilter(Open_PublicAccountRTGis.ACTION_GIS_POINTS));
        }
    }

    @Override
    public void removeByTimer() {
        timer=new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if(rtgisViewI!=null){
                    long nowTime=new Date().getTime()/1000;
                    for(final Obj_RTGis_Point point:list){
                        long time=Long.parseLong(point.time);
                        if(nowTime-(Open_PublicAccountRTGis.shuaxinjiangeTime*1000)>time){
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    rtgisViewI.removePoint(point);
                                }
                            });
                        }
                    }
                }
            }
        },0,removeTime);
    }

    @Override
    public void stop() {
        if(context!=null){
            context.unregisterReceiver(RTGISPointBC);
        }
        if(timer!=null){
            timer.cancel();
        }
        rtgisViewI=null;
    }

    private BroadcastReceiver RTGISPointBC =new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //在此处暂时不做超时point的remove处理，而是交由Timer来定时处理。
            String publicGUID=intent.getStringExtra(Open_PublicAccountRTGis.EX_PUBLIC_GUID);
            long time=intent.getLongExtra(Open_PublicAccountRTGis.EX_TIME,0);
            String MSG_INFO=intent.getStringExtra(Open_PublicAccountRTGis.EX_GIS_MSG_INFO);
            List<Obj_RTGis_Point> newlist=Open_PublicAccountRTGis.parseDataFromString(MSG_INFO,publicGUID,time+"");

            if(rtgisViewI!=null){
                for(Obj_RTGis_Point new_point:newlist){
                    if(new_point.type==0){
                        rtgisViewI.removePoint(new_point);
                    }else if(new_point.type==1){
                            rtgisViewI.addPoint(new_point);
                    }
                }
            }
//            if(rtgisViewI!=null){
//                List<Obj_RTGis_Point> oldlist=rtgisViewI.getOldPointList();
//                for(Obj_RTGis_Point new_point:list){
//                    if(new_point.type==0){
//                        for(Obj_RTGis_Point oldObj:oldlist){
//                            if(oldObj.ID.equals(new_point.ID)){
//                                oldlist.remove(oldObj);
//                                break;
//                            }
//                        }
//                        continue;
//                    }else{
//                        if(new_point.type==1){
//                            boolean iscunzai=false;
//                            for(int i=0;i<oldlist.size();i++){
//                                if(oldlist.get(i).ID.equals(new_point.ID)){
//                                    iscunzai=true;
//                                    oldlist.set(i,new_point);
//                                    break;
//                                }
//                            }
//                            if(!iscunzai){
//                                oldlist.add(new_point);
//                            }
//                        }
//                        continue;
//                    }
//                }
//                rtgisViewI.setPoint(oldlist);
//            }
        }
    };
}
