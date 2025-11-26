package ru.practicum.shareit.booking.strategy;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.BookingState;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Component
public class BookingFetchStateStrategyFactory {

    private Map<BookingState, BookingFetchStateStrategy> strategies;

    public BookingFetchStateStrategyFactory(Set<BookingFetchStateStrategy> strategySet) {
        create(strategySet);
    }

    public BookingFetchStateStrategy findStrategy(BookingState state) {
        return strategies.get(state);
    }

    private void create(Set<BookingFetchStateStrategy> strategySet) {
        strategies = new HashMap<>();
        strategySet.forEach(
                (strategy) -> strategies.put(strategy.getStrategyState(), strategy)
        );
    }
}
