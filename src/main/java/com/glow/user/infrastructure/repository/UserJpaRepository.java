package com.glow.user.infrastructure.repository;

import com.glow.user.application.common.PageRequest;
import com.glow.user.application.common.PageResult;
import com.glow.user.infrastructure.repository.entities.UserJpaEntity;
import com.glow.user.infrastructure.repository.projections.UserIdProjection;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class UserJpaRepository implements PanacheRepositoryBase<UserJpaEntity, String> {

    public PageResult<String> findIds(PageRequest pageRequest) {
        var query = findAll()
            .page(pageRequest.page(), pageRequest.size())
            .project(UserIdProjection.class);
        var hasNext = query.hasNextPage();

        return new PageResult<>(
            query.list().stream().map(UserIdProjection::id).toList(),
            hasNext ? pageRequest.page() + 1 : pageRequest.page(),
            !hasNext);
    }

    public List<UserJpaEntity> findUsersByIds(List<String> ids) {
        return list("id in ?1", ids);
    }

    public Optional<UserJpaEntity> findByKeycloakId(String keycloakId) {
        return find("keycloakId", keycloakId).firstResultOptional();
    }

    public boolean existsByEmail(String email) {
        return count("email", email) > 0;
    }
}