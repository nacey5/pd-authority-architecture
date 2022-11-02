package com.hzh.pinda.auth.client.configuration;


import com.hzh.pinda.auth.client.properties.AuthClientProperties;
import com.hzh.pinda.auth.client.utils.JwtTokenClientUtils;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * 客户端认证配置
 *
 */
@EnableConfigurationProperties(value = {
        AuthClientProperties.class,
})
public class AuthClientConfiguration {
    @Bean
    public JwtTokenClientUtils getJwtTokenClientUtils(AuthClientProperties authClientProperties) {
        return new JwtTokenClientUtils(authClientProperties);
    }

}
