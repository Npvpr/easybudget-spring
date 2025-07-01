package com.easybudget.easybudget_spring.guest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/guest")
public class GuestController {

    @Autowired
    private GuestService guestService;

    @GetMapping("/reset-db")
    public ResponseEntity<String> dbReset() {
        try{
            guestService.resetDb();
            return new ResponseEntity<>("Database has been reset successfully for the Guest User.", HttpStatus.OK);
        } catch (IllegalStateException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        }
    }

}
