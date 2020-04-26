package synway.module_publicaccount.push;

import android.util.Log;
import qyc.net.push.synwayoscpush.SPushFacInterface;
import synway.module_interface.sharedate.SharedPreferencesQ;

/**
 * 公众号更新
 * Created by zjw on 2019/1/20.
 */

public class onPublicUpdate extends SPushFacInterface {

    private SharedPreferencesQ sharedPreferencesQ = null;

    @Override
    public int[] regist() {
        return new int[]{5100};
    }

    @Override
    public void onCreat(Object o) {
        sharedPreferencesQ= new SharedPreferencesQ(context, "isPublicUpdate");
    }

    @Override
    public void onDestory() {

    }

    @Override
    public void onReceive(int i, String s) {
        //添加被修改的标识.
        sharedPreferencesQ.put("isPublicUpdate", true);
        Log.i("zjw","onReceive(5100)");
    }
}
