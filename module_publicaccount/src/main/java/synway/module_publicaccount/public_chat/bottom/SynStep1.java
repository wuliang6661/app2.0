package synway.module_publicaccount.public_chat.bottom;

import android.os.Handler;
import android.util.Log;

public class SynStep1{
	private OnSynStepListen onSynStepListen = null;
	public void start(){
		 new Thread(runnable).start();
	 }

	private Runnable runnable = new Runnable(){
		 @Override
		 public void run(){
			 try{
				 Thread.sleep(2000);
			 }
			 catch(InterruptedException e){
			 }

			 Handler handler = new Handler();
			 handler.post(new Runnable(){
				 @Override
				 public void run(){
					 if(onSynStepListen!=null){
						 Log.e("zjw", "thread");
						 onSynStepListen.onResult();
					 }
				 }
			 });
		 }
	 };

	public void setonSynStepListen(OnSynStepListen onSynStepListen){
		 this.onSynStepListen =onSynStepListen;
	 }
	public interface OnSynStepListen {
		void onResult();

		void onFail();
	}
}
