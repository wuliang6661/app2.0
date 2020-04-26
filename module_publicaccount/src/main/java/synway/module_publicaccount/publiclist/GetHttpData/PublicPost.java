package synway.module_publicaccount.publiclist.GetHttpData;

import org.json.JSONObject;

/**
 * Created by zjw on 2019/1/10.
 */

public class PublicPost {
    public static String[] checkResult(JSONObject jsonObject) {
        String result = jsonObject.optString("status");
        // 检查结果
        if (result.equals("success")) {
            return null;
        } else  {
            int result2 = jsonObject.optInt("RESULT", -1);
            if (result2 == -2) {
                return new String[]{"返回数据格式有误", jsonObject.optString("REASON", "JSON has no KEY=REASON")};
            } else if (result2 == -1) {
                return new String[]{"网络请求失败", jsonObject.optString("REASON", "JSON has no KEY=REASON")};
            } else if (result2 == 0) {
                return new String[]{"操作失败", jsonObject.optString("REASON", "JSON has no KEY=REASON")};
            }
            return new String[]{"请求失败", jsonObject.optString("result", "JSON has no KEY=reslut")};
        }
    }
}
