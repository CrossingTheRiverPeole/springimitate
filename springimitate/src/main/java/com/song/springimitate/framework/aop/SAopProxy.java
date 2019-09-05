package com.song.springimitate.framework.aop;

/**
 * TODO
 *
 * @author songsong
 * @since 2019/8/3
 **/
public interface SAopProxy {

    Object getProxy();

    Object getProxy(ClassLoader classLoader);
}
