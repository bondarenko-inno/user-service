package org.ebndrnk.userservice.mapper;

import org.ebndrnk.userservice.model.dto.user.UserCacheDto;
import org.ebndrnk.userservice.model.dto.user.UserRequest;
import org.ebndrnk.userservice.model.dto.user.UserResponse;
import org.ebndrnk.userservice.model.entity.user.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;


/**
 * Mapper interface for converting between User entities and their DTO representations.
 * <p>
 * Supports mapping between UserRequest, UserResponse, UserCacheDto, and User entity.
 * Uses MapStruct for automatic implementation generation.
 */
@Mapper(componentModel = "spring")
public interface UserMapper {

    User toEntity(UserRequest dto);

    UserResponse toDto(User entity);

    void update(@MappingTarget User entity, UserRequest dto);

    UserCacheDto toCacheDto(User entity);

    UserResponse toDto(UserCacheDto cacheDto);
}
