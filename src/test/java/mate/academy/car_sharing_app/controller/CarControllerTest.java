package mate.academy.car_sharing_app.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import mate.academy.car_sharing_app.dto.CarDto;
import mate.academy.car_sharing_app.dto.CreateCarRequestDto;
import mate.academy.car_sharing_app.dto.UpdateCarRequestDto;
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
    private static final Long DELETE_CAR_ID = 3L;
    private static final String MODEL = "Model S";
    private static final String BRAND = "Tesla";
    private static final Car.TypeCar TYPE = Car.TypeCar.SEDAN;
    private static final int INVENTORY = 10;
    private static final BigDecimal DAILY_FEE = BigDecimal.valueOf(100.00);
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
    @WithMockUser(username = "user", authorities = {"ROLE_MANAGER"})
    @DisplayName("Create a new car record")
    void createCar_ValidRequestDto_CreatesNewCar() throws Exception {
        // Given
        CreateCarRequestDto carRequestDto
                = createCarRequestDto(MODEL, BRAND, TYPE, INVENTORY, DAILY_FEE);
        String jsonRequest = objectMapper.writeValueAsString(carRequestDto);

        // When
        MvcResult result = mockMvc.perform(
                        post("/api/cars")
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        // Then
        CarDto actualCar = objectMapper
                .readValue(result.getResponse().getContentAsString(), CarDto.class);
        Assertions.assertNotNull(actualCar.id());
        Assertions.assertEquals(MODEL, actualCar.model());
        Assertions.assertEquals(BRAND, actualCar.brand());
        Assertions.assertEquals(TYPE, actualCar.type());
        Assertions.assertEquals(DAILY_FEE, actualCar.dailyFee());
    }

    @Test
    @WithMockUser(username = "user", authorities = {"MANAGER"})
    @DisplayName("Retrieve car details by ID")
    void getCarById_ValidId_ReturnsCarDto() throws Exception {
        // Given
        CarDto carDto
                = createCarDto(CAR_ID, MODEL, BRAND, TYPE, DAILY_FEE);
        CarDto expected
                = createCarDto(carDto.id(), carDto.model(),
                carDto.model(), carDto.type(), carDto.dailyFee());

        // When
        MvcResult result = mockMvc.perform(
                        get("/api/cars/{id}", CAR_ID)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        // Then
        CarDto actual = objectMapper
                .readValue(result.getResponse().getContentAsString(), CarDto.class);
        EqualsBuilder.reflectionEquals(expected, actual, "id");
    }

    @Test
    @WithMockUser(username = "user", authorities = {"USER"})
    @DisplayName("Get a list of all cars")
    void getAll_ValidRequest_ReturnsAllCars() throws Exception {
        // Given
        CarDto carDto1 = createCarDto(1L, "Model S",
                "Tesla", Car.TypeCar.SEDAN, BigDecimal.valueOf(100.00));
        CarDto carDto2 = createCarDto(2L, "Civic",
                "Honda", Car.TypeCar.SEDAN, BigDecimal.valueOf(75.50));
        List<CarDto> expected = List.of(carDto1, carDto2);

        // When
        MvcResult result = mockMvc.perform(
                        get("/api/cars")
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        // Then
        CarDto[] actual = objectMapper
                .readValue(result.getResponse().getContentAsString(), CarDto[].class);
        Assertions.assertEquals(expected.size(), actual.length);
    }

    @Test
    @WithMockUser(username = "user", authorities = {"ROLE_MANAGER"})
    @DisplayName("Delete car record by ID")
    void deleteCarById_ValidId_DeletesCar() throws Exception {
        // Given
        carRepository.findById(DELETE_CAR_ID);

        // When
        mockMvc.perform(
                        delete("/api/cars/{id}", DELETE_CAR_ID)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        // Then
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
