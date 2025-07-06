package org.ebndrnk.userservice;

import org.ebndrnk.userservice.config.TestContainersConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class UserServiceApplicationTests extends TestContainersConfig {

    @Test
    void contextLoads() {
    }

}
