package com.easybudget.easybudget_spring.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/user")
    public ResponseEntity<UserDto> getUserInfos() {
        return new ResponseEntity<>(userService.getUserInfos(), HttpStatus.OK);
    }

    @PutMapping("/currency")
    public ResponseEntity<String> updateCurrency(@RequestParam String currency) {
        return new ResponseEntity<>(userService.updateCurrency(currency), HttpStatus.OK);
    }
}
