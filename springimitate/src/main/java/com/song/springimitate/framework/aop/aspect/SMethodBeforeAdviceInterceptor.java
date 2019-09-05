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
public class SMethodBeforeAdviceInterceptor extends SAbstractAspectAdvice implements SAdvice, SMethodInterceptor {

    private SJoinPoint joinPoint;

    public SMethodBeforeAdviceInterceptor(Method aspectMethod, Object aspectTarget) {
        super(aspectMethod, aspectTarget);
    }

    private void before(Method method, Object[] args, Object target) throws Throwable {
        //传送了给织入参数
        //method.invoke(target);
        super.invokeAdviceMethod(this.joinPoint, null, null);

    }

    @Override
    public Object invoke(SMethodInvocation mi) throws Throwable {
        //从被织入的代码中才能拿到，JoinPoint
        this.joinPoint = mi;
        before(mi.getMethod(), mi.getArguments(), mi.getThis());
        return mi.proceed();
    }
}
