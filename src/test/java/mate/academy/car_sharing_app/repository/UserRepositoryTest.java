package mate.academy.car_sharing_app.repository;

import mate.academy.car_sharing_app.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserRepositoryTest {
    private static final String EMAIL = "dirk@example.com";

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("Verify findByEmail() method works")
    @Sql(scripts = {
            "classpath:database/roles/delete-roles-from-roles_table.sql",
            "classpath:database/users/delete-users-from-users_table.sql",
            "classpath:database/roles/add-2-roles-to-roles_table.sql",
            "classpath:database/users/add-1-user-to-users_table.sql"
    },
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/roles/delete-roles-from-roles_table.sql",
            "classpath:database/users/delete-users-from-users_table.sql",
    },
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findByEmail_ShouldReturnUser_WhenEmailExists() {
        // When
        User user = userRepository.findByEmail(EMAIL).orElseThrow();

        // Then
        assertThat(user).isNotNull();
        assertThat(user.getEmail()).isEqualTo(EMAIL);
        assertThat(user.getFirstName()).isEqualTo("John");
        assertThat(user.getLastName()).isEqualTo("Doe");
        assertThat(user.getPassword()).isEqualTo("123123123");
    }
}
