package mate.academy.car_sharing_app.mapper;

import mate.academy.car_sharing_app.config.MapperConfig;
import mate.academy.car_sharing_app.dto.user.RegisterUserRequestDto;
import mate.academy.car_sharing_app.dto.user.UpdateUserRequestDto;
import mate.academy.car_sharing_app.dto.user.UserDto;
import mate.academy.car_sharing_app.dto.user.UserUpdateResponseDto;
import mate.academy.car_sharing_app.model.Role;
import mate.academy.car_sharing_app.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(config = MapperConfig.class)
public interface UserMapper {
    @Mapping(target = "roles", expression = "java(mapRoles(user.getRoles()))")
    UserDto toDto(User user);

    UserUpdateResponseDto toUserUpdateResponseDto(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "password", ignore = true)
    User toEntity(RegisterUserRequestDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "email", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "password", ignore = true)
    User updateFromDto(UpdateUserRequestDto dto, @MappingTarget User entity);

    default Set<String> mapRoles(Set<Role> roles) {
        return roles.stream()
                .map(role -> role.getRoleName().name())
                .collect(Collectors.toSet());
    }
}
