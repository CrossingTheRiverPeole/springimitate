package com.song.springimitate.framework.beans.support;

import com.song.springimitate.framework.beans.config.SBeanDefinition;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * TODO
 *
 * @author songsong
 * @since 2019/7/25
 **/
public class SBeanDefinitionReader {

    private Properties config = new Properties();

    private List<String> registryBeanClass = new ArrayList<>();

    //固定配置文件中的key，相当于xml中的规范
    private final String SCAN_PACKAGE = "scanPackage";

    public SBeanDefinitionReader(String... locations) {
        InputStream is = this.getClass().getClassLoader().
                getResourceAsStream(locations[0].replaceAll("classpath:", ""));
        try {
            config.load(is);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != is) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        doScanner(config.getProperty(SCAN_PACKAGE));
    }

    private void doScanner(String scanPackage) {
        //转换未见路径，就是把.替换为/
        URL url = this.getClass().getClassLoader().getResource("/" + scanPackage.replaceAll("\\.", "/"));
        File classPath = new File(url.getFile());
        for (File file : classPath.listFiles()) {
            if (file.isDirectory()) {
                doScanner(scanPackage + "." + file.getName());
            } else {
                if (file.getName().endsWith(".class")) {
                    continue;
                }
                String className = (scanPackage + file.getName().replace(".class", ""));
                registryBeanClass.add(className);
            }
        }
    }

    /**
     * 构建beandefinition并返回
     *
     * @return
     */
    public List<SBeanDefinition> loadBeanDefinitions() {
        List<SBeanDefinition> result = new ArrayList<>();
        for (String beanClass : registryBeanClass) {
            try {
                Class<?> clazz = Class.forName(beanClass);
                //判断是否是一个接口
                if (clazz.isInterface()) {
                    continue;
                }

                SBeanDefinition beanDefinition = doCreateBeanDefinition(toLowerFirstCase(clazz.getSimpleName()), clazz.getName());
                result.add(beanDefinition);
                result.add(doCreateBeanDefinition(clazz.getName(), clazz.getName()));
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 构建bean的SBeanDefinition
     *
     * @param factoryBeanName
     * @param className
     * @return
     */
    private SBeanDefinition doCreateBeanDefinition(String factoryBeanName, String className) {
        SBeanDefinition beanDefinition = new SBeanDefinition();
        beanDefinition.setFactoryBeanName(factoryBeanName);
        beanDefinition.setBeanClassName(className);
        return beanDefinition;
    }

    /**
     * 类名首字母小写
     *
     * @param simpleName
     * @return
     */
    private String toLowerFirstCase(String simpleName) {
        char[] chars = simpleName.toCharArray();
        chars[0] += 32;
        return String.valueOf(chars);

    }

    public Properties getConfig() {
        return config;
    }
}
