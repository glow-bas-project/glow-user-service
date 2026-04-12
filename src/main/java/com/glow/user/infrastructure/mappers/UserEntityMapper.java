package com.glow.user.infrastructure.mappers;

import com.glow.user.domain.model.User;
import com.glow.user.infrastructure.repository.entities.UserJpaEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "cdi")
public interface UserEntityMapper {

    UserJpaEntity toEntity(User user);
    User toDomain(UserJpaEntity entity);
}
