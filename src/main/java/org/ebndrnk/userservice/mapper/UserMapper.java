package org.ebndrnk.userservice.mapper;

import org.ebndrnk.userservice.kafka.dto.UserCreatedEvent;
import org.ebndrnk.userservice.model.dto.user.UserCacheDto;
import org.ebndrnk.userservice.model.dto.user.UserInfoForOrder;
import org.ebndrnk.userservice.model.dto.user.UserRequest;
import org.ebndrnk.userservice.model.dto.user.UserResponse;
import org.ebndrnk.userservice.model.entity.user.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;


/**
 * Mapper interface for converting between User entities and their DTO representations.
 * <p>
 * Supports mapping between UserRequest, UserResponse, UserCacheDto, and User entity.
 * Uses MapStruct for automatic implementation generation.
 */
@Mapper(componentModel = "spring")
public interface UserMapper {

    User requestToEntity(UserRequest dto);

    UserResponse entityToResponse(User entity);

    void update(@MappingTarget User entity, UserRequest dto);

    UserCacheDto entityToCacheDto(User entity);

    UserResponse cacheDtoToResponse(UserCacheDto cacheDto);

    UserRequest eventToRequest(UserCreatedEvent event);


    UserInfoForOrder entityToUserInfo(User user);
}
