package com.hxuanyu.jdolt.config;

import com.hxuanyu.jdolt.api.DoltClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * TODO
 *
 * @author hanxuanyu
 * @version 1.0
 */
@Configuration
public class JDoltConfig {

    @Bean
    public DoltClient doltClient(DataSource dataSource){
        return DoltClient.initialize(dataSource);
    }

}
