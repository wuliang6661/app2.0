package synway.module_stack.hm_leave.net;

/**
 * Created by wuliang on 2017/3/27.
 * 所有返回的json数据的公有格式
 */

public class BaseResult<T> {

//    1）	RESULT: 表成功和失败状态。1表成功，0表失败。
//            2）	REASON: 错误信息，当有错误发生时，此errorMessage包含有错误信息
//    3）	errorCode: 错误编码，当有错误发生时，此errorCode包含有错误编码
//    4）	data：返回数据

    private static int SURCESS = 200;

    private int code;

    private String err;

    private int total;

    private T data;


    public boolean suress() {
        return code == SURCESS;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getErr() {
        return err;
    }

    public void setErr(String err) {
        this.err = err;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
