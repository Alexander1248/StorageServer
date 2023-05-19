package ru.alexander.stosys.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final BCryptPasswordEncoder encoder;
    private final MessageDigest hash;


    public SecurityConfig() throws NoSuchAlgorithmException {
        encoder = new BCryptPasswordEncoder();
        hash = MessageDigest.getInstance("SHA-256");
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return encoder;
    }

    @Bean
    public MessageDigest hashEncoder() {
        return hash;
    }


    
    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http.cors().and().csrf().disable()
                .requiresChannel(channel -> channel.anyRequest().requiresSecure())
                .authorizeHttpRequests(requests -> requests.anyRequest().permitAll())
                .build();
    }
}
