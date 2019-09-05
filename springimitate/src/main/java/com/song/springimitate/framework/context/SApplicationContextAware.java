package com.song.springimitate.framework.context;

/**
 * 通过解耦的方式获得IOC容器的顶层设计
 * 后面将通过一个监听器扫描所有的类，主要实现了此接口，将自动
 * 调用setApplicationContext()方法，从而将容器注入到目标类中
 *
 * @author songsong
 * @since 2019/7/23
 **/
public interface SApplicationContextAware {
    void setApplicationContext(SApplicationContext applicationContext);

}
