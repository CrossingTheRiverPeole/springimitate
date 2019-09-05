package com.song.springimitate.framework.context;

import com.song.springimitate.framework.annotation.SAutowired;
import com.song.springimitate.framework.annotation.SController;
import com.song.springimitate.framework.annotation.SService;
import com.song.springimitate.framework.aop.SAopProxy;
import com.song.springimitate.framework.aop.SCglibAopProxy;
import com.song.springimitate.framework.aop.SJdkDynamicAopProxy;
import com.song.springimitate.framework.aop.config.SAopConfig;
import com.song.springimitate.framework.aop.support.SAdvisedSupport;
import com.song.springimitate.framework.beans.SBeanWrapper;
import com.song.springimitate.framework.beans.config.SBeanDefinition;
import com.song.springimitate.framework.beans.config.SBeanPostProcessor;
import com.song.springimitate.framework.beans.support.SBeanDefinitionReader;
import com.song.springimitate.framework.beans.support.SDefaultListableBeanFactory;
import com.song.springimitate.framework.core.SBeanFactory;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author songsong
 * @since 2019/7/23
 **/
public class SApplicationContext extends SDefaultListableBeanFactory implements SBeanFactory {

    private String[] configLocations;

    private Map<String, Object> singletonObjects = new HashMap<>();

    private SBeanDefinitionReader reader;

    //ioc容器
    private Map<String, SBeanWrapper> factoryBeanInstanceCache = new ConcurrentHashMap<>();


    public SApplicationContext(String... configLocations) {
        this.configLocations = configLocations;
        try {
            refresh();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public Object getBean(Class<?> clazz) throws Exception {
        return null;
    }

    @Override
    public void refresh() throws Exception {
        //1.定位：定位配置文件的位置
        reader = new SBeanDefinitionReader(configLocations);
        //2.构建beandefinition
        List<SBeanDefinition> beanDefinitions = reader.loadBeanDefinitions();
        //3,注册，把配置信息注册到容器里面（IOC容器）
        doRegisterBeanDefinition(beanDefinitions);

        //4.把不是延迟加载的类，提前初始化
        doAutowirted();
    }

    //只处理非延时加载的请款
    private void doAutowirted() {
        for (Map.Entry<String, SBeanDefinition> beanDefinitionEntry : super.beanDefinitionMap.entrySet()) {
            String beanName = beanDefinitionEntry.getKey();
            if (!beanDefinitionEntry.getValue().isLazyInit()) {
                getBean(beanName);
            }
        }
    }

    @Override
    public Object getBean(String beanName) {
        SBeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
        //依赖注入，读取BeanDefinition的信息，然后通过发射的机制创建一个实例并返回
        //spring做法是不会把原始的对象翻出去，会用一个BeanWrapper进行包装
        //装饰器模式
        //保留原先的OOP关系
        //对它进行扩展，增强
        Object instance = null;

        SBeanPostProcessor postProcessor = new SBeanPostProcessor();
        postProcessor.postProcessorBeforeInitialization(instance, beanName);

        //实例化bean
        instance = instantiateBean(beanName, beanDefinition);

        //得到实例之后，把bean封装到beanwrapper中
        SBeanWrapper beanWrapper = new SBeanWrapper(instance);
        //把beanWrapper存放到容器中
        factoryBeanInstanceCache.put(beanName, beanWrapper);
        //
        postProcessor.postProcessorAfterInitilization(instance, beanName);
        //3.注入
        populateBean(beanName, new SBeanDefinition(), beanWrapper);
        return factoryBeanInstanceCache.get(beanName).getWrapperInstance();
    }

    /**
     * 注入bean实例
     *
     * @param beanName
     * @param sBeanDefinition
     * @param beanWrapper
     */
    private void populateBean(String beanName, SBeanDefinition sBeanDefinition, SBeanWrapper beanWrapper) {
        Object instance = beanWrapper.getWrapperInstance();
        Class<?> clazz = beanWrapper.getWrappedClass();
        //只有加了注解的类才能执行依赖注入
        if ((clazz.isAnnotationPresent(SController.class) || clazz.isAnnotationPresent(SService.class))) {
            return;
        }

        Field[] declaredFields = clazz.getDeclaredFields();
        for (Field field : declaredFields) {
            if (!field.isAnnotationPresent(SAutowired.class)) {
                continue;
            }
            //强制访问
            field.setAccessible(true);

            SAutowired autowired = field.getAnnotation(SAutowired.class);
            String autowiredBeanName = autowired.value().trim();
            if (autowiredBeanName.isEmpty()) {
                autowiredBeanName = field.getType().getName();
            }
            if (factoryBeanInstanceCache.get(autowiredBeanName) == null) {
                continue;
            }
            try {
                field.set(instance, factoryBeanInstanceCache.get(autowiredBeanName).getWrapperInstance());
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * @param beanName
     * @param beanDefinition
     */
    private Object instantiateBean(String beanName, SBeanDefinition beanDefinition) {
        //拿到实例化的对象的类名
        String className = beanDefinition.getBeanClassName();

        Object instance = null;

        //2.反射进行实例化
        try {
            if (singletonObjects.containsKey(className)) {
                instance = singletonObjects.get(className);
            } else {
                Class<?> clazz = Class.forName(className);
                instance = clazz.newInstance();

                //初始化
                SAdvisedSupport config = instantionAopConfig(beanDefinition);
                config.setTarget(instance);
                config.setTargetClass(clazz);

                //如果符合PointCut的规则，创建代理对象
                if (config.pointCutMatch()){
                    instance = createProxy(config).getProxy();
                }
                singletonObjects.put(className, instance);
                singletonObjects.put(beanDefinition.getFactoryBeanName(), instance);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return instance;
    }

    //创建代理对象
    private SAopProxy createProxy(SAdvisedSupport config) {
        Class<?> targetClass = config.getTargetClass();
        if (targetClass.getInterfaces().length > 0){
            return new SJdkDynamicAopProxy(config);
        }
        return new SCglibAopProxy(config);
    }

    private SAdvisedSupport instantionAopConfig(SBeanDefinition beanDefinition) {
        SAopConfig  config = new SAopConfig();
        config.setPointCut(this.reader.getConfig().getProperty("pointCut"));
        config.setAspectClass(this.reader.getConfig().getProperty("aspectClass"));
        config.setAspectBefore(this.reader.getConfig().getProperty("aspectBefore"));
        config.setAspectAfter(this.reader.getConfig().getProperty("aspectAfter"));
        config.setAspectAfterThrow(this.reader.getConfig().getProperty("aspectAfterThrow"));
        config.setAspectAfterThrowingName(this.reader.getConfig().getProperty("aspectAfterThrowingName"));
        return new SAdvisedSupport(config);

    }

    /**
     * 把beandefition注册到
     *
     * @param beanDefinitions
     */
    private void doRegisterBeanDefinition(List<SBeanDefinition> beanDefinitions) throws Exception {
        for (SBeanDefinition beanDefinition : beanDefinitions) {
            if (super.beanDefinitionMap.containsKey(beanDefinition.getFactoryBeanName())) {
                throw new Exception("the " + beanDefinition.getFactoryBeanName() + "is exist");
            }
            super.beanDefinitionMap.put(beanDefinition.getFactoryBeanName(), beanDefinition);
        }
    }

    public String[] getBeanDefinitionNames() {
        return this.beanDefinitionMap.keySet().toArray(new String[beanDefinitionMap.size()]);
    }

    public Properties getConfig() {
        return reader.getConfig();
    }
}
