package org.ilzi.scorecard.repository;

import org.ilzi.scorecard.model.MatchSummary;
import org.ilzi.scorecard.util.cache.CachedReference;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static java.util.Comparator.comparing;

import static org.ilzi.scorecard.model.MatchSummary.Builder.matchSummary;

public class MatchSummaryRepository {

    private final ConcurrentHashMap<String, MatchSummary> matchSummaries;
    private final CachedReference<List<MatchSummary>> sortedMatchSummaries;
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock(true);

    public MatchSummaryRepository() {
        this.matchSummaries = new ConcurrentHashMap<>();
        this.sortedMatchSummaries = new CachedReference<>(this::getAllSynchronized);
    }

    public Optional<MatchSummary> find(String matchId) {
        return Optional.ofNullable(matchSummaries.get(matchId));
    }

    public MatchSummary get(String matchId) {
        return find(matchId)
            .orElseThrow(() -> new IllegalArgumentException("Match with id %s not found".formatted(matchId)));
    }
    
    public List<MatchSummary> getAll() {
        return sortedMatchSummaries.get();
    }

    private List<MatchSummary> getAllSynchronized() {
        return withReadLock(() -> matchSummaries.values().stream()
            .sorted(comparing((MatchSummary m) -> m.homeTeamScore + m.awayTeamScore)
                .thenComparing(m -> m.createdTimestamp)
                .reversed())
            .toList());
    }

    public String create(String homeTeam, String awayTeam) {
        final var matchSummary = matchSummary()
            .id(homeTeam + "_" + awayTeam)
            .homeTeam(homeTeam)
            .awayTeam(awayTeam)
            .homeTeamScore(0)
            .awayTeamScore(0)
            .build();
        add(matchSummary);
        return matchSummary.id;
    }

    void add(MatchSummary matchSummary) {
        final var result = withWriteLock(() -> matchSummaries.putIfAbsent(matchSummary.id, matchSummary));

        if (result != null) {
            throw new IllegalStateException("Match with id %s already exists".formatted(matchSummary.id));
        }
        sortedMatchSummaries.invalidate();
    }

    public void updateScore(String matchId, int newHomeTeamScore, int newAwayTeamScore) {
        final var result = withWriteLock(() -> matchSummaries
            .computeIfPresent(matchId, (key, oldValue) -> oldValue.withNewScore(newHomeTeamScore, newAwayTeamScore)));
        if (result == null) {
            throw new IllegalStateException("Match with id %s not found".formatted(matchId));
        }
        sortedMatchSummaries.invalidate();
    }

    public void remove(String matchId) {
        final var removed = withWriteLock(() -> matchSummaries.remove(matchId));
        if (removed != null) {
            sortedMatchSummaries.invalidate();
        }
    }

    private <R> R withReadLock(Callable<R> callable) {
        return withLock(lock.readLock(), callable);
    }

    private <R> R  withWriteLock(Callable<R> callable) {
        return withLock(lock.writeLock(), callable);
    }

    private <R> R withLock(Lock lock, Callable<R> callable) {
        lock.lock();
        try {
            return callable.call();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
    }
}
