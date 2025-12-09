package ru.practicum.server.request;

import ru.practicum.dto.request.ItemRequestDto;
import ru.practicum.dto.request.NewRequestDto;
import ru.practicum.dto.request.ShortItemRequest;

import java.util.List;

public interface ItemRequestService {

    ItemRequestDto create(long userId, NewRequestDto dto);

    List<ItemRequestDto> findUserRequests(long userId);

    List<ShortItemRequest> findAllRequests(long userId);

    ItemRequestDto findById(long requestId);

}
