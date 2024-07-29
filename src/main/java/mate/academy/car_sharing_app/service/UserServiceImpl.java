package mate.academy.car_sharing_app.service;

import lombok.RequiredArgsConstructor;
import mate.academy.car_sharing_app.dto.RegisterUserRequestDto;
import mate.academy.car_sharing_app.dto.UserDto;
import mate.academy.car_sharing_app.dto.UserUpdateResponseDto;
import mate.academy.car_sharing_app.dto.UpdateUserRequestDto;
import mate.academy.car_sharing_app.dto.UpdateUserRoleRequestDto;
import mate.academy.car_sharing_app.exceptions.RegistrationException;
import mate.academy.car_sharing_app.exceptions.RoleNotFoundException;
import mate.academy.car_sharing_app.exceptions.UsernameNotFoundException;
import mate.academy.car_sharing_app.mapper.UserMapper;
import mate.academy.car_sharing_app.model.Role;
import mate.academy.car_sharing_app.model.User;
import mate.academy.car_sharing_app.repository.RoleRepository;
import mate.academy.car_sharing_app.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {
    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDto register(RegisterUserRequestDto registerUserRequestDto) {
        if (userRepository.findByEmail(registerUserRequestDto.email()).isPresent()) {
            throw new RegistrationException("Can't register user");
        }

        Role defaultRole = roleRepository.findByRoleName(Role.RoleName.CUSTOMER).orElseThrow(
                () -> new RuntimeException("Default role CUSTOMER not found"));

        User user = new User();
        user.setEmail(registerUserRequestDto.email());
        user.setFirstName(registerUserRequestDto.firstName());
        user.setLastName(registerUserRequestDto.lastName());
        user.setPassword(passwordEncoder.encode(registerUserRequestDto.password()));
        user.setRole(defaultRole);

        User savedUser = userRepository.save(user);
        return userMapper.toDto(savedUser);
    }

    @Override
    public UserDto getProfileInfo(String username) {
        User user = userRepository.findByEmail(username).orElseThrow(
                () -> new UsernameNotFoundException("User not found by username: " + username));

        return userMapper.toDto(user);
    }

    @Override
    public UserUpdateResponseDto updateProfile(String username,
                                               UpdateUserRequestDto updateUserRequestDto) {
        User existingUser = userRepository.findByEmail(username).orElseThrow(
                () -> new UsernameNotFoundException("User not found by username: " + username));

        User user = new User();
        user.setId(existingUser.getId());
        user.setEmail(existingUser.getEmail());
        user.setFirstName(updateUserRequestDto.firstName());
        user.setLastName(updateUserRequestDto.lastName());
        user.setPassword(passwordEncoder.encode(updateUserRequestDto.password()));
        user.setRole(existingUser.getRole());

        User updatedUser = userRepository.save(user);
        return userMapper.toUserUpdateResponseDto(updatedUser);
    }

    @Override
    public UserDto updateRole(Long id, UpdateUserRoleRequestDto updateUserRoleRequestDto) {
        User existingUser = userRepository.findById(id).orElseThrow(
                () -> new UsernameNotFoundException("User not found by id: " + id));

       Role newRole = roleRepository.findByRoleName(updateUserRoleRequestDto.role()).orElseThrow(
               () -> new RoleNotFoundException("Role not found: "
                       + updateUserRoleRequestDto.role()));

       existingUser.setRole(newRole);
       userRepository.save(existingUser);
       return userMapper.toDto(existingUser);
    }
}
