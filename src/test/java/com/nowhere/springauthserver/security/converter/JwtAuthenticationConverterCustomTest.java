package com.nowhere.springauthserver.security.converter;

import com.nowhere.springauthserver.persistence.entity.Role;
import com.nowhere.springauthserver.security.SecurityConstants;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class JwtAuthenticationConverterCustomTest {

    private final Jwt mockJwt = mock(Jwt.class);
    private final Converter<Jwt, Collection<GrantedAuthority>> mockJwtGrantedAuthoritiesConverter = mock(JwtGrantedAuthoritiesConverter.class);

    @Test
    void convertGivenNoScopesShouldReturnTokenWithExtractedRoles() {
        List<String> roles = List.of(Role.RoleType.ADMIN.name(), Role.RoleType.USER.name());
        when(mockJwt.hasClaim(SecurityConstants.ROLES_CLAIM)).thenReturn(true);
        when(mockJwt.getClaim(SecurityConstants.ROLES_CLAIM)).thenReturn(roles);
        when(mockJwt.getClaimAsString(any())).thenReturn("subject");
        when(mockJwtGrantedAuthoritiesConverter.convert(mockJwt)).thenReturn(null);

        JwtAuthenticationConverterCustom converter = new JwtAuthenticationConverterCustom(mockJwtGrantedAuthoritiesConverter);
        AbstractAuthenticationToken token = converter.convert(mockJwt);

        assertInstanceOf(JwtAuthenticationToken.class, token);
        assertEquals(2, token.getAuthorities().size());
        assertThat(token.getAuthorities()).containsExactlyInAnyOrder(
                new SimpleGrantedAuthority("ROLE_ADMIN"),
                new SimpleGrantedAuthority("ROLE_USER")
        );
    }

    @Test
    void convertGivenExistingScopesShouldAddRolesToAuthorities() {
        List<String> roles = List.of("USER");

        when(mockJwt.hasClaim(SecurityConstants.ROLES_CLAIM)).thenReturn(true);
        when(mockJwt.getClaim(SecurityConstants.ROLES_CLAIM)).thenReturn(roles);
        when(mockJwt.getClaimAsString(any())).thenReturn("subject");
        // mutable list for authorities
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("SCOPE_message.read"));

        when(mockJwtGrantedAuthoritiesConverter.convert(mockJwt)).thenReturn(authorities);

        JwtAuthenticationConverterCustom converter = new JwtAuthenticationConverterCustom(mockJwtGrantedAuthoritiesConverter);
        AbstractAuthenticationToken token = converter.convert(mockJwt);


        assertInstanceOf(JwtAuthenticationToken.class, token);
        assertEquals(2, token.getAuthorities().size());
        assertThat(token.getAuthorities()).containsExactlyInAnyOrder(
                new SimpleGrantedAuthority("SCOPE_message.read"),
                new SimpleGrantedAuthority("ROLE_USER")
        );
    }
}
