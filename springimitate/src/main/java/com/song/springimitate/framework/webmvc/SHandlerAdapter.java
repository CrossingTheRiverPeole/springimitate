package com.song.springimitate.framework.webmvc;

import com.song.springimitate.framework.annotation.SRequestParam;
import com.sun.deploy.net.HttpRequest;
import com.sun.deploy.net.HttpResponse;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * TODO
 *
 * @author songsong
 * @since 2019/8/1
 **/
public class SHandlerAdapter {

    public boolean support(Object handler) {
        return (handler instanceof SHandlerMapping);
    }

    public SModelAndView handle(HttpServletRequest req, HttpServletResponse resp, Object handler) throws InvocationTargetException, IllegalAccessException {
        SHandlerMapping handlerMapping = (SHandlerMapping) handler;

        //方法的形参列表和request的参数列表顺序一一对应
        Map<String, Integer> paramIndexMapping = new HashMap<>();

        //提取方法中加了注解的参数
        //把方法上的注解拿到，得到一个二维数组
        //一个参数有多个注解，一个方法有多个参数
        Annotation[][] pa = handlerMapping.getMethod().getParameterAnnotations();
        for (int i = 0; i < pa.length; i++) {
            Annotation[] annotations = pa[i];
            for (Annotation a : annotations) {
                if (a instanceof SRequestParam) {
                    String paramName = ((SRequestParam) a).value();
                    if (!"".equals(paramName.trim())) {
                        paramIndexMapping.put(paramName, i);
                    }
                }
            }
        }

        //提取方法中的request和resp
        Class<?>[] parameterTypes = handlerMapping.getMethod().getParameterTypes();
        for (int i = 0; i < parameterTypes.length; i++) {
            Class<?> parameterType = parameterTypes[i];
            if (parameterType == HttpRequest.class || parameterType == HttpResponse.class) {
                paramIndexMapping.put(parameterType.getName(), i);
            }
        }


        //获取方法实参列表
        Object[] paramValues = new Object[parameterTypes.length];

        Map<String, String[]> params = req.getParameterMap();

        for (Map.Entry<String, String[]> parm : params.entrySet()) {
            String value = Arrays.toString(parm.getValue()).replaceAll("\\[|\\]", "")
                    .replaceAll("\\s", ",");

            if (!paramIndexMapping.containsKey(parm.getKey())) {
                continue;
            }

            int index = paramIndexMapping.get(parm.getKey());
            paramValues[index] = caseStringValue(value, parameterTypes[index]);
        }

        if (paramIndexMapping.containsKey(HttpServletRequest.class.getName())) {
            int reqIndex = paramIndexMapping.get(HttpServletRequest.class.getName());
            paramValues[reqIndex] = req;
        }

        if (paramIndexMapping.containsKey(HttpServletResponse.class.getName())) {
            int respIndex = paramIndexMapping.get(HttpServletResponse.class.getName());
            paramValues[respIndex] = resp;
        }

        //实际调用方法进行处理
        Object result = handlerMapping.getMethod().invoke(handlerMapping.getController(), paramValues);
        if (result == null || result instanceof Void) {
            return null;
        }

        //
        boolean isModelAndView = handlerMapping.getMethod().getReturnType() == SModelAndView.class;
        if (isModelAndView) {
            return (SModelAndView) result;
        }
        return null;
    }

    private Object caseStringValue(String value, Class<?> paramsType) {
        if (String.class == paramsType) {
            return value;
        }
        //如果是int
        if (Integer.class == paramsType) {
            return Integer.valueOf(value);
        } else if (Double.class == paramsType) {
            return Double.valueOf(value);
        } else {
            if (value != null) {
                return value;
            }
            return null;
        }
    }
}
