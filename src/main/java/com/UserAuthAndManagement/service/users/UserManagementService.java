package com.UserAuthAndManagement.service.users;

import com.UserAuthAndManagement.entity.users.RoleEntity;
import com.UserAuthAndManagement.entity.users.UserEntity;
import com.UserAuthAndManagement.repository.RoleEntityRepository;
import com.UserAuthAndManagement.repository.UserEntityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class UserManagementService {

    @Autowired
    private UserEntityRepository userEntityRepository;

    private final Map<String, Integer> userRolesMap;

    @Autowired
    public UserManagementService(RoleEntityRepository roleEntityRepository) {
        List<RoleEntity> roleEntityList = roleEntityRepository.findAll();
        this.userRolesMap = roleEntityList.stream().collect(Collectors.toMap(RoleEntity::getName, RoleEntity::getId));
    }

    public boolean isFirstUserCoolerOrEqualThanSecond(String roleName1, String roleName2) {
        return userRolesMap.get(roleName1) <= userRolesMap.get(roleName2);
    }

    @Transactional
    public List<UserEntity> getAllUsers() {
        return userEntityRepository.findAll();
    }
}
