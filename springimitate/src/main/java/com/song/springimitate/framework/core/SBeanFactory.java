package com.song.springimitate.framework.core;

/**
 * TODO
 *
 * @author songsong
 * @since 2019/7/25
 **/
public interface SBeanFactory {

    /**
     * 根据beanName从IOC容器中获得一个实例bean
     *
     * @param beanName
     * @return
     */
    Object getBean(String beanName) throws Exception;

    Object getBean(Class<?> clazz) throws Exception;

}
