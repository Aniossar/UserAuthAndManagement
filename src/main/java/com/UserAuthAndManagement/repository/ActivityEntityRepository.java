package com.UserAuthAndManagement.repository;

import com.UserAuthAndManagement.entity.ActivityEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ActivityEntityRepository extends JpaRepository<ActivityEntity, Integer> {

    List<ActivityEntity> findByLogin(String login);

    void deleteActivityEntitiesByLogin(String login);

    List<ActivityEntity> findByActivityType(String activityType);

}
