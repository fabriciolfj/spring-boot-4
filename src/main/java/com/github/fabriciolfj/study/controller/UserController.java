package com.github.fabriciolfj.study.controller;

import com.github.fabriciolfj.study.apirest.UserApiRest;
import com.github.fabriciolfj.study.entity.User;
import com.github.fabriciolfj.study.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController implements UserApiRest {

    private final UserService userService;

    @Override
    public void create(@RequestBody final User user) {
        userService.create(user);
    }

    @Override
    public ResponseEntity<User> getUser(@PathVariable final Long id) {
        var user = userService.getUser(id);
        return user.map(value -> ResponseEntity.accepted().body(value)).orElseGet(() -> ResponseEntity.notFound().build());

    }
}
