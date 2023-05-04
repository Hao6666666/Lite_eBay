package com.csye6225.config;


import com.csye6225.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;


import static org.springframework.security.config.Customizer.withDefaults;


@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    @Autowired
    UserService userService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    //based on basic auth for security tests
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
      http.csrf().disable()
              .authorizeHttpRequests((authz)->
              {authz.anyRequest().authenticated();})
              .httpBasic(withDefaults());
      return http.build();
    };

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring()
                .requestMatchers(HttpMethod.GET,"/healthz", "/v1/product/{id}")
                .requestMatchers(HttpMethod.POST,"/v1/user");
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        return new AuthenticationProvider() {
            @Override //realize authentication function
            public Authentication authenticate(Authentication authentication) throws AuthenticationException {
                //get username and password from authentication object
                String username = authentication.getName();
                String password = authentication.getCredentials().toString();

                UserDetails user = userService.loadUserByUsername(username);

                if(passwordEncoder().matches(password, user.getPassword())) {
                    //if the password is correct, create an UsernamePasswordAuthenticationToken object and return
                    return new UsernamePasswordAuthenticationToken(
                            username,
                            password,
                            user.getAuthorities());
                }else{
                    //password is incorrect and throw BadCredentialsException
                    throw new BadCredentialsException("The username or password is wrong!");
                }
            }

            @Override
            public boolean supports(Class<?> authentication) {
                return authentication.equals(UsernamePasswordAuthenticationToken.class);
            }
        };
    }


}

