package com.cjg.post.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PostViewController {

    @GetMapping(value = "/post/list")
    public String list(){
        return "post/list";
    }
}
