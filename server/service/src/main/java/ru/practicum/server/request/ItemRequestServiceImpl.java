package ru.practicum.server.request;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.server.exceptions.NotFoundException;
import ru.practicum.dto.request.ItemRequestDto;
import ru.practicum.dto.request.NewRequestDto;
import ru.practicum.dto.request.ShortItemRequest;
import ru.practicum.server.user.User;
import ru.practicum.server.user.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final UserRepository userRepository;
    private final ItemRequestRepository requestRepository;

    @Transactional
    @Override
    public ItemRequestDto create(long userId, NewRequestDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));
        ItemRequest request = requestRepository.save(ItemRequestMapper.mapToItemRequest(user, dto));
        return ItemRequestMapper.mapToItemRequestDto(request);
    }

    @Override
    public List<ItemRequestDto> findUserRequests(long userId) {
        List<ItemRequest> requests = requestRepository.findAllByUserIdOrderByCreatedDesc(userId);
        return ItemRequestMapper.mapToItemRequestDto(requests);
    }

    @Override
    public List<ShortItemRequest> findAllRequests(long userId) {
        return requestRepository.findAllByUserIdNotOrderByCreatedDesc(userId);
    }

    @Override
    public ItemRequestDto findById(long requestId) {
        ItemRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос товвара с id " + requestId + " не найден"));
        return ItemRequestMapper.mapToItemRequestDto(request);
    }

}
