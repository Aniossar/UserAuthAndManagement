package com.UserAuthAndManagement.controller.api;

import com.UserAuthAndManagement.configuration.jwt.JwtProvider;
import com.UserAuthAndManagement.entity.users.UserEntity;
import com.UserAuthAndManagement.exception.BadAuthException;
import com.UserAuthAndManagement.exception.ExistingLoginEmailRegisterException;
import com.UserAuthAndManagement.exception.WrongPasswordUserMovesException;
import com.UserAuthAndManagement.payload.request.users.PasswordChangeRequest;
import com.UserAuthAndManagement.payload.request.users.RefreshTokenRequest;
import com.UserAuthAndManagement.payload.request.users.UserEditRequest;
import com.UserAuthAndManagement.payload.response.AuthentificationResponse;
import com.UserAuthAndManagement.service.users.AuthService;
import com.UserAuthAndManagement.service.users.UserService;
import lombok.NoArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@NoArgsConstructor
@Log
public class AuthSecuredController {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtProvider jwtProvider;

    @PostMapping("/refreshToken")
    public ResponseEntity<AuthentificationResponse> getNewRefreshToken(@RequestBody RefreshTokenRequest request) {
        AuthentificationResponse authResponse = authService.getNewRefreshToken(request.getRefreshToken());
        return ResponseEntity.ok(authResponse);
    }

    @PutMapping("/changeOwnPassword")
    public void changePasswordByUser(@RequestHeader(name = "Authorization") String bearer,
                                     @RequestBody PasswordChangeRequest request) {
        String token = jwtProvider.getTokenFromBearer(bearer);
        String userLogin = jwtProvider.getLoginFromAccessToken(token);
        if (userLogin != null) {
            UserEntity userEntity = userService.findByLoginAndPassword(userLogin, request.getOldPassword());
            if (userEntity != null) {
                userEntity.setPassword(request.getNewPassword());
                userService.updateUserPassword(userEntity);
            } else throw new WrongPasswordUserMovesException("Wrong old password");
        } else throw new BadAuthException("No user is authorized");
    }

    @PutMapping("/editOwnInfo")
    public void editMyself(@RequestHeader(name = "Authorization") String bearer,
                           @RequestBody UserEditRequest request) {

        String token = jwtProvider.getTokenFromBearer(bearer);
        String userLogin = jwtProvider.getLoginFromAccessToken(token);
        UserEntity userEntity = userService.findByLogin(userLogin);

        if (request.getEmail() != null) {
            if (userService.findByEmail(request.getEmail()) == null) {
                userEntity.setEmail(request.getEmail());
            } else {
                log.severe("Trying to use existing email " + request.getEmail() + " for another user");
                throw new ExistingLoginEmailRegisterException("This email is already registered");
            }
        }
        if (request.getFullName() != null) {
            userEntity.setFullName(request.getFullName());
        }
        if (request.getCompanyName() != null) {
            userEntity.setCompanyName(request.getCompanyName());
        }
        if (request.getPhoneNumber() != null) {
            userEntity.setPhoneNumber(request.getPhoneNumber());
        }
        if (request.getAddress() != null) {
            userEntity.setAddress(request.getAddress());
        }

        userService.updateUser(userEntity);
    }

}
