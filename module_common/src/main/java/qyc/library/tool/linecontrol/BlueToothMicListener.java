package qyc.library.tool.linecontrol;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BlueToothMicListener {

    private Context context=null;
    public BlueToothMicListener(Context context)
    {
        this.context=context;
    }

    public void start()
    {
        context.registerReceiver(broadcastReceiver,null);
    }

    BroadcastReceiver  broadcastReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

        }
    };

    public void stop()
    {
        context.unregisterReceiver(broadcastReceiver);
    }

}
