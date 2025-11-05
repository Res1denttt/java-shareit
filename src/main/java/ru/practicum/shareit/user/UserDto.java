package ru.practicum.shareit.user;

import jakarta.validation.constraints.Email;
import lombok.Data;
import org.springframework.util.StringUtils;

@Data
public class UserDto {
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

