package synway.common.okhttp.builder;


import synway.common.okhttp.OkHttpUtils;
import synway.common.okhttp.request.OtherRequest;
import synway.common.okhttp.request.RequestCall;

/**
 * Created by zhy on 16/3/2.
 */
public class HeadBuilder extends GetBuilder
{
    @Override
    public RequestCall build()
    {
        return new OtherRequest(null, null, OkHttpUtils.METHOD.HEAD, url, tag, params, headers,id).build();
    }
}
