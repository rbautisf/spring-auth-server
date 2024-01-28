package com.nowherelearn.postservice.security.converter;


import com.nowherelearn.postservice.security.SecurityConstants;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;

public class JwtAuthenticationConverterCustom implements Converter<Jwt, AbstractAuthenticationToken> {
    private final Converter<Jwt, Collection<GrantedAuthority>> jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        Collection<GrantedAuthority> authorities = this.jwtGrantedAuthoritiesConverter.convert(jwt);
        if (authorities != null) {
            authorities.addAll(extractRolesFromClaims(jwt));
        } else {
            authorities = extractRolesFromClaims(jwt);
        }
        String principalClaimValue = jwt.getClaimAsString(JwtClaimNames.SUB);
        return new JwtAuthenticationToken(jwt, authorities, principalClaimValue);
    }


    @SuppressWarnings("unchecked")
    private Collection<GrantedAuthority> extractRolesFromClaims(Jwt jwt) {
        if (jwt.hasClaim(SecurityConstants.ROLES_CLAIM)) {
            Object authorities = jwt.getClaim(SecurityConstants.ROLES_CLAIM);
            if (authorities instanceof Collection) {
                return ((Collection<String>) authorities).stream()
                        .map(role -> (new SimpleGrantedAuthority(SecurityConstants.DEFAULT_AUTHORITY_PREFIX + role)))
                        .collect(Collectors.toList());
            }
        }
        return Collections.emptyList();
    }

}
