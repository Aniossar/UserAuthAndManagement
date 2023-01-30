package com.UserAuthAndManagement.controller;

import com.UserAuthAndManagement.configuration.CustomUserDetailsService;
import com.UserAuthAndManagement.configuration.jwt.JwtProvider;
import com.UserAuthAndManagement.entity.EmailContext;
import com.UserAuthAndManagement.entity.users.UserEntity;
import com.UserAuthAndManagement.exception.BadAuthException;
import com.UserAuthAndManagement.exception.ExistingLoginEmailRegisterException;
import com.UserAuthAndManagement.exception.WrongPasswordUserMovesException;
import com.UserAuthAndManagement.payload.request.SingleMessageRequest;
import com.UserAuthAndManagement.payload.request.users.AuthentificationRequest;
import com.UserAuthAndManagement.payload.request.users.PasswordResetRequest;
import com.UserAuthAndManagement.payload.request.users.RefreshTokenRequest;
import com.UserAuthAndManagement.payload.request.users.RegistrationRequest;
import com.UserAuthAndManagement.payload.response.AuthentificationResponse;
import com.UserAuthAndManagement.payload.response.MeResponse;
import com.UserAuthAndManagement.service.EmailService;
import com.UserAuthAndManagement.service.users.AuthService;
import com.UserAuthAndManagement.service.users.UserService;
import lombok.NoArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.mail.MessagingException;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.util.StringUtils.hasText;


@RestController
@NoArgsConstructor
@Log
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthService authService;

    @Autowired
    CustomUserDetailsService customUserDetailsService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private JwtProvider jwtProvider;

    @PostMapping("/register")
    public String registerUser(@RequestBody @Valid RegistrationRequest registrationRequest) {
        UserEntity userEntity = new UserEntity();
        if (userService.findByLogin(registrationRequest.getLogin()) != null
                || userService.findByEmail(registrationRequest.getEmail()) != null) {
            log.warning("Trying to register user with existing email or login");
            throw new ExistingLoginEmailRegisterException("This login or email is already registered");
        }
        userEntity.setPassword(registrationRequest.getPassword());
        userEntity.setLogin(registrationRequest.getLogin());
        userEntity.setEmail(registrationRequest.getEmail());
        userEntity.setFullName(registrationRequest.getFullName());
        userEntity.setCompanyName(registrationRequest.getCompanyName());
        userEntity.setPhoneNumber(registrationRequest.getPhoneNumber());
        userEntity.setAddress(registrationRequest.getAddress());
        userEntity.setEnabled(true);
        userService.saveUser(userEntity, registrationRequest.getDesiredRole());
        return "OK";
    }

    @PostMapping("/auth")
    public ResponseEntity<AuthentificationResponse> auth(@RequestBody AuthentificationRequest request) {
        AuthentificationResponse authResponse = authService.authenticate(request);
        return ResponseEntity.ok(authResponse);
    }

    @PostMapping("/token")
    public ResponseEntity<AuthentificationResponse> getNewAccessToken(@RequestBody RefreshTokenRequest request) {
        AuthentificationResponse authResponse = authService.getAccessToken(request.getRefreshToken());
        return ResponseEntity.ok(authResponse);
    }

    @GetMapping("/me")
    public MeResponse checkUserAuth(@RequestHeader(name = "Authorization") String bearer) {
        String token = null;
        if (hasText(bearer) && bearer.startsWith("Bearer ")) {
            token = bearer.substring(7);
        }
        if (token != null && jwtProvider.validateAccessToken(token)) {
            String userLogin = jwtProvider.getLoginFromAccessToken(token);
            String roleFromToken = jwtProvider.getRoleFromAccessToken(token);
            return new MeResponse(userLogin, roleFromToken);
        } else throw new BadAuthException("No user is authorized");
    }

    @PostMapping("/forgottenPassword")
    public String situationWithTheForgottenPassword(@RequestBody SingleMessageRequest request) {
        String usersMail = request.getMessage();
        UserEntity userEntity = userService.findByEmail(usersMail);
        if (userEntity != null) {
            String restoringToken = authService.generateRestoringPasswordToken(userEntity.getLogin());
            String contextPath = ServletUriComponentsBuilder.fromCurrentContextPath().toUriString();
            String restorePasswordUrl = contextPath + "/changePassword?token=" + restoringToken;
            try {
                EmailContext emailContext = new EmailContext();
                emailContext.setFrom("noreply@gmail.com");
                emailContext.setSubject("Let's restore your password!");
                emailContext.setTo(usersMail);
                emailContext.setTemplateLocation("passwordRestoreLetter");
                Map<String, Object> context = new HashMap<>();
                context.put("restorePwLink", restorePasswordUrl);
                emailContext.setContext(context);
                emailService.sendMail(emailContext);
                log.info("Sent restoring letter to email " + usersMail);
                return "OK";
            } catch (MessagingException e) {
                log.severe("Error while sending out email — " + e.getLocalizedMessage());
            }
        } else {
            throw new BadAuthException("User mail not found");
        }
        return "NOK";
    }

    @PutMapping("/resetPassword")
    public void resetPassword(@RequestBody PasswordResetRequest request) {
        String loginFromRestoreToken = authService.getLoginFromRestoreToken(request.getRestoreToken());
        if (loginFromRestoreToken != null) {
            UserEntity userEntity = userService.findByLogin(loginFromRestoreToken);
            if (userEntity != null) {
                userEntity.setPassword(request.getNewPassword());
                userService.updateUserPassword(userEntity);
            } else throw new WrongPasswordUserMovesException("Wrong old password");
        }
        throw new BadAuthException("Need authorization");
    }
}