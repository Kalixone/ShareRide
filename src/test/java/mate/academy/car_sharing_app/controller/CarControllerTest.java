package mate.academy.car_sharing_app.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import mate.academy.car_sharing_app.dto.car.CarDto;
import mate.academy.car_sharing_app.dto.car.CreateCarRequestDto;
import mate.academy.car_sharing_app.dto.car.UpdateCarRequestDto;
import mate.academy.car_sharing_app.model.Car;
import mate.academy.car_sharing_app.repository.CarRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.shaded.org.apache.commons.lang3.builder.EqualsBuilder;
import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CarControllerTest {
    private static final Long CAR_ID = 1L;
    private static final Long SECOND_CAR_ID = 2L;
    private static final Long DELETE_CAR_ID = 3L;
    private static final String MODEL = "Model S";
    private static final String SECOND_MODEL = "Civic";
    private static final String BRAND = "Tesla";
    private static final String SECOND_BRAND = "Honda";
    private static final Car.TypeCar TYPE = Car.TypeCar.SEDAN;
    private static final Car.TypeCar SECOND_TYPE = Car.TypeCar.SEDAN;
    private static final int INVENTORY = 10;
    private static final BigDecimal DAILY_FEE = BigDecimal.valueOf(100.00);
    private static final BigDecimal SECOND_DAILY_FEE = BigDecimal.valueOf(75.50);
    protected static MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CarRepository carRepository;

    @BeforeAll
    static void beforeAll(
            @Autowired DataSource dataSource,
            @Autowired WebApplicationContext webApplicationContext) throws SQLException {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
        teardown(dataSource);
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/cars/add-3-cars-to-cars_table.sql")
            );
        }
    }

    @AfterAll
    static void afterAll(
            @Autowired DataSource dataSource
    ) {
        teardown(dataSource);
    }

    @SneakyThrows
    static void teardown(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/cars/" +
                            "delete-cars-from-cars_table.sql")
            );
        }
    }

    @Test
    @WithMockUser(username = "dirk@example.com", authorities = {"ROLE_MANAGER"})
    @DisplayName("Verify createCar() method works")
    void createCar_ValidRequestDto_CreatesNewCar() throws Exception {
        CreateCarRequestDto carRequestDto
                = createCarRequestDto(MODEL, BRAND, TYPE, INVENTORY, DAILY_FEE);
        String jsonRequest = objectMapper.writeValueAsString(carRequestDto);

        MvcResult result = mockMvc.perform(
                        post("/api/cars")
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        CarDto actualCar = objectMapper
                .readValue(result.getResponse().getContentAsString(), CarDto.class);
        Assertions.assertNotNull(actualCar.id());
        Assertions.assertEquals(MODEL, actualCar.model());
        Assertions.assertEquals(BRAND, actualCar.brand());
        Assertions.assertEquals(TYPE, actualCar.type());
        Assertions.assertEquals(DAILY_FEE, actualCar.dailyFee());
    }

    @Test
    @WithMockUser(username = "dirk@example.com")
    @DisplayName("Verify getCarById() method works")
    void getCarById_ValidId_ReturnsCarDto() throws Exception {
        CarDto carDto
                = createCarDto(CAR_ID, MODEL, BRAND, TYPE, DAILY_FEE);
        CarDto expected
                = createCarDto(carDto.id(), carDto.model(),
                carDto.model(), carDto.type(), carDto.dailyFee());

        MvcResult result = mockMvc.perform(
                        get("/api/cars/{id}", CAR_ID)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        CarDto actual = objectMapper
                .readValue(result.getResponse().getContentAsString(), CarDto.class);
        EqualsBuilder.reflectionEquals(expected, actual, "id");
    }

    @Test
    @WithMockUser(username = "dirk@example.com")
    @DisplayName("Verify getAll() method works")
    void getAll_ValidRequest_ReturnsAllCars() throws Exception {
        CarDto carDto1 = createCarDto(CAR_ID, MODEL,
                BRAND,TYPE, DAILY_FEE);
        CarDto carDto2 = createCarDto(SECOND_CAR_ID, SECOND_MODEL,
                SECOND_BRAND, SECOND_TYPE, SECOND_DAILY_FEE);
        List<CarDto> expected = List.of(carDto1, carDto2);

        MvcResult result = mockMvc.perform(
                        get("/api/cars")
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        CarDto[] actual = objectMapper
                .readValue(result.getResponse().getContentAsString(), CarDto[].class);
        Assertions.assertEquals(expected.size(), actual.length);
    }

    @Test
    @WithMockUser(username = "dirk@example.com'", authorities = {"ROLE_MANAGER"})
    @DisplayName("Verify deleteById() method works")
    void deleteCarById_ValidId_DeletesCar() throws Exception {
        carRepository.findById(DELETE_CAR_ID);

        mockMvc.perform(
                        delete("/api/cars/{id}", DELETE_CAR_ID)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        Optional<Car> deletedCar = carRepository.findById(DELETE_CAR_ID);
        Assertions.assertFalse(deletedCar.isPresent());
    }

    private CarDto createCarDto(Long id, String model, String brand,
                                Car.TypeCar type, BigDecimal dailyFee) {
        return new CarDto(id, model, brand, type, dailyFee);
    }

    private CreateCarRequestDto createCarRequestDto(String model, String brand,
                                                    Car.TypeCar type, int inventory,
                                                    BigDecimal dailyFee) {
        return new CreateCarRequestDto(model, brand, type, inventory, dailyFee);
    }

    private UpdateCarRequestDto createUpdateCarRequestDto(String model, String brand,
                                                          Car.TypeCar type, int inventory,
                                                          BigDecimal dailyFee) {
        return new UpdateCarRequestDto(model, brand, type, inventory, dailyFee);
    }

    private Car createCar(Long id, String model, String brand, Car.TypeCar type,
                          int inventory, BigDecimal dailyFee) {
        Car car = new Car();
        car.setId(id);
        car.setModel(model);
        car.setBrand(brand);
        car.setType(type);
        car.setInventory(inventory);
        car.setDailyFee(dailyFee);
        return car;
    }
}
