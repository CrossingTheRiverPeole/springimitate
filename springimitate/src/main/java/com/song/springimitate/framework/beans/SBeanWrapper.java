package com.song.springimitate.framework.beans;

/**
 * TODO
 *
 * @author songsong
 * @since 2019/7/29
 **/
public class SBeanWrapper {

    private Object wrapperInstance;
    private Class<?> wrappedClass;

    public SBeanWrapper(Object wrapperInstance) {
        this.wrapperInstance = wrapperInstance;
    }

    public Object getWrapperInstance() {
        return wrapperInstance;
    }

    //代理之后的类
    public Class<?> getWrappedClass() {
        return wrappedClass.getClass();
    }
}
