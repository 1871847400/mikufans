package pers.tgl.mikufans.filter.parser;

import pers.tgl.mikufans.model.UserToken;

import javax.servlet.http.HttpServletRequest;

/**
 * 指定如何编码
 * Authorization: <auth-scheme> <authorization-parameters>
 * 例如Basic：
 *  Authorization: Basic <credentials>
 */
public interface TokenParser {
    String getAuthScheme();

    UserToken parse(HttpServletRequest request, String credentials);
}