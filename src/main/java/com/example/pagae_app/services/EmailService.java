package com.example.pagae_app.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private JavaMailSender mailSender;

    /**
     * Method to send email
     * @param to
     * @param subject
     * @param body
     */
    public void sendEmail(String to, String subject, String body) {
        try{
            SimpleMailMessage message = new SimpleMailMessage();

            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);

            log.info("Email enviado com sucesso para {}", to);
        } catch (Exception e) {
            log.error(e.getMessage());
        }

    }
}
