package mate.academy.car_sharing_app.mapper;

import mate.academy.car_sharing_app.config.MapperConfig;
import mate.academy.car_sharing_app.dto.UserDto;
import mate.academy.car_sharing_app.dto.UserUpdateResponseDto;
import mate.academy.car_sharing_app.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class)
public interface UserMapper {

    @Mapping(source = "role.roleName", target = "role")
    UserDto toDto(User user);

    UserUpdateResponseDto toUserUpdateResponseDto(User user);

}
