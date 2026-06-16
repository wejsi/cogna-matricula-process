package com.cogna.core.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = com.cogna.core.config.AppConfig.class)
class AppConfigTest {

    @Autowired
    RestTemplate restTemplate;

    @Test
    void restTemplateBeanExists() {
        assertNotNull(restTemplate);
    }
}
