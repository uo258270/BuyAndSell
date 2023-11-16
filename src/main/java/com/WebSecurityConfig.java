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
import org.thymeleaf.extras.springsecurity4.dialect.SpringSecurityDialect;



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
					.requestMatchers(mvc.pattern("/css/**"), mvc.pattern("/img/**"), mvc.pattern("/script/**"), mvc.pattern("/"), mvc.pattern("/signup"), mvc.pattern("/login/**")).permitAll()
					.requestMatchers(mvc.pattern("/user/list")).hasAuthority("ROLE_ADMIN")
					.requestMatchers(mvc.pattern("/message/**")).hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")
					.requestMatchers(mvc.pattern("/conversation/**")).hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")
					.requestMatchers(mvc.pattern("/offer/**")).hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")
					.anyRequest().authenticated();
			})
			
			.formLogin(form -> {
				form
					.loginPage("/login").permitAll()
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
	
	@Bean
	public SpringSecurityDialect securityDialect() {
		return new SpringSecurityDialect();
	}
}