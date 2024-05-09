package org.zerock.apiServer.util;

public class CustomJWTException extends RuntimeException{

    public CustomJWTException(String msg){
        super(msg);
    }
}
