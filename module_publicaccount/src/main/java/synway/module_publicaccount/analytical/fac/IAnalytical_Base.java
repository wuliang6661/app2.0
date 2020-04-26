package synway.module_publicaccount.analytical.fac;

import android.content.Context;

import org.json.JSONObject;

import synway.module_publicaccount.analytical.obj.Obj_PublicMsgBase;

public interface IAnalytical_Base {
	
	int msgType();

	void onInit(Context context);

	Obj_PublicMsgBase onDeal(JSONObject jsonObject);

}