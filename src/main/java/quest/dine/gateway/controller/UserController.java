package quest.dine.gateway.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import quest.dine.gateway.dto.ApiResponse;
import quest.dine.gateway.dto.CreateUserRequest;
import quest.dine.gateway.dto.LoginRequest;
import quest.dine.gateway.services.JwtService;
import quest.dine.gateway.services.UserService;
import reactor.core.publisher.Mono;

@RestController
@Validated
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;
    private final JwtService jwtService;

    public UserController(UserService userService, JwtService jwtService) {
        this.userService = userService;
        this.jwtService = jwtService;
    }

    @PostMapping("/create")
    public Mono<ResponseEntity<ApiResponse>> createUser(@Valid @RequestBody Mono<CreateUserRequest> createUserRequestMono) {
        return createUserRequestMono.flatMap(createUserRequest -> {
            String username = createUserRequest.getUsername();
            String password = createUserRequest.getPassword();
            String email = createUserRequest.getEmail();

            return userService.createUser(username, password, email)
                    .thenReturn(new ResponseEntity<>(new ApiResponse("User created successfully", HttpStatus.OK.value()), HttpStatus.OK))
                    .onErrorReturn(new ResponseEntity<>(new ApiResponse("Failed to create user", HttpStatus.BAD_REQUEST.value()), HttpStatus.BAD_REQUEST));
        });
    }

    @PostMapping("/login")
    public Mono<ResponseEntity<ApiResponse>> login(@Valid @RequestBody Mono<LoginRequest> loginRequestMono) {
        return loginRequestMono.flatMap(createUserRequest -> {
            String password = createUserRequest.getPassword();
            String email = createUserRequest.getEmail();

            return userService.getUserByEmail(email, password)
                    .map(userDetails -> {
                        String token = jwtService.generateToken(email);
                        return ResponseEntity.ok(new ApiResponse(token, HttpStatus.OK.value()));
                    });
        });

    }

}
