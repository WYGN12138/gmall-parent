package com.atguigu.gmall.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {
    /**
     * 跳转登录页
     *originUrl=http://www.gmall.com/
     * originUrl=http://list.gmall.com/list.html?&category3Id=61&order=1:desc
     * @return
     */
    @GetMapping("/login.html")
    public String loginPage(@RequestParam("originUrl") String originUrl,
                            Model model) {
        model.addAttribute("originUrl",originUrl);
        return "login";

    }

}
