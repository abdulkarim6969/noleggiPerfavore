package com.example.progettoinfonoleggi.controller.auth;

import com.example.progettoinfonoleggi.dto.LoginRequestDTO;
import com.example.progettoinfonoleggi.dto.LoginResponseDTO;
import com.example.progettoinfonoleggi.dto.RegisterRequestDTO;
import com.example.progettoinfonoleggi.model.utenti.Utenti;
import com.example.progettoinfonoleggi.service.jwt.JWTservice;
import com.example.progettoinfonoleggi.service.utenti.SaldoService;
import com.example.progettoinfonoleggi.service.utenti.UtentiService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin("http://localhost:5173")
public class AuthController {
    @Autowired
    private UtentiService utentiService;

    @Autowired
    private SaldoService saldoService;

    @Autowired
    private JWTservice jwtService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequestDTO user, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            // Collect validation errors
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(error ->
                    errors.put(error.getField(), error.getDefaultMessage())
            );
            return ResponseEntity.badRequest().body(errors);
        }

        try {
            Utenti registeredUser = utentiService.register(user);

            saldoService.creaSaldoPerUtente(registeredUser.getEmail());

            return ResponseEntity.status(HttpStatus.CREATED).body(registeredUser);
        } catch (Exception e) {
            // Handle other exceptions
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "An unexpected error occurred."));
        }
    }




    @PostMapping("/login")
    public ResponseEntity<?> login (@RequestBody LoginRequestDTO user) {
        // 1) Invoke your existing verify() method
        String result = utentiService.verify(user);  // returns token or "fail"

        // 2) On success, wrap the JWT in our DTO and return 200 OK
        if (!"fail".equals(result)) {
            LoginResponseDTO response = new LoginResponseDTO(result);
            return ResponseEntity
                    .ok(response);  // HTTP 200 with JSON body {"token":"...","type":"Bearer"} :contentReference[oaicite:6]{index=6}
        }

        // 3) On failure, return 401 Unauthorized with an error message
        Map<String, String> error = Map.of("message", "Invalid email or password.");
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)  // HTTP 401 :contentReference[oaicite:7]{index=7}
                .body(error);
    }




    @PostMapping("/logout")
public ResponseEntity<String> logout(HttpServletRequest request) {

    System.out.println("Logout function");
    String authHeader = request.getHeader("Authorization");

    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
        System.out.println("Missing or invalid Authorization header");
        return ResponseEntity.ok("Logout without  token");

    }

    String token = authHeader.substring(7);
    System.out.println("this is the token: " + token);

    jwtService.revokeToken(token);

    return ResponseEntity.ok("Logout successful, token revoked");
}
}


