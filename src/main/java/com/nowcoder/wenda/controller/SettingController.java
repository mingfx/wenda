package com.nowcoder.wenda.controller;

import com.nowcoder.wenda.service.WendaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
public class SettingController {
    @Autowired
    WendaService wendaService;

    @RequestMapping(path = {"/setting"},method = RequestMethod.GET)
    @ResponseBody//说明返回的是简单的字符串
    public String index(HttpSession httpSession){
        return "Setting Ok"+wendaService.getMessage(1);
    }

}
