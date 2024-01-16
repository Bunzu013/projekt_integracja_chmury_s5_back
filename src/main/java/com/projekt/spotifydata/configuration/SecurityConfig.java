package com.projekt.spotifydata.configuration;

import com.projekt.spotifydata.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

  @Autowired
  private UserService userService;

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    // Enable CORS and disable CSRF
    http = http.cors().and().csrf().disable();

    // Set session management to stateless
    http = http
            .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and();

    // Set unauthorized requests exception handler
    http = http
            .exceptionHandling()
            .authenticationEntryPoint(
                    (request, response, ex) -> {
                      response.sendError(
                              HttpServletResponse.SC_UNAUTHORIZED,
                              ex.getMessage()
                      );
                    }
            )
            .and();

    // Endpoint authorization
    http.authorizeRequests()
            .anyRequest().permitAll()
            .and()
            .csrf().disable();

    // Add JWT token filter
    http.addFilterBefore(
            jwtAuthenticationFilter(),
            UsernamePasswordAuthenticationFilter.class
    );
  }

  @Override
  protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth.userDetailsService(userService).passwordEncoder(passwordEncoder());
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  private JwtAuthenticationFilter jwtAuthenticationFilter() throws Exception {
    return new JwtAuthenticationFilter(authenticationManager());
  }
}
