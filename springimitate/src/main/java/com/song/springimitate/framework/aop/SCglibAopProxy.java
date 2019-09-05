package com.song.springimitate.framework.aop;

import com.song.springimitate.framework.aop.support.SAdvisedSupport;

/**
 * TODO
 *
 * @author songsong
 * @since 2019/8/3
 **/
public class SCglibAopProxy implements SAopProxy {

    private SAdvisedSupport advised;

    public SCglibAopProxy(SAdvisedSupport config) {
        this.advised = config;
    }

    @Override
    public Object getProxy() {
        return null;
    }

    @Override
    public Object getProxy(ClassLoader classLoader) {
        return null;
    }
}
