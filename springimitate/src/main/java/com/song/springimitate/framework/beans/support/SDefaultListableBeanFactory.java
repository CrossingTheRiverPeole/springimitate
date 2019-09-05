package com.song.springimitate.framework.beans.support;

import com.song.springimitate.framework.beans.config.SBeanDefinition;
import com.song.springimitate.framework.context.SApplicationContext;
import com.song.springimitate.framework.context.support.SAbstractApplicationContext;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 *
 * @author songsong
 * @since 2019/7/23
 **/
public abstract class SDefaultListableBeanFactory extends SAbstractApplicationContext {
    //注册存储注册信息的BeanDefinition
    protected final Map<String, SBeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>();
}
