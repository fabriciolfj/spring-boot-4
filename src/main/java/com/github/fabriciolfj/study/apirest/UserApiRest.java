package com.github.fabriciolfj.study.apirest;


import com.github.fabriciolfj.study.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


public interface UserApiRest {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    void create(@RequestBody final User user);


    @GetMapping("/{id}")
    @Operation(summary = "buscar usuario por id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "ok"),
            @ApiResponse(responseCode = "404")
    })
    ResponseEntity<User> getUser(@PathVariable final Long id);
}
