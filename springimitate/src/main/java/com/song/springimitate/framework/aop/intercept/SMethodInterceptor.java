package com.song.springimitate.framework.aop.intercept;

import org.aopalliance.intercept.MethodInvocation;

/**
 * TODO
 *
 * @author songsong
 * @since 2019/8/3
 **/
public interface SMethodInterceptor {
    Object invoke(SMethodInvocation invocation) throws Throwable;
}
