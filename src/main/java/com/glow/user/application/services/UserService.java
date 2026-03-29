package com.glow.user.application.services;

import com.glow.user.application.api.model.CreateUserRequest;
import com.glow.user.application.common.PageRequest;
import com.glow.user.application.common.PageResult;
import com.glow.user.application.mappers.UserDtoMapper;
import com.glow.user.application.model.UserDto;
import com.glow.user.domain.model.*;
import com.glow.user.domain.repository.UserRepository;
import com.glow.user.domain.shared.DomainException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class UserService {

    private static final Logger LOG = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final UserDtoMapper mapper;

    public UserService(UserRepository userRepository, UserDtoMapper mapper) {
        this.userRepository = userRepository;
        this.mapper = mapper;
    }

    public UserDto createUser(CreateUserRequest request) {
        var permissions = request.permissions() == null ? List.<Permission>of() : request.permissions();
        var keycloakId = resolveKeycloakId(request.keycloakId());

        User.Builder builder = resolveBuilder(request, permissions);
        builder.keycloakId(keycloakId);
        builder.name(request.name());
        builder.phoneNumber(request.phoneNumber());
        builder.email(request.email());
        builder.permissions(permissions);

        var user = builder.build();

        userRepository.save(user);
        return mapper.toDto(user);
    }

    private UUID resolveKeycloakId(String rawKeycloakId) {
        if (rawKeycloakId == null || rawKeycloakId.isBlank()) {
            throw new DomainException("User keycloakId is required and cannot be blank");
        }

        try {
            return UUID.fromString(rawKeycloakId);
        } catch (IllegalArgumentException ex) {
            throw new DomainException("User keycloakId must be a valid UUID");
        }
    }

    private User.Builder resolveBuilder(CreateUserRequest request, List<Permission> permissions) {
        if (permissions.contains(Permission.CUSTOMER)) {
            return CustomerUser.builder().savedAddresses(
                request.savedAddresses() == null ? List.of() : request.savedAddresses());
        }

        if (permissions.contains(Permission.COURIER)) {
            return CourierUser.builder().vehicleType(request.vehicleType());
        }

        if (permissions.contains(Permission.RESTAURANT_USER) &&
            request.restaurantId() != null && !request.restaurantId().isBlank()) {
            return RestaurantUser.builder().restaurantId(UUID.fromString(request.restaurantId()));
        }

        return User.builder();
    }

    public UserDto findById(String id) {
        return mapper.toDto(
            userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User with id " + id + " not found")));
    }

    public PageResult<String> findUserIds(int size, int page) {
        // TODO: Build some defaults but for now this is fine
        return userRepository.findIds(new PageRequest(size, page));
    }

    public List<UserDto> materialise(List<String> ids) {
        return userRepository.findByIds(ids).stream()
            .map(mapper::toDto)
            .toList();
    }

    public boolean deleteUserById(String id) {
        return userRepository.deleteById(id);
    }
}