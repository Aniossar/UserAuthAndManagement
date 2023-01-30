package com.UserAuthAndManagement.service.users;

import com.UserAuthAndManagement.entity.users.RoleEntity;
import com.UserAuthAndManagement.entity.users.UserEntity;
import com.UserAuthAndManagement.payload.request.users.RegistrationRequest;
import com.UserAuthAndManagement.repository.RoleEntityRepository;
import com.UserAuthAndManagement.repository.UserEntityRepository;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Log
public class UserService {

    @Autowired
    private UserEntityRepository userEntityRepository;

    @Autowired
    private RoleEntityRepository roleEntityRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public UserEntity saveUser(UserEntity userEntity, RegistrationRequest.DesiredRole desiredRole) {
        log.info("Saving new user to database " + userEntity.getLogin());
        RoleEntity userRole = roleEntityRepository.findByName("ROLE_" + desiredRole);
        userEntity.setRoleEntity(userRole);
        userEntity.setPassword(passwordEncoder.encode(userEntity.getPassword()));
        return userEntityRepository.save(userEntity);
    }

    public UserEntity updateUserPassword(UserEntity userEntity) {
        userEntity.setPassword(passwordEncoder.encode(userEntity.getPassword()));
        return userEntityRepository.save(userEntity);
    }

    public UserEntity updateUser(UserEntity userEntity) {
        return userEntityRepository.save(userEntity);
    }

    public void deleteUser(String login) {
        UserEntity userEntity = findByLogin(login);
        userEntityRepository.delete(userEntity);
    }

    public UserEntity findByLogin(String login) {
        return userEntityRepository.findByLogin(login);
    }

    public UserEntity findByEmail(String email) {
        return userEntityRepository.findByEmail(email);
    }

    public Optional<UserEntity> findById(int id) {
        return userEntityRepository.findById(id);
    }

    public UserEntity findByLoginAndPassword(String login, String password) {
        UserEntity userEntity = findByLogin(login);
        if (userEntity != null) {
            if (passwordEncoder.matches(password, userEntity.getPassword())) {
                return userEntity;
            }
        }
        return null;
    }
}
