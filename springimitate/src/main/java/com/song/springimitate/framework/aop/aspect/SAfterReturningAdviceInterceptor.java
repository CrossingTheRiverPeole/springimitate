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
public class SAfterReturningAdviceInterceptor extends SAbstractAspectAdvice implements SAdvice, SMethodInterceptor {

    private SJoinPoint joinPoint;

    public SAfterReturningAdviceInterceptor(Method aspectMethod, Object aspectTarget) {
        super(aspectMethod, aspectTarget);
    }

    @Override
    public Object invoke(SMethodInvocation mi) throws Throwable {
        Object retVal = mi.proceed();
        this.joinPoint = mi;
        this.afterReturning(retVal, mi.getMethod(), mi.getArguments(), mi.getThis());
        return retVal;
    }

    private void afterReturning(Object retVal, Method method, Object[] arguments, Object aThis) throws Throwable {
        super.invokeAdviceMethod(this.joinPoint, retVal, null);
    }
}
