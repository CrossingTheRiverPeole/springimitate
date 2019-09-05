package com.song.springimitate.framework.aop.aspect;

import java.lang.reflect.Method;

/**
 * TODO
 *
 * @author songsong
 * @since 2019/8/3
 **/
public interface SJoinPoint {

    Object getThis();

    Object[] getArguments();

    Method getMethod();

    void setUserAttribute(String key, Object value);

    Object getUserAttribute(String key);
}
