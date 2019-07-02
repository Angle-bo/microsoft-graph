package com.luobo.microsoftgraph.mail;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * 邮件发送示例
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class MailTest {

    @Value("${spring.mail.username}")
    private  String setForm;

    @Autowired
    private JavaMailSender mailSender;

    @Test
    public void testMail() {
        SimpleMailMessage message = new SimpleMailMessage();
        //13123

        message.setFrom(setForm);//发送者.
        message.setTo("867030436@qq.com");//接收者.
        message.setSubject("测试邮件（邮件主题）");//邮件主题.
        message.setText("这是邮件内容");//邮件内容.

        mailSender.send(message);//发送邮件
    }

}
