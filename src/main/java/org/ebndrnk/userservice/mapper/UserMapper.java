package org.ebndrnk.userservice.mapper;

import org.ebndrnk.userservice.model.dto.user.UserCacheDto;
import org.ebndrnk.userservice.model.dto.user.UserRequest;
import org.ebndrnk.userservice.model.dto.user.UserResponse;
import org.ebndrnk.userservice.model.entity.user.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User toEntity(UserRequest dto);

    UserResponse toDto(User entity);

    void update(@MappingTarget User entity, UserRequest dto);

    UserCacheDto toCacheDto(User entity);

    UserResponse toDto(UserCacheDto cacheDto);
}
