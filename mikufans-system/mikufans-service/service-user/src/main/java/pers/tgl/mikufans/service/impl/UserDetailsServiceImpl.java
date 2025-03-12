package pers.tgl.mikufans.service.impl;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import pers.tgl.mikufans.domain.user.User;
import pers.tgl.mikufans.service.UserService;

import javax.annotation.Resource;

/**
 * spring security在验证用户账号和密码时会调用loadUserByUsername
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Resource
    private UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userService.getOneBy(User::getUsername, username);
        if (user == null) {
            throw new UsernameNotFoundException("");
        }
        return user;
    }
}