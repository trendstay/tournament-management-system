package com.tournament.repository;

import com.tournament.model.match.Match;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class InMemoryMatchRepository implements MatchRepository {
    private final Map<Long, Match> storage = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    @Override
    public Match save(Match match) {
        if (match.getId() == null) {
            match.setId(idGenerator.getAndIncrement());
        }
        storage.put(match.getId(), match);
        return match;
    }

    @Override
    public Optional<Match> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<Match> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public boolean delete(Long id) {
        return storage.remove(id) != null;
    }
}