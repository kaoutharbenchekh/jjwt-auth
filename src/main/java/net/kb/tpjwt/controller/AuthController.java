package net.kb.tpjwt.controller;

import net.kb.tpjwt.dto.AuthResponse;
import net.kb.tpjwt.dto.LoginRequest;
import net.kb.tpjwt.dto.RegisterRequest;
import net.kb.tpjwt.entity.AppUser;
import net.kb.tpjwt.repository.AppUserRepository;
import net.kb.tpjwt.service.CustomerUserDetailService;
import net.kb.tpjwt.service.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AppUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final CustomerUserDetailService userDetailService;

    public AuthController(
            AppUserRepository userRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtService jwtService,
            CustomerUserDetailService userDetailService) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userDetailService = userDetailService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(
            @RequestBody RegisterRequest registerRequest) {

        if (userRepository.existsByEmail(registerRequest.email())) {
            return ResponseEntity.badRequest()
                    .body("Email already exists");
        }

        AppUser appUser = new AppUser();

        appUser.setEmail(registerRequest.email());
        appUser.setPassword(
                passwordEncoder.encode(registerRequest.password())
        );
        appUser.setRole("USER");

        userRepository.save(appUser);

        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/login")
    public AuthResponse login(
            @RequestBody LoginRequest loginRequest) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.email(),
                        loginRequest.password()
                )
        );

        UserDetails user =
                userDetailService.loadUserByUsername(loginRequest.email());

        String token = jwtService.generateToken(user);

        return new AuthResponse(token);
    }
}
