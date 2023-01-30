package com.UserAuthAndManagement.payload.request.users;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class RegistrationRequest {

    public enum DesiredRole{
        STORAGE, USER
    }

    private String login;

    private String password;

    private String email;

    private String fullName;

    private String companyName;

    private String phoneNumber;

    private String address;

    private DesiredRole desiredRole;

}
