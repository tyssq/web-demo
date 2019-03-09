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
	
	@RequestMapping("/monitor")
	public Object monitor() {
		String reqUrl = "http://t.itapgo.com:32222/actuator/bus-refresh";
		String s;
		try {
			s = HttpUtil.httpRequest(reqUrl, "", "POST");
			System.out.println(s);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
	
	public static void main(String[] args) {
		new IndexController().monitor();
	}
}
