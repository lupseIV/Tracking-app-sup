package com.lupseiv.supplements;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Base class for controller integration tests: full application context,
 * Flyway-migrated in-memory H2 with the seeded catalog, MockMvc, and
 * per-test transaction rollback.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public abstract class BaseControllerIntegrationTest {

    @Autowired
    protected MockMvc mockMvc;
}
