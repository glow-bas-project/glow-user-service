package com.glow.user.application.services;

import com.glow.user.application.api.model.CreateUserRequest;
import com.glow.user.application.api.model.UpdateUserRequest;
import com.glow.user.domain.shared.PageRequest;
import com.glow.user.domain.shared.PageResult;
import com.glow.user.application.mappers.UserDtoMapper;
import com.glow.user.application.model.UserDto;
import com.glow.user.domain.model.*;
import com.glow.user.domain.repository.UserRepository;
import com.glow.user.domain.shared.DomainException;
import jakarta.ws.rs.NotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    UserRepository userRepository;

    @Mock
    UserDtoMapper mapper;

    @InjectMocks
    UserService service;

    @Test
    public void createUser_withCustomer_savesAndMapsToDto() {
        // given
        var request = new CreateUserRequest(
            "John Doe",
            "+1234567890",
            "john@example.com",
            List.of(Permission.CUSTOMER),
            List.of(new Address("1 Main St", "City", "Country", 0.0, 0.0)),
            null,
            null,
            null);
        var keycloakSubject = UUID.randomUUID().toString();

        when(mapper.toDto(any(User.class))).thenAnswer(invocation -> {
            User u = invocation.getArgument(0);
            return new UserDto(
                u.getId().toString(),
                u.getName(),
                u.getPhoneNumber(),
                u.getEmail(),
                u.getPermissions(),
                u instanceof CustomerUser ? ((CustomerUser) u).getSavedAddresses() : null,
                u instanceof CourierUser ? ((CourierUser) u).getVehicleType() : null,
                u instanceof CourierUser ? ((CourierUser) u).getCourierAvailability() : null,
                u instanceof RestaurantUser ? ((RestaurantUser) u).getRestaurantId().toString() : null);
        });

        // when
        var dto = service.createUser(request, keycloakSubject);

        // then
        assertEquals("John Doe", dto.name());
        assertEquals("john@example.com", dto.email());
        verify(userRepository).save(any(User.class));
        verify(mapper).toDto(any(User.class));
    }

    @Test
    public void createUser_withMultipleProfilePermissions_throwsDomainException() {
        // given
        var request = new CreateUserRequest(
            "John Doe",
            "+1234567890",
            "john@example.com",
            List.of(Permission.CUSTOMER, Permission.COURIER),
            null,
            VehicleType.CAR,
            null,
            null);
        var keycloakSubject = UUID.randomUUID().toString();

        // when
        var ex = assertThrows(DomainException.class, () -> service.createUser(request, keycloakSubject));

        // then
        assertEquals("User can only have one profile permission among CUSTOMER, COURIER, and RESTAURANT_USER", ex.getMessage());
    }

    @Test
    public void findById_throwsWhenMissing() {
        // given
        when(userRepository.findById("missing")).thenReturn(Optional.empty());

        // when / then
        assertThrows(NotFoundException.class, () -> service.findById("missing"));
    }

    @Test
    public void findById_returnsDtoWhenPresent() {
        // given
        var user = User.builder()
            .name("John Doe")
            .email("john@example.com")
            .keycloakId(UUID.randomUUID())
            .build();
        var id = user.getId().toString();
        var expected = new UserDto(
            id, "John Doe", "+1", "john@example.com",
            List.of(), null, null, null, null);
        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        when(mapper.toDto(user)).thenReturn(expected);

        // when
        var actual = service.findById(id);

        // then
        assertEquals(expected, actual);
    }

    @Test
    public void updateUser_withValidPermissions_savesAndReturnsDto() {
        // given
        var userId = UUID.randomUUID().toString();
        var existing = User.builder()
            .id(UUID.fromString(userId))
            .name("John Doe")
            .email("john@example.com")
            .phoneNumber("+1234567890")
            .keycloakId(UUID.randomUUID())
            .permissions(List.of(Permission.CUSTOMER))
            .build();
        var request = new UpdateUserRequest(
            "Jane Doe",
            "+9999999999",
            "jane@example.com",
            List.of(Permission.CUSTOMER),
            List.of(),
            null,
            null,
            null);
        var updated = new UserDto(
            userId, "Jane Doe", "+9999999999", "jane@example.com",
            List.of(Permission.CUSTOMER), null, null, null, null);

        when(userRepository.findById(userId)).thenReturn(Optional.of(existing));
        when(mapper.toDto(any(User.class))).thenReturn(updated);

        // when
        var dto = service.updateUser(userId, request);

        // then
        assertEquals("Jane Doe", dto.name());
        verify(userRepository).update(any(User.class));
    }

    @Test
    public void updateUser_withMultipleProfilePermissions_throwsDomainException() {
        // given
        var userId = UUID.randomUUID().toString();
        var existing = User.builder()
            .id(UUID.fromString(userId))
            .name("John Doe")
            .email("john@example.com")
            .keycloakId(UUID.randomUUID())
            .permissions(List.of(Permission.CUSTOMER))
            .build();
        var request = new UpdateUserRequest(
            "John Doe",
            "+1234567890",
            "john@example.com",
            List.of(Permission.CUSTOMER, Permission.COURIER),
            null,
            VehicleType.BIKE,
            null,
            null);

        when(userRepository.findById(userId)).thenReturn(Optional.of(existing));

        // when
        var ex = assertThrows(DomainException.class, () -> service.updateUser(userId, request));

        // then
        assertEquals("User can only have one profile permission among CUSTOMER, COURIER, and RESTAURANT_USER", ex.getMessage());
    }

    @Test
    public void findUserIds_delegatesToRepository() {
        // given
        var pageResult = new PageResult<>(List.of(UUID.randomUUID().toString()), 1, true);
        when(userRepository.findIds(any(PageRequest.class))).thenReturn(pageResult);

        // when
        var actual = service.findUserIds(10, 0);

        // then
        assertEquals(pageResult, actual);
        var captor = ArgumentCaptor.forClass(PageRequest.class);
        verify(userRepository).findIds(captor.capture());
        assertEquals(10, captor.getValue().size());
        assertEquals(0, captor.getValue().page());
    }

    @Test
    public void materialise_mapsEachUser() {
        // given
        var user = User.builder()
            .name("John Doe")
            .email("john@example.com")
            .keycloakId(UUID.randomUUID())
            .build();
        var id = user.getId().toString();
        var dto = new UserDto(id, user.getName(), user.getPhoneNumber(), user.getEmail(),
            user.getPermissions(), null, null, null, null);
        when(userRepository.findByIds(List.of(id))).thenReturn(List.of(user));
        when(mapper.toDto(user)).thenReturn(dto);

        // when
        var actual = service.materialise(List.of(id));

        // then
        assertEquals(List.of(dto), actual);
    }

    @Test
    public void deleteUserById_returnsTrueWhenRepositoryDeletes() {
        // given
        when(userRepository.deleteById("x")).thenReturn(true);

        // when
        var deleted = service.deleteUserById("x");

        // then
        assertTrue(deleted);
    }

    @Test
    public void deleteUserById_returnsFalseWhenRepositoryDoesNotDelete() {
        // given
        when(userRepository.deleteById("y")).thenReturn(false);

        // when
        var deleted = service.deleteUserById("y");

        // then
        assertFalse(deleted);
    }
}
