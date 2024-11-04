package project.security;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;
import project.model.Role;
import project.model.User;

public class WithMockCustomUserSecurityContextFactory
        implements WithSecurityContextFactory<WithMockCustomUser> {
    @Override
    public SecurityContext createSecurityContext(WithMockCustomUser customUser) {
        User principal = new User();
        principal.setId(customUser.userId());
        principal.setUsername(customUser.username());
        principal.setRoles(getAuthorities(customUser.roles()));

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        Authentication auth = new UsernamePasswordAuthenticationToken(
                principal, null, principal.getAuthorities());

        context.setAuthentication(auth);
        return context;
    }

    private Set<Role> getAuthorities(String[] roles) {
        return Arrays.stream(roles)
                .map(roleName -> {
                    Role role = new Role();
                    role.setName(Role.RoleName.valueOf(roleName));
                    return role;
                })
                .collect(Collectors.toSet());
    }
}
