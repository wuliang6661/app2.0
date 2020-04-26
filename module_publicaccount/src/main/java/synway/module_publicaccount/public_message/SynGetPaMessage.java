package synway.module_publicaccount.public_message;

import android.content.Context;
import android.os.Handler;

import java.util.ArrayList;

/**
 * Created by huangxi
 * DATE :2019/1/18
 * Description ：
 */

public class SynGetPaMessage {

    private OnGetPaMessageListen onGetPaMessageListen = null;
    private Context context = null;
    private Handler handler = new Handler();

    public SynGetPaMessage(Context context) {
        this.context = context;
    }

    private Runnable runnable = new Runnable() {

        @Override
        public void run() {
            final ArrayList<ObjPaMessage> paMessageArrayList = new ArrayList<>();
            PublicMessage.get(context, paMessageArrayList);
            handler.post(new Runnable() {

                @Override
                public void run() {
                    if (onGetPaMessageListen != null) {
                        onGetPaMessageListen.onGet(paMessageArrayList);
                    }
                }
            });
        }
    };

    public void start() {
        new Thread(runnable).start();
    }

    public void setOnListen(OnGetPaMessageListen onGetPaMessageListen) {
        this.onGetPaMessageListen = onGetPaMessageListen;
    }

    public void stop() {
        this.onGetPaMessageListen = null;
    }

    public interface OnGetPaMessageListen {
        void onGet(ArrayList<ObjPaMessage> paMessageArrayList);
    }
}
