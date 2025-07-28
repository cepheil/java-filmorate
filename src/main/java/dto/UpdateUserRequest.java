package dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;

@Data
public class UpdateUserRequest {
    @NotNull(message = "ID пользователя обязателен")
    private Long id;

    @NotBlank(message = "Логин не может быть пустым")
    private String login;

    @NotBlank(message = "Email не может быть пустым")
    @Email(message = "Некорректный формат email")
    private String email;

    private String name;

    private LocalDate birthday;
}
