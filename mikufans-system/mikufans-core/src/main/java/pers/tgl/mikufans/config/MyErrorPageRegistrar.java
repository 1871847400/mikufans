package pers.tgl.mikufans.config;

import org.springframework.boot.web.server.ErrorPage;
import org.springframework.boot.web.server.ErrorPageRegistrar;
import org.springframework.boot.web.server.ErrorPageRegistry;
import org.springframework.http.HttpStatus;

/**
 * 自定义错误返回的页面,默认是页面Whitelabel Error Page
 *  会阻止BasicErrorController生效
 *  浏览器和其它工具返回的都是html
 */
//@Component
public class MyErrorPageRegistrar implements ErrorPageRegistrar {
    @Override
    public void registerErrorPages(ErrorPageRegistry registry) {
        ErrorPage notFoundPage = new ErrorPage(HttpStatus.NOT_FOUND, "/404.html");
        ErrorPage serverErrorPage = new ErrorPage(HttpStatus.INTERNAL_SERVER_ERROR, "/500.html");
        // 貌似400不能被拦截
        ErrorPage illegalErrorPage = new ErrorPage(IllegalArgumentException.class, "/400.html");
        registry.addErrorPages(notFoundPage, serverErrorPage, illegalErrorPage);
    }
}