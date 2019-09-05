package com.song.springimitate.framework.aop;

import com.song.springimitate.framework.aop.intercept.SMethodInvocation;
import com.song.springimitate.framework.aop.support.SAdvisedSupport;
import org.aopalliance.intercept.MethodInvocation;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;

/**
 * TODO
 *
 * @author songsong
 * @since 2019/8/3
 **/
public class SJdkDynamicAopProxy implements SAopProxy, InvocationHandler {

    private SAdvisedSupport advised;

    public SJdkDynamicAopProxy(SAdvisedSupport config) {
        this.advised = config;
    }

    @Override
    public Object getProxy() {
        return getProxy(advised.getTargetClass().getClassLoader());
    }

    @Override
    public Object getProxy(ClassLoader classLoader) {
        return Proxy.newProxyInstance(classLoader, advised.getTargetClass().getInterfaces(), this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        List<Object> interceptorsAndDynamicMethodMatchers = advised.getInterceptorsAndDynamicInterceptionAdvice(method,advised.getTargetClass());
        SMethodInvocation invocation = new SMethodInvocation(proxy,this.advised.getTarget(),method,args,this.advised.getTargetClass(),interceptorsAndDynamicMethodMatchers);
        return invocation.proceed();
    }
}
