package com.UserAuthAndManagement.payload.request.users;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserEditRequest {

    public enum NewRole {
        ADMIN, MODERATOR, STORAGE, USER
    }

    private String login;

    private String email;

    private String fullName;

    private String companyName;

    private String phoneNumber;

    private String address;

    private NewRole newRole;

    private Boolean enabled;
}
