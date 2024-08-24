package mate.academy.car_sharing_app.service;

import mate.academy.car_sharing_app.dto.UpdateUserRequestDto;
import mate.academy.car_sharing_app.dto.UpdateUserRoleRequestDto;
import mate.academy.car_sharing_app.dto.UserDto;
import mate.academy.car_sharing_app.dto.UserUpdateResponseDto;
import mate.academy.car_sharing_app.dto.RegisterUserRequestDto;
import mate.academy.car_sharing_app.mapper.UserMapper;
import mate.academy.car_sharing_app.model.Role;
import mate.academy.car_sharing_app.model.User;
import mate.academy.car_sharing_app.repository.RoleRepository;
import mate.academy.car_sharing_app.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    private static final Long USER_ID = 1L;
    private static final String EMAIL = "dirk@example.com";
    private static final String UPDATED_FIRST_NAME = "Piotr";
    private static final String FIRST_NAME = "Dirk";
    private static final String UPDATED_LAST_NAME = "Krzysztof";
    private static final String LAST_NAME = "Nowitzki";
    private static final String UPDATED_PASSWORD = "321321321";
    private static final String PASSWORD = "123123123";
    private static final Long ROLE_ID = 1L;
    private static final Long ROLE_ID_UPDATE = 2L;
    private static final Role.RoleName ROLE = Role.RoleName.CUSTOMER;
    private static final Role.RoleName ROLE_UPDATE = Role.RoleName.MANAGER;

    @InjectMocks
    private  UserServiceImpl userService;

    @Mock
    private  UserRepository userRepository;

    @Mock
    private  UserMapper userMapper;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("Verify getProfileInfo() method works")
    public void getProfileInfo_ValidUsername_ReturnsUserDto() {
        // Given
        Role role = createRole(ROLE_ID, ROLE);
        User user = createUser(USER_ID, EMAIL, FIRST_NAME,
                LAST_NAME, passwordEncoder.encode(PASSWORD), role);
        UserDto userDto = createUserDto(USER_ID, EMAIL, FIRST_NAME, LAST_NAME, ROLE);

        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));
        when(userMapper.toDto(user)).thenReturn(userDto);

        // When
        UserDto profileInfo = userService.getProfileInfo(EMAIL);

        // Then
        assertThat(profileInfo).isEqualTo(userDto);
        verify(userRepository).findByEmail(EMAIL);
        verify(userMapper).toDto(user);
        verifyNoMoreInteractions(userRepository, userMapper);
    }

    @Test
    @DisplayName("Verify updateRole() method works")
    public void updateRole_ValidRequestDto_ReturnsUserDto() {
        // Given
        UpdateUserRoleRequestDto updateUserRoleRequestDto
                = createUpdateUserRoleRequestDto(ROLE_UPDATE);

        Role oldRole = createRole(ROLE_ID, ROLE);
        Role newRole = createRole(ROLE_ID_UPDATE, ROLE_UPDATE);

        User user = createUser(USER_ID, EMAIL, FIRST_NAME,
                LAST_NAME, passwordEncoder.encode(PASSWORD), oldRole);
        UserDto updatedUserDto = createUserDto(USER_ID, EMAIL,
                FIRST_NAME, LAST_NAME, ROLE_UPDATE);

        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(roleRepository.findByRoleName(ROLE_UPDATE)).thenReturn(Optional.of(newRole));
        when(userMapper.toDto(user)).thenReturn(updatedUserDto);

        // When
        UserDto result = userService.updateRole(USER_ID, updateUserRoleRequestDto);

        // Then
        assertThat(result).isEqualTo(updatedUserDto);
        verify(userRepository).findById(USER_ID);
        verify(roleRepository).findByRoleName(ROLE_UPDATE);
        verify(userRepository).save(user);
        verify(userMapper).toDto(user);
        verifyNoMoreInteractions(userRepository, roleRepository, userMapper);
    }

    @Test
    @DisplayName("Verify updateProfile() method works")
    public void updateProfile_ValidRequestDto_ReturnsUserUpdateResponseDto() {
        // Given
        UpdateUserRequestDto updateUserRequestDto
                = createUpdateUserRequestDto(UPDATED_FIRST_NAME,
                UPDATED_LAST_NAME, UPDATED_PASSWORD);

        Role role = createRole(ROLE_ID, ROLE);

        User existingUser = createUser(USER_ID, EMAIL, FIRST_NAME,
                LAST_NAME, passwordEncoder.encode(PASSWORD), role);
        User updatedUser = createUser(USER_ID, EMAIL, UPDATED_FIRST_NAME,
                UPDATED_LAST_NAME, passwordEncoder.encode(UPDATED_PASSWORD), role);
        UserUpdateResponseDto userUpdateResponseDto
                = createUserUpdateResponseDto(UPDATED_FIRST_NAME, UPDATED_LAST_NAME);

        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(updatedUser)).thenReturn(updatedUser);
        when(userMapper.toUserUpdateResponseDto(updatedUser)).thenReturn(userUpdateResponseDto);

        // When
        UserUpdateResponseDto result = userService.updateProfile(EMAIL, updateUserRequestDto);

        // Then
        assertThat(result).isEqualTo(userUpdateResponseDto);
        verify(userRepository).findByEmail(EMAIL);
        verify(userRepository).save(updatedUser);
        verify(userMapper).toUserUpdateResponseDto(updatedUser);
        verifyNoMoreInteractions(userRepository, userMapper);
    }

    @Test
    @DisplayName("Verify register() method works correctly")
    public void register_ValidRequestDto_ReturnsUserDto() {
        // Given
        RegisterUserRequestDto registerUserRequestDto
                = createRegisterUserRequestDto(EMAIL, FIRST_NAME, LAST_NAME, PASSWORD);

        Role role = createRole(ROLE_ID, ROLE);
        User user = createUser(USER_ID, registerUserRequestDto.email(),
                registerUserRequestDto.firstName(), registerUserRequestDto.lastName(),
                passwordEncoder.encode(registerUserRequestDto.password()), role);
        UserDto userDto = createUserDto(USER_ID, user.getEmail(),
                user.getFirstName(), user.getLastName(),
                user.getRole().getRoleName());

        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.empty());
        when(roleRepository.findByRoleName(ROLE)).thenReturn(Optional.of(role));
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(userDto);

        // When
        UserDto registeredUser = userService.register(registerUserRequestDto);

        // Then
        assertThat(registeredUser).isEqualTo(userDto);
        verify(userRepository).save(any(User.class));
        verify(userRepository).findByEmail(EMAIL);
        verify(roleRepository).findByRoleName(ROLE);
        verify(userMapper).toDto(user);
        verifyNoMoreInteractions(userRepository, userMapper, roleRepository);
    }

    private User createUser(Long id, String email,
                            String firstName, String lastName, String password, Role role) {
        User user = new User();
        user.setId(id);
        user.setEmail(email);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setPassword(password);
        user.setRole(role);
        return user;
    }

    private Role createRole(Long id, Role.RoleName roleName) {
        Role role = new Role();
        role.setId(id);
        role.setRoleName(roleName);
        return role;
    }

    private UserDto createUserDto(Long id, String email, String firstName,
                                  String lastName, Role.RoleName roleName) {
        return new UserDto(id, email, firstName, lastName, roleName);
    }

    private UpdateUserRequestDto createUpdateUserRequestDto(String firstName,
                                                            String lastName,
                                                            String password) {
        return new UpdateUserRequestDto(firstName, lastName, password);
    }

    private UpdateUserRoleRequestDto createUpdateUserRoleRequestDto(Role.RoleName roleName) {
        return new UpdateUserRoleRequestDto(roleName);
    }

    private RegisterUserRequestDto createRegisterUserRequestDto(String email,
                                                                String firstName,
                                                                String lastName,
                                                                String password) {
        return new RegisterUserRequestDto(email, firstName, lastName, password);
    }

    private UserUpdateResponseDto createUserUpdateResponseDto(String firstName,
                                                              String lastName) {
        return new UserUpdateResponseDto(firstName, lastName);
    }
}
