package com.nowcoder.wenda.util;

import com.nowcoder.wenda.WendaApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = WendaApplication.class)
public class MailSenderTest {
    //private static MailSender mailSender = new MailSender();

    @Autowired
    MailSender mailSender;

    @Test
    public void sendWithHTMLTemplate() {
        Map<String,Object> map = new HashMap<String, Object>();
        map.put("username","ming");
        mailSender.sendWithHTMLTemplate("2256939775@qq.com","登录IP异常",
                "mails/login_exception.html",map);
    }
}