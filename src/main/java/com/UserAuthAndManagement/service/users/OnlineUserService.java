package com.UserAuthAndManagement.service.users;

import com.UserAuthAndManagement.entity.users.OnlineUserEntity;
import com.UserAuthAndManagement.repository.OnlineUserEntityRepository;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Log
public class OnlineUserService {

    @Autowired
    private OnlineUserEntityRepository onlineUserEntityRepository;

    @Transactional
    public List<OnlineUserEntity> getAllUsers() {
        return onlineUserEntityRepository.findAll();
    }

    @Transactional
    public OnlineUserEntity getUserViaLogin(String login) {
        return onlineUserEntityRepository.findByUserLogin(login);
    }

    public OnlineUserEntity getUserViaId(int id) {
        return onlineUserEntityRepository.getById(id);
    }

    @Transactional
    public void saveUserLine(OnlineUserEntity onlineUserEntity) {
        onlineUserEntityRepository.saveAndFlush(onlineUserEntity);
    }

    @Transactional
    public void deleteUserLine(OnlineUserEntity onlineUserEntity) {
        onlineUserEntityRepository.delete(onlineUserEntity);
    }
}
