package com.glow.user.application.services;

import com.glow.user.application.api.model.CreateUserRequest;
import com.glow.user.application.api.model.ProfileSyncRequest;
import com.glow.user.application.api.model.UpdateUserRequest;
import com.glow.user.domain.shared.PageRequest;
import com.glow.user.domain.shared.PageResult;
import com.glow.user.application.mappers.UserDtoMapper;
import com.glow.user.application.model.UserDto;
import com.glow.user.domain.model.*;
import com.glow.user.domain.repository.UserRepository;
import com.glow.user.domain.shared.DomainException;
import com.glow.user.domain.shared.DomainPrecondition;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.NotFoundException;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class UserService {

    private static final List<Permission> PROFILE_PERMISSIONS = List.of(
        Permission.CUSTOMER,
        Permission.COURIER,
        Permission.RESTAURANT_USER);

    private final UserRepository userRepository;
    private final UserDtoMapper mapper;

    public UserService(UserRepository userRepository, UserDtoMapper mapper) {
        this.userRepository = userRepository;
        this.mapper = mapper;
    }

    public void ensureUserExists(String keycloakSubject, String email) {
        ensureUserExists(keycloakSubject, email, List.of());
    }

    public void ensureUserExists(String keycloakSubject, String email, List<Permission> permissions) {
        if (email == null || email.isBlank()) {
            return;
        }

        var keycloakId = resolveKeycloakId(keycloakSubject);
        var existingUser = userRepository.findByKeycloakId(keycloakId.toString());
        if (existingUser.isPresent()) {
            return;
        }

        if (userRepository.existsByEmail(email)) {
            return;
        }

        var user = User.builder()
            .keycloakId(keycloakId)
            .name(email)
            .email(email)
            .phoneNumber(null)
            .permissions(permissions == null ? List.of() : permissions)
            .build();

        userRepository.save(user);
    }

    public UserDto syncUserProfile(String keycloakSubject, String email, ProfileSyncRequest request) {
        if (email == null || email.isBlank()) {
            throw new DomainException("User email claim is required");
        }

        var keycloakId = resolveKeycloakId(keycloakSubject);
        var existingUser = userRepository.findByKeycloakId(keycloakId.toString());
        if (existingUser.isPresent()) {
            return mapper.toDto(existingUser.get());
        }

        var createUserRequest = new CreateUserRequest(
            request.name(),
            request.phoneNumber(),
            email,
            request.permissions(),
            request.savedAddresses(),
            request.vehicleType(),
            request.courierAvailability(),
            request.restaurantId());

        return createUser(createUserRequest, keycloakSubject);
    }

    public UserDto createUser(CreateUserRequest request, String keycloakSubject) {
        var permissions = request.permissions() == null ? List.<Permission>of() : request.permissions();
        validatePermissionCombination(permissions);
        var keycloakId = resolveKeycloakId(keycloakSubject);

        if (userRepository.existsByKeycloakId(keycloakId.toString())) {
            throw new DomainException("User with keycloakId " + keycloakId + " already exists");
        }

        if (userRepository.existsByEmail(request.email())) {
            throw new DomainException("User with email " + request.email() + " already exists");
        }

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
        return DomainPrecondition.requireValidUuid(
            rawKeycloakId,
            "User keycloakId is required and cannot be blank",
            "User keycloakId must be a valid UUID");
    }

    private void validatePermissionCombination(List<Permission> permissions) {
        long profilePermissions = permissions.stream()
            .filter(PROFILE_PERMISSIONS::contains)
            .count();

        if (profilePermissions > 1) {
            throw new DomainException(
                "User can only have one profile permission among CUSTOMER, COURIER, and RESTAURANT_USER");
        }
    }

    private User.Builder resolveBuilder(CreateUserRequest request, List<Permission> permissions) {
        if (permissions.contains(Permission.CUSTOMER)) {
            return CustomerUser.builder().savedAddresses(
                request.savedAddresses() == null ? List.of() : request.savedAddresses());
        }

        if (permissions.contains(Permission.COURIER)) {
            return CourierUser.builder()
                .vehicleType(request.vehicleType())
                .courierAvailability(request.courierAvailability());
        }

        if (permissions.contains(Permission.RESTAURANT_USER) &&
            request.restaurantId() != null && !request.restaurantId().isBlank()) {
            return RestaurantUser.builder().restaurantId(UUID.fromString(request.restaurantId()));
        }

        return User.builder();
    }

    private User.Builder resolveBuilderForUpdate(UpdateUserRequest request, User existing,
                                                 List<Permission> permissions) {
        if (permissions.contains(Permission.CUSTOMER)) {
            List<Address> addresses = request.savedAddresses() != null
                ? request.savedAddresses()
                : (existing instanceof CustomerUser customerUser ? customerUser.getSavedAddresses() : List.of());

            return CustomerUser.builder().savedAddresses(addresses);
        }

        if (permissions.contains(Permission.COURIER)) {
            VehicleType vehicleType = request.vehicleType() != null
                ? request.vehicleType()
                : (existing instanceof CourierUser courierUser ? courierUser.getVehicleType() : null);

            CourierAvailability courierAvailability = request.courierAvailability() != null
                ? request.courierAvailability()
                : (existing instanceof CourierUser courierUser ? courierUser.getCourierAvailability() : null);

            return CourierUser.builder()
                .vehicleType(vehicleType)
                .courierAvailability(courierAvailability)
                .courierTransferId(existing instanceof CourierUser courierUser
                    ? courierUser.getCourierTransferId() : null)
                .stripeAccountId(existing instanceof CourierUser courierUser
                    ? courierUser.getStripeAccountId() : null)
                .stripeOnboardingComplete(existing instanceof CourierUser courierUser
                    ? courierUser.getStripeOnboardingComplete() : null);
        }

        if (permissions.contains(Permission.RESTAURANT_USER)) {
            UUID restaurantId = null;

            if (request.restaurantId() != null && !request.restaurantId().isBlank()) {
                restaurantId = UUID.fromString(request.restaurantId());
            } else if (existing instanceof RestaurantUser restaurantUser) {
                restaurantId = restaurantUser.getRestaurantId();
            }

            return RestaurantUser.builder().restaurantId(restaurantId);
        }

        return User.builder();
    }

    public UserDto findByKeycloakId(String keycloakSubject) {
        var keycloakId = resolveKeycloakId(keycloakSubject);
        return userRepository.findByKeycloakId(keycloakId.toString())
            .map(mapper::toDto)
            .orElseThrow(() -> new NotFoundException("No profile found for current user"));
    }

    public UserDto findById(String id) {
        return mapper.toDto(
            userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User with id " + id + " not found")));
    }

    public UserDto updateUser(String id, UpdateUserRequest request, String keycloakSubject, boolean isSysAdmin) {
        var existing = userRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("User with id " + id + " not found"));

        var callerKeycloakId = resolveKeycloakId(keycloakSubject);

        if (!isSysAdmin && !existing.getKeycloakId().equals(callerKeycloakId)) {
            throw new ForbiddenException("You are not allowed to update this user");
        }

        if (!isSysAdmin && request.permissions() != null) {
            throw new ForbiddenException("Only SYSADMIN can modify permissions");
        }

        var permissions = request.permissions() == null ? existing.getPermissions() : request.permissions();
        validatePermissionCombination(permissions);

        User.Builder builder = resolveBuilderForUpdate(request, existing, permissions);
        builder.id(existing.getId());
        builder.createdAt(existing.getCreatedAt());
        builder.updatedAt(Instant.now());
        builder.keycloakId(existing.getKeycloakId());
        builder.name(request.name() == null ? existing.getName() : request.name());
        builder.phoneNumber(request.phoneNumber() == null ? existing.getPhoneNumber() : request.phoneNumber());
        builder.email(request.email() == null ? existing.getEmail() : request.email());
        builder.permissions(permissions);

        var updated = builder.build();
        userRepository.update(updated);

        return mapper.toDto(updated);
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

    public void deleteUserById(String id, String keycloakSubject, boolean isSysAdmin) {
        var existing = userRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("User with id " + id + " not found"));

        var callerKeycloakId = resolveKeycloakId(keycloakSubject);

        if (!isSysAdmin && !existing.getKeycloakId().equals(callerKeycloakId)) {
            throw new ForbiddenException("You are not allowed to delete this user");
        }

        userRepository.deleteById(id);
    }
}