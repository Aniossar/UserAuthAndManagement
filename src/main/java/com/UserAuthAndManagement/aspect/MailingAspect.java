package com.UserAuthAndManagement.aspect;

import com.UserAuthAndManagement.entity.EmailContext;
import com.UserAuthAndManagement.payload.request.users.RegistrationRequest;
import com.UserAuthAndManagement.service.EmailService;
import lombok.extern.java.Log;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import java.util.HashMap;
import java.util.Map;

@Component
@Aspect
@Log
public class MailingAspect {

    @Autowired
    private EmailService emailService;

    @AfterReturning("execution(* com.UserAuthAndManagement.controller.AuthController.registerUser(..))"
            + "&&args(registrationRequest)")
    public void loginUserAdvice(JoinPoint joinPoint, RegistrationRequest registrationRequest) {
        try {
            EmailContext emailContext = new EmailContext();
            emailContext.setFrom("noreply@gmail.ru");
            emailContext.setSubject("Welcome to the portal!");
            emailContext.setTo(registrationRequest.getEmail());
            emailContext.setTemplateLocation("greetingLetter");
            Map<String, Object> context = new HashMap<>();
            context.put("email", registrationRequest.getEmail());
            emailContext.setContext(context);
            emailService.sendMail(emailContext);
            log.info("Sent welcome letter to email " + registrationRequest.getEmail());
        } catch (MessagingException e) {
            log.severe("Error while sending out email — " + e.getLocalizedMessage());
        }
    }
}
