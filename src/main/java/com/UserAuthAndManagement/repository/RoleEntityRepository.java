package com.UserAuthAndManagement.repository;

import com.UserAuthAndManagement.entity.users.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleEntityRepository extends JpaRepository<RoleEntity,Integer> {

    RoleEntity findByName(String name);

}
