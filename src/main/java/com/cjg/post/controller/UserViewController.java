package com.cjg.post.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserViewController {

    @GetMapping(value = "/user/login")
    public String login(){
        return "user/login";
    }

    @GetMapping(value = "/user/signup")
    public String signup(){
        return "user/signup";
    }

    @GetMapping(value = "/user/modify")
    public String modify(){
        return "user/modify";
    }
}
