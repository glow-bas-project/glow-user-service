package com.glow.user.infrastructure.services;

import com.glow.user.domain.model.User;
import com.glow.user.domain.repository.UserRepository;
import com.glow.user.domain.shared.PageRequest;
import com.glow.user.domain.shared.PageResult;
import com.glow.user.infrastructure.mappers.UserEntityMapper;
import com.glow.user.infrastructure.repository.UserJpaRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class UserRepositoryService implements UserRepository {

    private final UserJpaRepository repository;
    private final UserEntityMapper mapper;

    public UserRepositoryService(UserJpaRepository repository, UserEntityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public void save(User user) {
        repository.persist(mapper.toEntity(user));
    }

    @Override
    @Transactional
    public void update(User user) {
        repository.getEntityManager().merge(mapper.toEntity(user));
    }

    @Override
    @Transactional
    public Optional<User> findById(String id) {
        return repository.findByIdOptional(id)
            .map(mapper::toDomain);
    }

    @Override
    @Transactional
    public Optional<User> findByKeycloakId(String keycloakId) {
        return repository.findByKeycloakId(keycloakId)
            .map(mapper::toDomain);
    }

    @Override
    public boolean existsByKeycloakId(String keycloakId) {
        return repository.existsByKeycloakId(keycloakId);
    }

    @Override
    public PageResult<String> findIds(PageRequest pageRequest) {
        var result = repository.findIds(pageRequest);

        return new PageResult<>(
            result.result(),
            result.nextPage(),
            result.lastPage());
    }

    @Override
    @Transactional
    public List<User> findByIds(List<String> ids) {
        return repository.findUsersByIds(ids).stream()
            .map(mapper::toDomain)
            .toList();
    }

    @Override
    public boolean existsByEmail(String email) {
        return repository.existsByEmail(email);
    }

    @Override
    @Transactional
    public boolean deleteById(String id) {
        return repository.deleteById(id);
    }
}