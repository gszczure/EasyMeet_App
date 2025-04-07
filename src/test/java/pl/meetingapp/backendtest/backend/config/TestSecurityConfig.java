package pl.meetingapp.backendtest.backend.config;


import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import pl.meetingapp.backendtest.backend.security.JwtRequestFilter;


import static org.mockito.Mockito.mock;

/**
 * Test security configuration used to disable CSRF because csrf in WebMvcTest is default enabled
 */
@TestConfiguration
@EnableWebSecurity
public class TestSecurityConfig {

    /**
    * This bean is required because the WebSecurityConfig class expects
    * a JwtRequestFilter to be present in the security filter chain.
    * Since we're mocking the filter in tests, we provide a mock here to simulate the behavior
    * of the JwtRequestFilter, allowing the test to pass without requiring actual JWT validation.
     */
    @Bean
    @Primary
    public JwtRequestFilter jwtRequestFilter() {
        return mock(JwtRequestFilter.class);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                .addFilterBefore(jwtRequestFilter(), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
