package com.bezkoder.spring.security;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.csrf(csrf -> csrf.disable());

		http.headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()));

		http.authorizeHttpRequests(auth -> auth
			.requestMatchers(PathRequest.toH2Console()).permitAll()
			.requestMatchers("/api/admin/**").hasRole("ADMIN")
			.requestMatchers(HttpMethod.POST, "/api/orders/**").hasAnyRole("USER", "ADMIN")
			.requestMatchers(HttpMethod.GET, "/api/users/**").hasAnyRole("USER", "ADMIN")
			.requestMatchers(HttpMethod.GET, "/api/products/**").hasAnyRole("USER", "ADMIN")
			.anyRequest().authenticated()
		);

		http.httpBasic(Customizer.withDefaults());

		return http.build();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
	}

	@Bean
	public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
		UserDetails user = User.withUsername("user")
			.password(passwordEncoder.encode("password"))
			.roles("USER")
			.build();

		UserDetails admin = User.withUsername("admin")
			.password(passwordEncoder.encode("password"))
			.roles("ADMIN")
			.build();

		return new InMemoryUserDetailsManager(user, admin);
	}
}

