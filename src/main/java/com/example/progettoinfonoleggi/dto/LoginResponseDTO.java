package com.example.progettoinfonoleggi.dto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginResponseDTO {

    private String token;
    private String tokenType = "Bearer";
    public LoginResponseDTO(String token) {
        this.token = token;
    }
}
