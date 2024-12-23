package com.hxuanyu.jdolt.config;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Configuration class for Dolt database connection.
 * This class uses the Builder pattern to allow flexible and readable configuration.
 *
 * @author hanxuanyu
 * @version 1.0
 */
public class DoltConfig {

    private final String jdbcUrl;
    private final String username;
    private final String password;
    private final Map<String, String> properties;

    /**
     * Private constructor to enforce the use of the Builder.
     *
     * @param builder the Builder instance used to create this configuration
     */
    private DoltConfig(Builder builder) {
        this.jdbcUrl = builder.jdbcUrl;
        this.username = builder.username;
        this.password = builder.password;
        this.properties = Collections.unmodifiableMap(new HashMap<>(builder.properties));
    }

    public static Builder newBuilder() {
        return new Builder();
    }
    

    // Getters for the configuration fields
    public String getJdbcUrl() {
        return jdbcUrl;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }


    public Map<String, String> getProperties() {
        return properties;
    }

    /**
     * Builder class for DoltConfig.
     * Provides a fluent API for constructing a DoltConfig instance.
     */
    public static class Builder {
        private String jdbcUrl;
        private String username;
        private String password;
        private int connectionPoolSize = 10; // Default connection pool size
        private final Map<String, String> properties = new HashMap<>();

        /**
         * Sets the JDBC URL for the database connection.
         *
         * @param jdbcUrl the JDBC URL
         * @return the Builder instance
         */
        public Builder jdbcUrl(String jdbcUrl) {
            this.jdbcUrl = jdbcUrl;
            return this;
        }

        /**
         * Sets the username for the database connection.
         *
         * @param username the username
         * @return the Builder instance
         */
        public Builder username(String username) {
            this.username = username;
            return this;
        }

        /**
         * Sets the password for the database connection.
         *
         * @param password the password
         * @return the Builder instance
         */
        public Builder password(String password) {
            this.password = password;
            return this;
        }

        /**
         * Sets the connection pool size.
         *
         * @param connectionPoolSize the connection pool size
         * @return the Builder instance
         */
        public Builder connectionPoolSize(int connectionPoolSize) {
            if (connectionPoolSize <= 0) {
                throw new IllegalArgumentException("Connection pool size must be greater than 0");
            }
            this.connectionPoolSize = connectionPoolSize;
            return this;
        }

        /**
         * Adds a custom property to the configuration.
         *
         * @param key   the property key
         * @param value the property value
         * @return the Builder instance
         */
        public Builder addProperty(String key, String value) {
            if (key == null || key.isEmpty()) {
                throw new IllegalArgumentException("Property key cannot be null or empty");
            }
            if (value == null) {
                throw new IllegalArgumentException("Property value cannot be null");
            }
            this.properties.put(key, value);
            return this;
        }

        /**
         * Adds multiple properties to the configuration.
         *
         * @param properties a map of properties
         * @return the Builder instance
         */
        public Builder addProperties(Map<String, String> properties) {
            if (properties == null) {
                throw new IllegalArgumentException("Properties map cannot be null");
            }
            this.properties.putAll(properties);
            return this;
        }

        /**
         * Validates the configuration before building the DoltConfig instance.
         */
        private void validate() {
            if (jdbcUrl == null || jdbcUrl.isEmpty()) {
                throw new IllegalArgumentException("JDBC URL cannot be empty");
            }
            if (username == null || username.isEmpty()) {
                throw new IllegalArgumentException("Username cannot be empty");
            }
            if (password == null || password.isEmpty()) {
                throw new IllegalArgumentException("Password cannot be empty");
            }
        }

        /**
         * Builds and returns a DoltConfig instance.
         *
         * @return a new DoltConfig instance
         */
        public DoltConfig build() {
            validate();
            return new DoltConfig(this);
        }
    }

    @Override
    public String toString() {
        return "DoltConfig{" +
                "jdbcUrl='" + jdbcUrl + '\'' +
                ", username='" + username + '\'' +
                ", properties=" + properties +
                '}';
    }
}