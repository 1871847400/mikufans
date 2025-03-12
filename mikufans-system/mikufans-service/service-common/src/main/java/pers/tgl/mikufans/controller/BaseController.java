package pers.tgl.mikufans.controller;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.NumberUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import pers.tgl.mikufans.util.PageImpl;
import pers.tgl.mikufans.util.SecurityUtils;
import pers.tgl.mikufans.util.ServletUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;

public class BaseController {
    /**
     * 自定义转换规则,只在当前controller生效
     */
    @InitBinder
    public void initBinder(WebDataBinder binder) {
    }

    /**
     * 获取当前登录用户的id,如果未登录抛出异常
     */
    @NotNull
    protected Long getContextUserId() {
        return SecurityUtils.getContextUserId(true);
    }

    /**
     * 根据当前前端的url请求参数生成page对象
     */
    public static <T> PageImpl<T> createPage() {
        String pageNum = ServletUtils.getParameter("pageNum", "1");
        String pageSize = ServletUtils.getParameter("pageSize", "10");
        String searchCount = ServletUtils.getParameter("searchCount", "true");
        int num = NumberUtil.parseInt(pageNum, 1);
        int size = NumberUtil.parseInt(pageSize, 10);
        return new PageImpl<>(num, size, Boolean.parseBoolean(searchCount));
    }

    protected <T> IPage<T> selectPage(T entity, OrderItem ...orders) {
        PageImpl<T> page = createPage();
        if (ArrayUtil.isNotEmpty(orders)) {
            page.setOrders(ListUtil.of(orders));
        }
        return Db.page(page, Wrappers.query(entity));
    }

    protected HttpServletRequest getRequest() {
        return ServletUtils.getRequest();
    }

    protected HttpServletResponse getResponse() {
        return ServletUtils.getResponse();
    }
}