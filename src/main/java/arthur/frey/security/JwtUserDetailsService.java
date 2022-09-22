package arthur.frey.security;

import arthur.frey.service.UserService;
import arthur.frey.store.entity.Role;
import arthur.frey.store.entity.Status;
import arthur.frey.store.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
public class JwtUserDetailsService implements UserDetailsService {

    private final UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userService.findByUsername(username);

        if (user == null) {
            throw new UsernameNotFoundException("User with username: " + username + " not found");
        }

        final Collection<GrantedAuthority> grantedAuthorities =
                mapToGrantedAuthorities(new ArrayList<>(user.getRoles()));

        JwtUser jwtUser = JwtUser.builder()
                .id(user.getId())
                .username(user.getUsername())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .password(user.getPassword())
                .authorities(grantedAuthorities)
                .enabled(user.getStatus().equals(Status.ACTIVE))
                .lastPasswordResetDate(user.getUpdated())
                .build();

        log.info("IN loadUserByUsername - user with username: {} successfully loaded", username);

        return jwtUser;
    }

    private List<GrantedAuthority> mapToGrantedAuthorities(List<Role> userRoles) {
        return userRoles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList());
    }
}
