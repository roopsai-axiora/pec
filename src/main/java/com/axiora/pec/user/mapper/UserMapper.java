package com.axiora.pec.user.mapper;

import com.axiora.pec.user.domain.User;
import com.axiora.pec.user.dto.AuthResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "token", ignore = true)
    @Mapping(target = "role",
            expression = "java(user.getRole().name())")
    AuthResponse toAuthResponse(User user);
}