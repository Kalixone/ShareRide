package mate.academy.car_sharing_app.mapper;

import mate.academy.car_sharing_app.config.MapperConfig;
import mate.academy.car_sharing_app.dto.CarDto;
import mate.academy.car_sharing_app.dto.CreateCarRequestDto;
import mate.academy.car_sharing_app.dto.UpdateCarRequestDto;
import mate.academy.car_sharing_app.model.Car;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class)
public interface CarMapper {
    Car toModel(CreateCarRequestDto createCarRequestDto);

    Car updateModel(UpdateCarRequestDto updateCarRequestDto, @MappingTarget Car car);

    CarDto toDto(Car car);
}
