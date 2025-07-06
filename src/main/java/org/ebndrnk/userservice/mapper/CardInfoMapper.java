package org.ebndrnk.userservice.mapper;

import org.ebndrnk.userservice.model.dto.card.CardInfoCacheDto;
import org.ebndrnk.userservice.model.dto.card.CardInfoRequest;
import org.ebndrnk.userservice.model.dto.card.CardInfoResponse;
import org.ebndrnk.userservice.model.entity.card.CardInfo;
import org.ebndrnk.userservice.model.entity.user.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;


/**
 * Mapper interface for converting between CardInfo entities and their DTO representations.
 * <p>
 * Supports mapping between CardInfoRequest, CardInfoResponse, CardInfoCacheDto, and CardInfo entity.
 * Uses MapStruct for automatic implementation generation.
 */
@Mapper(componentModel = "spring")
public interface CardInfoMapper {

    @Mapping(source = "userId", target = "user")
    CardInfo toEntity(CardInfoRequest dto);

    @Mapping(source = "user", target = "userId")
    CardInfoResponse toDto(CardInfo entity);

    @Mapping(source = "userId", target = "user")
    void update(@MappingTarget CardInfo entity, CardInfoRequest dto);

    @Mapping(source = "user", target = "userId")
    CardInfoCacheDto toCacheDto(CardInfo entity);

    CardInfoResponse toDto(CardInfoCacheDto cacheDto);

    default User map(Long userId) {
        if (userId == null) {
            return null;
        }
        User user = new User();
        user.setId(userId);
        return user;
    }

    default Long map(User user) {
        if (user == null) {
            return null;
        }
        return user.getId();
    }
}


