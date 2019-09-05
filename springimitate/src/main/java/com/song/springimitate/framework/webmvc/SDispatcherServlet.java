package com.song.springimitate.framework.webmvc;

import com.song.springimitate.framework.annotation.SController;
import com.song.springimitate.framework.annotation.SRequestMapping;
import com.song.springimitate.framework.context.SApplicationContext;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author songsong
 * @since 2019/8/1
 **/
@Slf4j
public class SDispatcherServlet extends HttpServlet {

    private SApplicationContext context = null;

    private String CONTEXT_CONFIG_LOCATION = "contextConfigLocation";
    private Map<SHandlerMapping, SHandlerAdapter> handlerAdapters = new HashMap<>();

    private List<SHandlerMapping> handlerMappings = new ArrayList<>();

    private List<SViewResolver> viewResolvers = new ArrayList<>();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            doDispatch(req, resp);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void doDispatch(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        //从request中拿到URL，去匹配一个handlerMapping
        SHandlerMapping handler = getHandler(req);
        if (handler == null) {
            processDispatchResult(req, resp, new SModelAndView("404"));
            return;
        }
        //准备调用前的参数
        SHandlerAdapter ha = getHandlerAdapter(handler);

        // 真正的调用方法，返回ModeAndVie存储了要传到页面上的值，和页面的模板名称
        SModelAndView mv = ha.handle(req, resp, handler);

        //处理结果并输出到页面
        processDispatchResult(req, resp, mv);
    }

    private SHandlerAdapter getHandlerAdapter(SHandlerMapping handler) {
        if (handlerAdapters.isEmpty()) {
            return null;
        }
        SHandlerAdapter ha = handlerAdapters.get(handler);
        if (ha.support(handler)) {
            return ha;
        }
        return null;
    }

    /**
     * 处理结果并输出到页面
     *
     * @param req
     * @param resp
     * @param mv
     */
    private void processDispatchResult(HttpServletRequest req, HttpServletResponse resp, SModelAndView mv) throws Exception {
        if (mv == null) {
            return;
        }

        if (viewResolvers.isEmpty()) {
            return;
        }

        for (SViewResolver viewResolver : viewResolvers) {
            SView view = viewResolver.resolveViewName(mv.getViewName(), null);
            view.render(mv.getModel(), req, resp);
            return;
        }
    }

    private SHandlerMapping getHandler(HttpServletRequest req) {
        if (handlerMappings.isEmpty()) {
            return null;
        }

        //获取请求的URL
        String url = req.getRequestURI();
        String contextPath = req.getContextPath();
        url = url.replace(contextPath, "").replaceAll("/+", "/");

        for (SHandlerMapping handlerMapping : handlerMappings) {
            Matcher matcher = handlerMapping.getPattern().matcher(url);
            if (!matcher.matches()) {
                continue;
            }
            return handlerMapping;
        }
        return null;
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        //初始化ApplicationContext
        context = new SApplicationContext(config.getInitParameter(CONTEXT_CONFIG_LOCATION));

        //初始化spring mvc九大组件
        initStrategies(context);
    }

    private void initStrategies(SApplicationContext context) {
        //文件上传组件
        initMultipartResolver(context);
        //初始化本地语言环境
        InitLocalResolver(context);
        //初始化模板处理器
        initThemeResolver(context);
        //handlerMapping
        initHandlerMappings(context);
        //初始化适配器
        initHandlerAdapter(context);
        //初始化拦截器
        initHandlerExceptionResolver(context);
        //初始化视图预处理器
        initRequestToViewNameTranslator(context);

        //初始化试图转化器，必须实现
        initViewResolver(context);
        //参数缓存器
        initFlashMapManager(context);

    }

    private void initHandlerAdapter(SApplicationContext context) {
        //一个request变成一个handler，参数都是字符串，自动匹配到handler中的形参
        //拿到HandlerMapping才能干活
        //有几个handlerMapping就有几个HandlerAdapter
        for (SHandlerMapping handlerMapping : handlerMappings) {
            handlerAdapters.put(handlerMapping, new SHandlerAdapter());
        }


    }

    private void initFlashMapManager(SApplicationContext context) {

    }

    private void initViewResolver(SApplicationContext context) {
        //拿到存放模板的目录
        String templateRoot = context.getConfig().getProperty("templateRoot");
        String templateRootPath = this.getClass().getClassLoader().getResource(templateRoot).getFile();
        File templateRootDir = new File(templateRootPath);
        String[] templates = templateRootDir.list();
        for (int i = 0; i < templates.length; i++) {
            //这里主要是为了兼容多模板，所有模仿Spring用List保存
            //在我写的代码中简化了，其实只有需要一个模板就可以搞定
            //只是为了仿真，所有还是搞了个List
            this.viewResolvers.add(new SViewResolver(templateRoot));
        }

    }

    private void initRequestToViewNameTranslator(SApplicationContext context) {

    }

    private void initHandlerExceptionResolver(SApplicationContext context) {

    }


    private void initHandlerMappings(SApplicationContext context) {
        String[] beanNames = context.getBeanDefinitionNames();
        for (String beanName : beanNames) {
            Object controller = context.getBean(beanName);
            Class<?> clazz = controller.getClass();
            if (!clazz.isAnnotationPresent(SController.class)) {
                continue;
            }

            String baseUrl = "";
            if (clazz.isAnnotationPresent(SRequestMapping.class)) {
                SRequestMapping requestMapping = clazz.getAnnotation(SRequestMapping.class);
                baseUrl = requestMapping.value();
            }

            //获取该类下面所有的方法
            Method[] methods = clazz.getMethods();
            for (Method method : methods) {
                if (!method.isAnnotationPresent(SRequestMapping.class)) {
                    continue;
                }

                SRequestMapping requestMapping = method.getAnnotation(SRequestMapping.class);
                String regex = ("/" + baseUrl + "/" + requestMapping.value().replaceAll("\\*", ".*")).replaceAll("/+", "/");
                Pattern pattern = Pattern.compile(regex);

                handlerMappings.add(new SHandlerMapping(pattern, controller, method));
                log.info("Mapped " + regex + "," + method);
            }
        }
    }

    private void initThemeResolver(SApplicationContext context) {

    }

    private void InitLocalResolver(SApplicationContext context) {

    }

    private void initMultipartResolver(SApplicationContext context) {
    }
}
