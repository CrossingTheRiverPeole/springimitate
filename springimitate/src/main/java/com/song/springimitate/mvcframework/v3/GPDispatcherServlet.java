package com.song.springimitate.mvcframework.v3;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
 * @since 2019/7/18
 **/
public class GPDispatcherServlet extends HttpServlet {

    private static final String LOCATION = "contextConfigLocation";

    private List<String> classNames = new ArrayList<String>();

    Properties p = new Properties();

    public GPDispatcherServlet() {
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        //1、加载配置文件
        doLoadConfig(config.getInitParameter(LOCATION));
        //2、扫描所有的类
        doScanner(p.getProperty("scanPackage"));

        //3、初始化所有有关类的实例，并保存到IOC容器中


        //4、依赖注入

        //5、构造handlerMapping

        //等待请求，匹配URL，定位方法，，发射调用执行
        System.out.println("imitate spring init method is invoked");

    }


    //手动加载配置文件中的内容
    private void doLoadConfig(String location) {
        InputStream fis = this.getClass().getClassLoader().getResourceAsStream(location);
        try {
            //把配置文件读取为流并返显为properties
            p.load(fis);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != fis) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //根据配置文件中的内容扫描所有的类
    private void doScanner(String packageName) {
        URL url = this.getClass().getClassLoader().getResource("/" + packageName.replace("\\.", "/"));
        File dir = new File(url.getFile());
        for (File file : dir.listFiles()) {
            //如果是文件夹继续递归
            if (file.isDirectory()) {
                doScanner(packageName + "." + file.getName());
            } else {
                classNames.add(packageName + "." + file.getName().replaceAll(".class", ""));
            }
        }
    }


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doDispatch();
    }

    private void doDispatch() {

    }


}
