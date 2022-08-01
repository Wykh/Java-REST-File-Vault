package com.example.filevault.controller;

import com.example.filevault.dto.UserDto;
import com.example.filevault.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserDto> registerOne(@RequestParam String username,
                                               @RequestParam String password) {

        return ResponseEntity.ok(userService.registerOne(username, password));
    }

    @PutMapping("/{username}")
    ResponseEntity<UserDto> updateOne(@PathVariable String username,
                                      @RequestParam(required = false) String role,
                                      @RequestParam(required = false) Boolean isBlocked) {
        return ResponseEntity.ok(userService.updateOne(username, role, isBlocked));
    }

}
