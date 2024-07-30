package mate.academy.car_sharing_app.mapper;

import mate.academy.car_sharing_app.config.MapperConfig;
import mate.academy.car_sharing_app.dto.RentalDto;
import mate.academy.car_sharing_app.model.Rental;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class)
public interface RentalMapper {
    @Mapping(target = "isActive", expression = "java(rental.getActualReturnDate()" +
            " == null && rental.getReturnDate().isAfter(LocalDate.now()))")
    RentalDto toDto(Rental rental);
}
