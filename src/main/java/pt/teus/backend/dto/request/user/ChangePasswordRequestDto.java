package pt.teus.backend.dto.request.user;

public record ChangePasswordRequestDto (

    String currentPassword,
    String newPassword,
    String confirmationPassword
){
}

