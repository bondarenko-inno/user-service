package org.ebndrnk.userservice.mapper;

import org.ebndrnk.userservice.model.dto.card.CardInfoRequest;
import org.ebndrnk.userservice.model.dto.card.CardInfoResponse;
import org.ebndrnk.userservice.model.entity.card.CardInfo;
import org.ebndrnk.userservice.model.entity.user.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CardInfoMapper {

    @Mapping(source = "userId", target = "user")
    CardInfo toEntity(CardInfoRequest dto);

    @Mapping(source = "user", target = "userId")
    CardInfoResponse toDto(CardInfo entity);

    @Mapping(source = "userId", target = "user")
    void update(@MappingTarget CardInfo entity, CardInfoRequest dto);

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


