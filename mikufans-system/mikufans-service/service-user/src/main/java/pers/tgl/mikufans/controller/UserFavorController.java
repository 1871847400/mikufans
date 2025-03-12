package pers.tgl.mikufans.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user/favor")
@Validated
@RequiredArgsConstructor
public class UserFavorController extends BaseController {

}