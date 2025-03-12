package pers.tgl.mikufans.filter;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import pers.tgl.mikufans.domain.system.SysUser;
import pers.tgl.mikufans.domain.user.User;
import pers.tgl.mikufans.exception.TokenAuthenticationException;
import pers.tgl.mikufans.filter.parser.TokenParser;
import pers.tgl.mikufans.model.UserToken;
import pers.tgl.mikufans.service.SysUserService;
import pers.tgl.mikufans.service.UserService;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

/**
 * Header Authentication最大8KB
 *  注意: doFilter处理过程并不是处于springWeb的管理,无法接管异常处理等操作
 */
@Component
@Slf4j
public class TokenFilter extends OncePerRequestFilter {
    @Resource
    private SysUserService sysUserService;
    @Resource
    private UserService userService;
    @Resource
    private List<TokenParser> tokenParsers;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String accessToken = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (StrUtil.isNotBlank(accessToken) && !isSkip(request)) {
            UsernamePasswordAuthenticationToken authentication = null;
            try {
                String authScheme = StrUtil.sub(accessToken, 0, accessToken.indexOf(" "));
                TokenParser tokenParser = CollUtil.findOne(tokenParsers, a->Objects.equals(authScheme, a.getAuthScheme()));
                if (tokenParser == null) {
                    throw new TokenAuthenticationException("不支持的编码方式: " + authScheme);
                }
                String credentials = accessToken.replace(authScheme, "").trim();
                UserToken userToken = tokenParser.parse(request, credentials);
                List<? extends GrantedAuthority> authorities = null;
                if (userToken.getUserType() == 1) {
                    SysUser sysUser = sysUserService.getById(userToken.getId());
                    if (sysUser == null || sysUser.getDisabled() == 1) {
                        throw new DisabledException("");
                    }
                    if (sysUser.getPassword().hashCode() != userToken.getVersion()) {
                        throw new TokenAuthenticationException("认证信息已失效,请重新登录！");
                    }
                    authorities = sysUserService.getUserAuthorities(sysUser.getId());
                } else {
                    User user = userService.getById(userToken.getId());
                    if (user == null || user.getDisabled() == 1) {
                        throw new DisabledException("");
                    }
                    if (user.getPassword().hashCode() != userToken.getVersion()) {
                        throw new TokenAuthenticationException("认证信息已失效,请重新登录！");
                    }
                    authorities = user.getAuthorities();
                }
                authentication = new UsernamePasswordAuthenticationToken(userToken.getUsername(), null, authorities);
//                UserDetails userDetails = userDetailsService.loadUserByUsername(loginUser.getUsername());
//                authentication = (UsernamePasswordAuthenticationToken) authenticationManagerBuilder.getOrBuild().authenticate(authentication);
                authentication.setDetails(userToken);
            } catch (SignatureException e) {
                throw new TokenAuthenticationException("token签名无效", e);
            } catch (ExpiredJwtException e) {
                throw new TokenAuthenticationException("token已到期", e);
            } catch (BadCredentialsException e) {
                throw new TokenAuthenticationException("用户名或密码错误", e);
            } catch (AuthenticationException e) {
                //可能用户改过密码
                throw new TokenAuthenticationException("token已无效,请重新登录！", e);
            } catch (Exception e) {
                log.error("token filter 异常", e);
                throw new TokenAuthenticationException(e.getMessage(), e);
            }
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        chain.doFilter(request, response);
    }

    /**
     * 部分请求地址，需要跳过检测accessToken，以免报token失效
     * 1.如果请求地址是刷新token,则跳过
     * 2.如果请求地址是静态资源,则跳过
     */
    private boolean isSkip(HttpServletRequest request) {
        String uri = request.getRequestURI();
        return uri.endsWith("/refresh-token") || uri.startsWith("/static");
    }
}