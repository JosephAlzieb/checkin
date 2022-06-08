package de.checkin.web.security;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2UserAuthority;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;

public class AuthenticationTemplates {

  public static MockHttpSession orgaSession() {
    return erstelleSession("ORGA", "bendisposto");
  }

  public static MockHttpSession tutorSession() {
    return erstelleSession("TUTOR", "Jannik");
  }

  public static MockHttpSession studentSession() {
    return erstelleSession("STUDENT", "Sebastian");
  }

  private static OAuth2AuthenticationToken buildPrincipal(String role, String name) {
    Map<String, Object> attributes = new HashMap<>();
    attributes.put("login", name);

    List<GrantedAuthority> authorities = Collections.singletonList(
        new OAuth2UserAuthority("ROLE_" + role.toUpperCase(), attributes));
    OAuth2User user = new DefaultOAuth2User(authorities, attributes, "login");
    return new OAuth2AuthenticationToken(user, authorities, "whatever");
  }

  private static MockHttpSession erstelleSession(String role, String name) {
    OAuth2AuthenticationToken principal = buildPrincipal(role, name);
    MockHttpSession session = new MockHttpSession();
    session.setAttribute(
        HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
        new SecurityContextImpl(principal));
    return session;
  }

}
