package com.glow.user.infrastructure.services;

import com.glow.user.domain.model.Permission;
import com.glow.user.domain.model.User;
import com.glow.user.domain.repository.UserRepository;
import com.glow.user.domain.shared.PageRequest;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
public class UserRepositoryServiceIntegrationTest {

    @Inject
    UserRepository repository;

    @Test
    @Transactional
    public void saveAndFindById_roundTrips() {
        // given
        var user = newUser("Integration User", "john@example.com");

        // when
        repository.save(user);
        var id = user.getId().toString();
        var found = repository.findById(id);

        // then
        assertTrue(found.isPresent());
        assertEquals("Integration User", found.get().getName());
        assertEquals("john@example.com", found.get().getEmail());
    }

    @Test
    @Transactional
    public void findIds_returnsPersistedId() {
        // given
        var user = newUser("Paged", "paged@example.com");
        repository.save(user);
        var id = user.getId().toString();

        // when
        var page = repository.findIds(new PageRequest(10, 0));

        // then
        assertTrue(page.result().contains(id));
    }

    @Test
    @Transactional
    public void findByIds_returnsMatching() {
        // given
        var a = newUser("User A", "a@example.com");
        var b = newUser("User B", "b@example.com");
        repository.save(a);
        repository.save(b);
        var ids = List.of(a.getId().toString(), b.getId().toString());

        // when
        var list = repository.findByIds(ids);

        // then
        assertEquals(2, list.size());
    }

    @Test
    @Transactional
    public void deleteById_removesUser() {
        // given
        var user = newUser("To Delete", "delete@example.com");
        repository.save(user);
        var id = user.getId().toString();

        // when
        var deleted = repository.deleteById(id);
        var after = repository.findById(id);

        // then
        assertTrue(deleted);
        assertTrue(after.isEmpty());
    }

    private static User newUser(String name, String email) {
        return User.builder()
            .name(name)
            .email(email)
            .phoneNumber("+1234567890")
            .keycloakId(UUID.randomUUID())
            .build();
    }
}
