package com.glow.user.domain.repository;

import com.glow.user.application.common.PageRequest;
import com.glow.user.application.common.PageResult;
import com.glow.user.domain.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {

    void save(User user);
    void update(User user);

    Optional<User> findById(String id);
    Optional<User> findByKeycloakId(String keycloakId);
    PageResult<String> findIds(PageRequest pageRequest);
    List<User> findByIds(List<String> ids);

    boolean existsByEmail(String email);
    boolean deleteById(String id);
}