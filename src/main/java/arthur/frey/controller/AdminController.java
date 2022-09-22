package arthur.frey.controller;

import arthur.frey.dto.AdminDto;
import arthur.frey.service.UserService;
import arthur.frey.store.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/admin/")
public class AdminController {

    private final UserService userService;

    public static final String GET_USERS = "users/{id}";

    @GetMapping(GET_USERS)
    public ResponseEntity<AdminDto> getUserById(@PathVariable(name = "id") Long id) {
        User user = userService.findById(id);

        if (user == null) return new ResponseEntity<>(HttpStatus.NO_CONTENT);

        AdminDto result = AdminDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .status(user.getStatus().name())
                .build();

        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
