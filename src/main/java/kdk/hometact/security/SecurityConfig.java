package kdk.hometact.security;

import kdk.hometact.security.jwt.JwtSecurityConfig;
import kdk.hometact.security.jwt.TokenProvider;
import kdk.hometact.security.jwt.exception.handler.JwtAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.firewall.DefaultHttpFirewall;
import org.springframework.security.web.firewall.HttpFirewall;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	private final TokenProvider tokenProvider;
	private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public HttpFirewall defaultHttpFirewall() {
		return new DefaultHttpFirewall();
	}

	@Override
	public void configure(WebSecurity web) {
		web
			.ignoring()
			.antMatchers(
				"/h2-console/**"
				, "/favicon.ico"
				, "/swagger-ui/**"
			)
			.and()
			.httpFirewall(defaultHttpFirewall());
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
			// token을 사용하는 방식이기 때문에 csrf를 disable
			.csrf().disable()

			.exceptionHandling()
			.authenticationEntryPoint(jwtAuthenticationEntryPoint)

			// enable h2-console
			.and()
			.headers()
			.frameOptions()
			.sameOrigin()

			// 세션을 사용하지 않기 때문에 STATELESS로 설정
			.and()
			.sessionManagement()
			.sessionCreationPolicy(SessionCreationPolicy.STATELESS)

			.and()
			.authorizeRequests()
			.antMatchers("/api/user/login"
				, "/api/user/signup"
				, "/api/token/refresh"
				, "/swagger-resources"
				, "/swagger-resources/**"
				, "/v3/api-docs"
			).permitAll()
			.antMatchers(HttpMethod.GET, "/api/post/get/**").permitAll()
			.antMatchers(HttpMethod.GET, "/api/postcategory").permitAll()

			.anyRequest().authenticated()

			.and()
			.apply(new JwtSecurityConfig(tokenProvider));
	}
}