package com.UserAuthAndManagement.repository;

import com.UserAuthAndManagement.entity.users.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserEntityRepository extends JpaRepository<UserEntity, Integer> {

    UserEntity findByLogin(String login);

    UserEntity findByEmail(String email);
}
