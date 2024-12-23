package com.hxuanyu.jdolt.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DoltConfigTest {

    @Test
    void testGetJdbcUrlWithValidUrl() {
        // Arrange
        DoltConfig doltConfig = DoltConfig.newBuilder()
                .jdbcUrl("jdbc:dolt://localhost:3306/mydb")
                .username("user")
                .password("pass")
                .build();

        // Act & Assert
        assertEquals("jdbc:dolt://localhost:3306/mydb", doltConfig.getJdbcUrl());
    }

    @Test
    void testGetJdbcUrlWithEmptyUrl() {
        // Arrange & Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            DoltConfig.newBuilder()
                    .jdbcUrl("")
                    .username("user")
                    .password("pass")
                    .build();
        });
    }

    @Test
    void testGetJdbcUrlWithNullUrl() {
        // Arrange & Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            DoltConfig.newBuilder()
                    .jdbcUrl(null)
                    .username("user")
                    .password("pass")
                    .build();
        });
    }
}