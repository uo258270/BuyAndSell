package com;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;



@Configuration
@EnableWebSecurity
public class WebSecurityConfig {
	
	@Bean
	public MvcRequestMatcher.Builder mvc(HandlerMappingIntrospector introspector) {
		return new MvcRequestMatcher.Builder(introspector);
	}
	
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http, MvcRequestMatcher.Builder mvc) throws Exception {
		http
			.csrf(csrf -> {
				csrf.disable();
			})
			
			.authorizeHttpRequests(request -> {
				request
					.requestMatchers(mvc.pattern("/home")).permitAll()
					.requestMatchers(mvc.pattern("/signup")).permitAll()
					.requestMatchers(mvc.pattern("/error")).permitAll()
					.requestMatchers(mvc.pattern("/css/**"), mvc.pattern("/img/**"), mvc.pattern("/script/**"), mvc.pattern("/"), mvc.pattern("/signup"), mvc.pattern("/login/**")).permitAll()
					.requestMatchers(mvc.pattern("/user/list")).hasAuthority("ROLE_ADMIN")
					.requestMatchers(mvc.pattern("/product/**")).hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")
					.requestMatchers(mvc.pattern("/featured/**")).hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")
					.requestMatchers(mvc.pattern("/reviews/**")).hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")

					.anyRequest().authenticated();
			})
			
			.formLogin(form -> {
			    form
			        .loginPage("/login")
			        .permitAll()
			        .defaultSuccessUrl("/home");
			})

			
			
			.logout(logout -> {
				logout.permitAll();
			});
		
		return http.build();
	}
	
	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration conf) throws Exception {
		return conf.getAuthenticationManager();
	}
	
}