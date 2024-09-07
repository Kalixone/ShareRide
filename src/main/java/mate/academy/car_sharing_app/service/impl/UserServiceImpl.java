package mate.academy.car_sharing_app.service.impl;

import lombok.RequiredArgsConstructor;
import mate.academy.car_sharing_app.dto.user.RegisterUserRequestDto;
import mate.academy.car_sharing_app.dto.user.UpdateUserRequestDto;
import mate.academy.car_sharing_app.dto.user.UserDto;
import mate.academy.car_sharing_app.dto.user.UserUpdateResponseDto;
import mate.academy.car_sharing_app.dto.user.UpdateUserRoleRequestDto;
import mate.academy.car_sharing_app.exceptions.EntityNotFoundException;
import mate.academy.car_sharing_app.exceptions.RegistrationException;
import mate.academy.car_sharing_app.mapper.UserMapper;
import mate.academy.car_sharing_app.model.Role;
import mate.academy.car_sharing_app.model.User;
import mate.academy.car_sharing_app.repository.RoleRepository;
import mate.academy.car_sharing_app.repository.UserRepository;
import mate.academy.car_sharing_app.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Set;
import java.util.HashSet;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {
    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDto register(RegisterUserRequestDto registerUserRequestDto) {
        if (userRepository.existsByEmail(registerUserRequestDto.email())) {
            throw new RegistrationException("Email already in use: "
                    + registerUserRequestDto.email());
        }

        Role defaultRole = roleRepository.findByRoleName(Role.RoleName.CUSTOMER).orElseThrow(
                () -> new EntityNotFoundException("Default role CUSTOMER not found"));

        User user = userMapper.toEntity(registerUserRequestDto);
        user.setPassword(passwordEncoder.encode(registerUserRequestDto.password()));
        user.setRoles(Set.of(defaultRole));

        User savedUser = userRepository.save(user);
        return userMapper.toDto(savedUser);
    }

    @Override
    public UserDto getProfileInfo(String username) {
        User user = userRepository.findByEmail(username).orElseThrow(
                () -> new EntityNotFoundException("User not found by username: " + username));

        return userMapper.toDto(user);
    }

    @Override
    public UserUpdateResponseDto updateProfile(String username,
                                               UpdateUserRequestDto updateUserRequestDto) {
        User existingUser = userRepository.findByEmail(username).orElseThrow(
                () -> new EntityNotFoundException("User not found by username: " + username));

        userMapper.updateFromDto(updateUserRequestDto, existingUser);

        if (updateUserRequestDto.password() != null && !updateUserRequestDto.password().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(updateUserRequestDto.password()));
        }

        User updatedUser = userRepository.save(existingUser);

        return userMapper.toUserUpdateResponseDto(updatedUser);
    }

    @Override
    public UserDto updateRole(Long id, UpdateUserRoleRequestDto updateUserRoleRequestDto) {
        User existingUser = userRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("User not found by id: " + id));

       Role newRole = roleRepository.findByRoleName(updateUserRoleRequestDto.role()).orElseThrow(
               () -> new EntityNotFoundException("Role not found: "
                       + updateUserRoleRequestDto.role()));

        Set<Role> roles = new HashSet<>();
        roles.add(newRole);
        existingUser.setRoles(roles);

       userRepository.save(existingUser);
       return userMapper.toDto(existingUser);
    }
}
