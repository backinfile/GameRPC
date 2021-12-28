package com.backinfile.gameRPC.rpc;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标记这个函数是rpc函数
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.METHOD)
public @interface RPCMethod {
    /**
     * 此函数是代理函数
     */
    boolean proxy() default false;

    String returns() default "";
}
