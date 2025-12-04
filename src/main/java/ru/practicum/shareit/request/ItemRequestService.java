package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.NewRequestDto;
import ru.practicum.shareit.request.dto.ShortItemRequest;

import java.util.List;

public interface ItemRequestService {

    ItemRequestDto create(long userId, NewRequestDto dto);

    List<ItemRequestDto> findUserRequests(long userId);

    List<ShortItemRequest> findAllRequests(long userId);

    ItemRequestDto findById(long requestId);

}
