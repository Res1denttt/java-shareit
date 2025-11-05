package ru.practicum.shareit.user;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserMapper {

    public static User updateUserFields(User user, UserDto userDto) {
        User updated = new User();
        updated.setId(user.getId());
        updated.setName(userDto.hasName() ? userDto.getName() : user.getName());
        updated.setEmail(userDto.hasEmail() ? userDto.getEmail() : user.getEmail());
        return updated;
    }

}
