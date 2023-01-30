package com.UserAuthAndManagement.controller.api;

import com.UserAuthAndManagement.entity.ActivityEntity;
import com.UserAuthAndManagement.service.activity.ActivityService;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
@Log
public class ActivityController {

    @Autowired
    private ActivityService activityService;

    @GetMapping("/allActivities")
    public List<ActivityEntity> getAllActivities() {
        return activityService.getAllActivities();
    }

    @GetMapping("/getUserActivities/{login}")
    public List<ActivityEntity> getUserActivities(@PathVariable String login) {
        return activityService.getCertainUserActivities(login);
    }
}
