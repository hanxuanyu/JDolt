package com.hxuanyu.jdolt.config;

import com.hxuanyu.jdolt.core.api.DoltClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * Jdolt配置类
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
