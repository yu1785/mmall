package com.mmall.common;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.io.Serializable;

//保证序列化json的时候，如果是null的对象，key也会消失
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class ServerRespose<T> implements Serializable {

    private int status;
    private String msg;
    private T data;

    //为什么是私有构造器  //TODO
    private ServerRespose(int status) {
        this.status = status;
    }

    private ServerRespose(int status, String msg) {
        this.status = status;
        this.msg = msg;
    }

    private ServerRespose(int status, T data) {
        this.status = status;
        this.data = data;
    }

    private ServerRespose(int status, String msg, T data) {
        this.status = status;
        this.msg = msg;
        this.data = data;
    }

    // 是否成功
    @JsonIgnore  // 不会显示在json序列化结果里面
    public boolean isSuccess(){
        return this.status == ResponseCode.SUCCESS.getCode();
    }

    // get方法
    public int getStatus() {
        return status;
    }
    public String getMsg() {
        return msg;
    }
    public T getData() {
        return data;
    }

    // 返回成功信息
    public static <T> ServerRespose<T> createBySuccess(){
        return new ServerRespose<T>(ResponseCode.SUCCESS.getCode());
    }
    public static <T> ServerRespose<T> createBySuccessMessage(String msg){
        return new ServerRespose<T>(ResponseCode.SUCCESS.getCode(),msg);
    }
    public static <T> ServerRespose<T> createBySuccess(T data){
        return new ServerRespose<T>(ResponseCode.SUCCESS.getCode(),data);
    }
    public static <T> ServerRespose<T> createBySuccess(String msg,T data){
        return new ServerRespose<T>(ResponseCode.SUCCESS.getCode(),msg,data);
    }

    // 返回失败信息
    public static <T> ServerRespose<T> createByError(){
        return new ServerRespose<T>(ResponseCode.ERROR.getCode(),ResponseCode.ERROR.getDesc());
    }
    public static <T> ServerRespose<T> createByErrorMessage(String errorMessage){
        return new ServerRespose<T>(ResponseCode.ERROR.getCode(),errorMessage);
    }
    public static <T> ServerRespose<T> createByErrorCodeMessage(int errorCode,String errorMessage){
        return new ServerRespose<T>(errorCode,errorMessage);
    }

}
