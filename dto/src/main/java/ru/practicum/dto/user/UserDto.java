package ru.practicum.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.util.StringUtils;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    private Long id;

    private String name;

    @Email
    private String email;

    public boolean hasName() {
        return StringUtils.hasText(name);
    }

    public boolean hasEmail() {
        return StringUtils.hasText(email);
    }
}

