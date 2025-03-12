package pers.tgl.mikufans.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pers.tgl.mikufans.domain.user.UserFavor;
import pers.tgl.mikufans.mapper.UserFavorMapper;
import pers.tgl.mikufans.service.UserFavorService;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserFavorServiceImpl extends BaseServiceImpl<UserFavor, UserFavorMapper> implements UserFavorService {

}