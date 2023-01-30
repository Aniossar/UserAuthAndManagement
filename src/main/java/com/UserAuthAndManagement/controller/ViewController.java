package com.UserAuthAndManagement.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class ViewController {

  @GetMapping("/changePassword?token={restoreToken}")
    public String restorePasswordPage(@PathVariable String restoreToken) {
        return "restorePass";
    }
  
}
