package com.example.userservice.service;

import com.example.userservice.dto.UserDto;
import com.example.userservice.jpa.UserEntity;
import org.springframework.security.core.userdetails.UserDetails;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public interface UserService {
    UserDto createUser(UserDto userDto);
    UserDto getUserByUserId(String userId);
    UserDto getUserDetailByEmail(String email);

    Iterable<UserEntity> getUserByAll();

    UserDetails loadUserByUsername(@NotNull(message = "Email cannot be null") @Size(min = 2, message = "Email not be less than two characters") @Email String email);
}

