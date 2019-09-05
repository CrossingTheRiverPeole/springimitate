package com.song.springimitate.framework.aop.aspect;

import com.song.springimitate.framework.aop.intercept.SMethodInterceptor;
import com.song.springimitate.framework.aop.intercept.SMethodInvocation;

import java.lang.reflect.Method;

/**
 * TODO
 *
 * @author songsong
 * @since 2019/8/3
 **/
public class SAfterThrowingAdviceInterceptor extends SAbstractAspectAdvice implements SAdvice, SMethodInterceptor {

    private String throwingName;

    public SAfterThrowingAdviceInterceptor(Method aspectMethod, Object aspectTarget) {
        super(aspectMethod, aspectTarget);
    }

    @Override
    public Object invoke(SMethodInvocation mi) throws Throwable {
        try {
            return mi.proceed();
        } catch (Throwable e) {
            invokeAdviceMethod(mi, null, e.getCause());
            throw e;
        }
    }

    public void setThrowName(String throwName) {
        this.throwingName = throwName;
    }
}
