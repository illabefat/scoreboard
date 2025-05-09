package org.ilzi.scorecard.repository;

import org.ilzi.scorecard.model.MatchSummary;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import static org.ilzi.scorecard.model.MatchSummary.Builder.matchSummary;

public class MatchSummaryRepository {

    private final ConcurrentHashMap<String, MatchSummary> matchSummaries;

    public MatchSummaryRepository() {
        this.matchSummaries = new ConcurrentHashMap<>();
    }

    public Optional<MatchSummary> find(String matchId) {
        return Optional.ofNullable(matchSummaries.get(matchId));
    }

    public MatchSummary get(String matchId) {
        return find(matchId)
            .orElseThrow(() -> new IllegalArgumentException("Match with id %s not found".formatted(matchId)));
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
        final var result = matchSummaries.putIfAbsent(matchSummary.id, matchSummary);
        if (result != null) {
            throw new IllegalStateException("Match with id %s already exists".formatted(matchSummary.id));
        }
    }

    public void updateScore(String matchId, int newHomeTeamScore, int newAwayTeamScore) {
        final var result = matchSummaries
            .computeIfPresent(matchId, (key, oldValue) -> oldValue.withNewScore(newHomeTeamScore, newAwayTeamScore));
        if (result == null) {
            throw new IllegalStateException("Match with id %s not found".formatted(matchId));
        }
    }

    public void remove(String matchId) {
        matchSummaries.remove(matchId);
    }
}
