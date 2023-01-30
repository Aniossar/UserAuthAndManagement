package com.UserAuthAndManagement.aspect;

import com.UserAuthAndManagement.entity.ActivityEntity;
import com.UserAuthAndManagement.entity.users.UserEntity;
import com.UserAuthAndManagement.service.activity.ActivityService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@Aspect
public class LoggingAspect {

    @Autowired
    private ActivityService activityService;

    private final String userTypeActivity = "User Activity";

    @AfterReturning("execution(* com.UserAuthAndManagement.configuration.jwt.JwtProvider.generateAccessToken(String))"
            + "&&args(loginString)")
    public void loginUserAdvice(JoinPoint joinPoint, String loginString) {
        Instant timeStamp = Instant.now();
        ActivityEntity activityEntity = new ActivityEntity(timeStamp, userTypeActivity, loginString,
                "User logins into system");
        activityService.saveActivity(activityEntity);
    }

    @Before("execution(* org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler.logout(..))")
    public void logoutUserAdvice(JoinPoint joinPoint) {
        Instant timeStamp = Instant.now();
        String logoutName = SecurityContextHolder.getContext().getAuthentication().getName();
        ActivityEntity activityEntity = new ActivityEntity(timeStamp, userTypeActivity, logoutName,
                "User logout from system");
        activityService.saveActivity(activityEntity);
    }

    @AfterReturning("execution(* com.UserAuthAndManagement.service.users.UserService.saveUser(..))" + "&&args(userEntity,..)")
    public void saveNewUserAdvice(JoinPoint joinPoint, UserEntity userEntity) {
        Instant timeStamp = Instant.now();
        ActivityEntity activityEntity = new ActivityEntity(timeStamp, userTypeActivity, userEntity.getLogin(),
                "New user registered with email: " + userEntity.getEmail());
        activityService.saveActivity(activityEntity);
    }

    @AfterReturning("execution(* com.UserAuthAndManagement.service.users.UserService.deleteUser(..))" + "&&args(login,..)")
    public void deleteUserAdvice(JoinPoint joinPoint, String login) {
        Instant timeStamp = Instant.now();
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        ActivityEntity activityEntity = new ActivityEntity(timeStamp, userTypeActivity, userName,
                "Deleted user: " + login);
        activityService.saveActivity(activityEntity);
    }

    @AfterReturning("execution(* com.UserAuthAndManagement.service.users.UserService.updateUser(..))" + "&&args(userEntity,..)")
    public void editUserAdvice(JoinPoint joinPoint, UserEntity userEntity) {
        Instant timeStamp = Instant.now();
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        ActivityEntity activityEntity = new ActivityEntity(timeStamp, userTypeActivity, userName,
                "Edited user: " + userEntity.getLogin());
        activityService.saveActivity(activityEntity);
    }

}
