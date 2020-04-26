package synway.common.okhttp.callback;

import org.json.JSONObject;

import okhttp3.Response;

/**
 * <p>
 * [LIO]Life:Input:Output:
 *
 * @author 孙量 2016/11/22 10:22
 */
public abstract class JSONObjectCallback extends Callback<JSONObject> {
    @Override
    public JSONObject parseNetworkResponse(Response response, int id) throws Exception {
        return new JSONObject(response.body().string());
    }
}
