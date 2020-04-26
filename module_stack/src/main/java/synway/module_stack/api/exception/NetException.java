package synway.module_stack.api.exception;

/**
 * Created by wuliang on 2018/11/13.
 *
 * 自定义的异常处理
 */

public class NetException extends RuntimeException{

    public String code;
    public String message;

    public NetException(String code,String message){
        super(message);
        this.code = code;
        this.message = message;
    }










}
