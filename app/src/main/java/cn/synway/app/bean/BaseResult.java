package cn.synway.app.bean;

/**
 * Created by wuliang on 2017/3/27.
 * 所有返回的json数据的公有格式
 */

public class BaseResult<T> {

//    1）	status: 表成功和失败状态。1表成功，0表失败。
//            2）	errorMessage: 错误信息，当有错误发生时，此errorMessage包含有错误信息
//    3）	errorCode: 错误编码，当有错误发生时，此errorCode包含有错误编码
//    4）	data：返回数据

    private static int SURCESS = 1;

    private String msg;
    private int code;
    private T data;

    public boolean surcess() {
        return code == SURCESS;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
