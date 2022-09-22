package arthur.frey.controller;

import arthur.frey.dto.AuthenticationDto;
import arthur.frey.security.JwtTokenProvider;
import arthur.frey.service.UserService;
import arthur.frey.store.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/auth/")
public class AuthController {

    private final AuthenticationManager authenticationManager;

    private final JwtTokenProvider jwtTokenProvider;

    private final UserService userService;

    public static final String POST_LOGIN = "login";

    @PostMapping(POST_LOGIN)
    public ResponseEntity<Map<Object, Object>> login(@RequestBody AuthenticationDto authDto) {
        try {
            String username = authDto.getUsername();
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, authDto.getPassword()));
            User user = userService.findByUsername(username);

            if (user == null) throw new UsernameNotFoundException("User with username: " + username + " not found");

            String token = jwtTokenProvider.createToken(username, user.getRoles());

            Map<Object, Object> response = new HashMap<>();
            response.put("username", username);
            response.put("token", token);

            return ResponseEntity.ok(response);

        } catch (AuthenticationException e) {
            throw new BadCredentialsException("Invalid username or password");
        }
    }
}
