package mate.academy.car_sharing_app.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import mate.academy.car_sharing_app.dto.UpdateUserRequestDto;
import mate.academy.car_sharing_app.dto.UpdateUserRoleRequestDto;
import mate.academy.car_sharing_app.dto.UserDto;
import mate.academy.car_sharing_app.model.Role;
import mate.academy.car_sharing_app.model.User;
import mate.academy.car_sharing_app.repository.UserRepository;
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
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserControllerTest {
    private static final Long USER_ID = 1L;
    private static final String USERNAME = "dirk@example.com";
    private static final String FIRST_NAME = "UpdatedFirstName";
    private static final String LAST_NAME = "UpdatedLastName";
    private static final String PASSWORD = "newpassword123";

    protected static MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

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
                    new ClassPathResource("database/roles/" +
                            "add-2-roles-to-roles_table.sql")
            );
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/users/add-1-user-to-users_table.sql")
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
                    new ClassPathResource("database/roles/" +
                            "delete-roles-from-roles_table.sql")
            );
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/users/" +
                            "delete-users-from-users_table.sql")
            );
        }
    }

    @Test
    @WithMockUser(username = "dirk@example.com")
    @DisplayName("Verify getProfileInfo() method works")
    void getProfileInfo_AuthenticatedUser_ReturnsUserProfile() throws Exception {
        // When
        MvcResult result = mockMvc.perform(
                        get("/api/users/me")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        // Then
        User user = userRepository.findByEmail(USERNAME).orElseThrow();
        UserDto actual = objectMapper
                .readValue(result.getResponse().getContentAsString(), UserDto.class);

        Assertions.assertEquals(user.getEmail(), actual.email());
        Assertions.assertEquals(user.getFirstName(), actual.firstName());
        Assertions.assertEquals(user.getLastName(), actual.lastName());
    }

    @Test
    @WithMockUser(username = "dirk@example.com")
    @DisplayName("Verify updateProfile() method works")
    void updateProfile_ValidRequestDto_UpdatesUserProfile() throws Exception {
        // Given
        UpdateUserRequestDto updateUserRequestDto = new UpdateUserRequestDto(
                FIRST_NAME, LAST_NAME, PASSWORD
        );

        String jsonRequest = objectMapper.writeValueAsString(updateUserRequestDto);

        // When
        MvcResult result = mockMvc.perform(
                        put("/api/users/me")
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        // Then
        User updatedUser = userRepository.findByEmail(USERNAME).orElseThrow();
        Assertions.assertEquals(FIRST_NAME, updatedUser.getFirstName());
        Assertions.assertEquals(LAST_NAME, updatedUser.getLastName());
    }

    @Test
    @WithMockUser(username = "dirk@example.com", authorities = {"ROLE_MANAGER"})
    @DisplayName("Verify updateRole() method works")
    void updateRole_ValidRequestDto_UpdatesUserRole() throws Exception {
        // Given
        UpdateUserRoleRequestDto updateUserRoleRequestDto
                = new UpdateUserRoleRequestDto(Role.RoleName.MANAGER);

        String jsonRequest = objectMapper.writeValueAsString(updateUserRoleRequestDto);

        // When
        MvcResult result = mockMvc.perform(
                        put("/api/users/{id}/role", USER_ID)
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        // Then
        User updatedUser = userRepository.findById(USER_ID).orElseThrow();
        Assertions.assertEquals(Role.RoleName.MANAGER, updatedUser.getRole().getRoleName());
    }
}
