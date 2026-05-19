package com.glow.user.application.services;

import com.glow.user.application.api.model.ProfileSyncRequest;
import com.glow.user.domain.model.Permission;
import com.glow.user.domain.repository.UserRepository;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for automatic user provisioning on registration (GLOW-11).
 *
 * Registration flow: when a new user completes registration via Keycloak,
 * a corresponding local User record is created automatically on first authenticated request.
 */
@QuarkusTest
public class UserServiceRegistrationTest {

    @Inject
    UserService userService;

    @Inject
    UserRepository userRepository;

    @Test
    @Transactional
    public void ensureUserExists_createsMinimalUserFromJwtOnFirstCall() {
        // given: a new user from Keycloak with keycloakId and email
        var keycloakId = UUID.randomUUID().toString();
        var email = "newuser@example.com";

        // when: ensureUserExists is called for the first time
        userService.ensureUserExists(keycloakId, email);

        // then: a minimal user is created in the database
        var createdUser = userRepository.findByKeycloakId(keycloakId);
        assertTrue(createdUser.isPresent(), "User should be created");

        var user = createdUser.get();
        assertEquals(UUID.fromString(keycloakId), user.getKeycloakId());
        assertEquals(email, user.getEmail());
        assertEquals(email, user.getName(), "Name defaults to email for minimal provisioning");
        assertTrue(user.getPermissions().isEmpty(), "No permissions set on auto-provisioning");
        assertNull(user.getPhoneNumber(), "Phone is null on auto-provisioning");
    }

    @Test
    @Transactional
    public void ensureUserExists_isIdempotent_noErrorOnSecondCall() {
        // given: a user already exists by keycloakId
        var keycloakId = UUID.randomUUID().toString();
        var email = "existing@example.com";
        userService.ensureUserExists(keycloakId, email);
        var firstCall = userRepository.findByKeycloakId(keycloakId);
        assertTrue(firstCall.isPresent());

        // when: ensureUserExists is called again with the same keycloakId
        userService.ensureUserExists(keycloakId, email);

        // then: no error, no duplicate user created
        var allUsers = userRepository.findByKeycloakId(keycloakId);
        assertTrue(allUsers.isPresent());
        assertEquals(firstCall.get().getId(), allUsers.get().getId(), "Same user object returned");
    }

    @Test
    @Transactional
    public void ensureUserExists_preventsEmailDuplicate() {
        // given: a user already exists with an email
        var keycloakId1 = UUID.randomUUID().toString();
        var email = "shared@example.com";
        userService.ensureUserExists(keycloakId1, email);

        // when: ensureUserExists is called with a different keycloakId but same email
        var keycloakId2 = UUID.randomUUID().toString();
        userService.ensureUserExists(keycloakId2, email);

        // then: no duplicate user is created by email
        var byKc1 = userRepository.findByKeycloakId(keycloakId1);
        var byKc2 = userRepository.findByKeycloakId(keycloakId2);
        assertTrue(byKc1.isPresent());
        assertTrue(byKc2.isEmpty(), "Second user not created due to email conflict");
    }

    @Test
    @Transactional
    public void syncUserProfile_idempotentIfUserAlreadyExists() {
        // given: a user was auto-provisioned (minimal: keycloakId, email, name=email, no permissions)
        var keycloakId = UUID.randomUUID().toString();
        var email = "sync@example.com";
        userService.ensureUserExists(keycloakId, email);

        // when: syncUserProfile is called to enrich the user with profile details
        var profileRequest = new ProfileSyncRequest(
            "John Doe",
            "+1234567890",
            List.of(Permission.CUSTOMER),
            null,
            null,
            null,
            null
        );
        var syncedUser = userService.syncUserProfile(keycloakId, email, profileRequest);

        // then: existing user is returned unchanged (syncUserProfile is idempotent for existing users)
        // Profile details are NOT applied (that requires the updateUser endpoint)
        assertEquals(email, syncedUser.name(), "User retains minimal provisioning state");
        assertNull(syncedUser.phoneNumber(), "Phone was not set by sync");
        assertTrue(syncedUser.permissions().isEmpty(), "Permissions were not set by sync");

        // and: in database, the same user exists with original minimal state
        var byKc = userRepository.findByKeycloakId(keycloakId);
        assertTrue(byKc.isPresent());
        assertEquals(email, byKc.get().getName());
    }

    @Test
    @Transactional
    public void syncUserProfile_createsUserOnFirstCallIfMissing() {
        // given: a new user from Keycloak (not yet provisioned)
        var keycloakId = UUID.randomUUID().toString();
        var email = "newprofile@example.com";

        // when: syncUserProfile is called for the first time with profile details (using CUSTOMER permission)
        var profileRequest = new ProfileSyncRequest(
            "Alice",
            "+9876543210",
            List.of(Permission.CUSTOMER),
            List.of(),
            null,
            null,
            null
        );
        var created = userService.syncUserProfile(keycloakId, email, profileRequest);

        // then: user is created with the provided profile details
        assertEquals("Alice", created.name());
        assertEquals("+9876543210", created.phoneNumber());
        assertTrue(created.permissions().contains(Permission.CUSTOMER));

        // and: second call returns the same user (idempotent)
        var second = userService.syncUserProfile(keycloakId, email, profileRequest);
        assertEquals(created.id(), second.id());
    }
}
