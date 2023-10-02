package com.uap.acreditacion.service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.uap.acreditacion.entity.Persona;

@Service
public class EmailServiceImpl {
    private JavaMailSender javaMailSender;

    @Autowired
    public void EmailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public void enviarEmail(String email, String asunto, String mensaje) throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

        helper.setTo(email);
        helper.setSubject(asunto);
        helper.setText(mensaje, true); // true indica que el contenido es HTML

        javaMailSender.send(mimeMessage);
        System.out.println("Se envio el mensaje: "+mensaje);
    }
}
