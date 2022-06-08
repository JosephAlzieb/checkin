package de.checkin.web.security;

import java.util.HashSet;
import java.util.Set;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
@EnableWebSecurity
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

  @Value("${chicken.rollen.tutor}")
  private Set<String> tutoren;

  @Value("${chicken.rollen.orga}")
  private Set<String> organs;


  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.authorizeRequests(a -> a
            .antMatchers("/", "/error", "/css/**", "/img/**").permitAll()
            .anyRequest().authenticated()
        )
        .exceptionHandling(
            e -> e.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
        .csrf(c -> c.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()))
        .logout(l -> l.logoutSuccessUrl("/").permitAll()).oauth2Login();
  }

  @Bean
  OAuth2UserService<OAuth2UserRequest, OAuth2User> createUserService() {
    DefaultOAuth2UserService defaultService = new DefaultOAuth2UserService();
    return userRequest -> {
      OAuth2User oauth2User = defaultService.loadUser(userRequest);

      var attributes = oauth2User.getAttributes();

      var authorities = new HashSet<GrantedAuthority>();

      String login = attributes.get("login").toString();

      if (organs.contains(login)) {
        authorities.add(new SimpleGrantedAuthority("ROLE_ORGA"));
      } else if (tutoren.contains(login)) {
        authorities.add(new SimpleGrantedAuthority("ROLE_TUTOR"));
      } else {
        authorities.add(new SimpleGrantedAuthority("ROLE_STUDENT"));
      }

      return new DefaultOAuth2User(authorities, attributes, "login");
    };
  }
}
