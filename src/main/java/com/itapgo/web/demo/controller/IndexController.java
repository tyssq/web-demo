package com.itapgo.web.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 
 * @author LiaoZhengHan
 * @date 2019年2月12日
 */
@Controller
public class IndexController {

	@RequestMapping("/")
	public String index() {
		return "/index";
	}
}
