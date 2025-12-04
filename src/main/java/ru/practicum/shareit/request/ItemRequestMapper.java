package ru.practicum.shareit.request;

import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.NewRequestDto;
import ru.practicum.shareit.user.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class ItemRequestMapper {
    public static ItemRequest mapToItemRequest(User user, NewRequestDto dto) {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription(dto.getDescription());
        itemRequest.setUser(user);
        return itemRequest;
    }

    public static ItemRequestDto mapToItemRequestDto(ItemRequest request) {
        List<ItemDto> itemDtos = Optional.ofNullable(request.getItems())
                .orElse(Collections.emptyList())
                .stream()
                .map(ItemMapper::mapToItemDto)
                .toList();


        return ItemRequestDto.builder()
                .id(request.getId())
                .description(request.getDescription())
                .created(request.getCreated())
                .items(itemDtos)
                .build();
    }

    public static List<ItemRequestDto> mapToItemRequestDto(Iterable<ItemRequest> requests) {
        List<ItemRequestDto> dtos = new ArrayList<>();
        for (ItemRequest request : requests) {
            dtos.add(mapToItemRequestDto(request));
        }
        return dtos;
    }
}
