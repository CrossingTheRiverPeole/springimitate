package com.song.springimitate.demo.action;

import com.song.springimitate.demo.service.IModifyService;
import com.song.springimitate.demo.service.IQueryService;
import com.song.springimitate.framework.annotation.SAutowired;
import com.song.springimitate.framework.annotation.SController;
import com.song.springimitate.framework.annotation.SRequestMapping;
import com.song.springimitate.framework.annotation.SRequestParam;
import com.song.springimitate.framework.webmvc.SModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * 公布接口url
 *
 * @author Tom
 */
@SController
@SRequestMapping("/web")
public class MyAction {

    @SAutowired
    IQueryService queryService;
    @SAutowired
    IModifyService modifyService;

    @SRequestMapping("/query.json")
    public SModelAndView query(HttpServletRequest request, HttpServletResponse response,
                               @SRequestParam("name") String name) {
        String result = queryService.query(name);
        return out(response, result);
    }

    @SRequestMapping("/add*.json")
    public SModelAndView add(HttpServletRequest request, HttpServletResponse response,
                             @SRequestParam("name") String name, @SRequestParam("addr") String addr) {
        String result = null;
        try {
            result = modifyService.add(name, addr);
            return out(response, result);
        } catch (Exception e) {
//			e.printStackTrace();
            Map<String, Object> model = new HashMap<String, Object>();
            model.put("detail", e.getCause().getMessage());
//			System.out.println(Arrays.toString(e.getStackTrace()).replaceAll("\\[|\\]",""));
            model.put("stackTrace", Arrays.toString(e.getStackTrace()).replaceAll("\\[|\\]", ""));
            return new SModelAndView("500", model);
        }

    }

    @SRequestMapping("/remove.json")
    public SModelAndView remove(HttpServletRequest request, HttpServletResponse response,
                                @SRequestParam("id") Integer id) {
        String result = modifyService.remove(id);
        return out(response, result);
    }

    @SRequestMapping("/edit.json")
    public SModelAndView edit(HttpServletRequest request, HttpServletResponse response,
                              @SRequestParam("id") Integer id,
                              @SRequestParam("name") String name) {
        String result = modifyService.edit(id, name);
        return out(response, result);
    }


    private SModelAndView out(HttpServletResponse resp, String str) {
        try {
            resp.getWriter().write(str);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
