package mate.academy.car_sharing_app.repository;

import mate.academy.car_sharing_app.model.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class RoleRepositoryTest {
    private static final Role.RoleName ROLE_NAME = Role.RoleName.CUSTOMER;

    @Autowired
    private RoleRepository roleRepository;

    @Test
    @DisplayName("Verify findByRoleName() method works")
    @Sql(scripts = {
            "classpath:database/roles/delete-roles-from-roles_table.sql",
            "classpath:database/roles/add-2-roles-to-roles_table.sql"
    },
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/roles/delete-roles-from-roles_table.sql"
    },
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findByRoleName_ShouldReturnRole_WhenRoleNameExists() {
        Role role = roleRepository.findByRoleName(ROLE_NAME).orElseThrow();

        assertThat(role).isNotNull();
        assertThat(role.getRoleName()).isEqualTo(ROLE_NAME);
    }
}
