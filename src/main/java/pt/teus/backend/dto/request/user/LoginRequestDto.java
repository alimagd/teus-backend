package pt.teus.backend.dto.request.user;


import jakarta.validation.constraints.NotBlank;

public record LoginRequestDto (

    @NotBlank(message = "Email as Username is required")
    String email, // in this project email is
    @NotBlank(message = "Password is required")
    String password
){
}
