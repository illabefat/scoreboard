package org.ilzi.scorecard.service;

import org.ilzi.scorecard.model.MatchSummary;
import org.ilzi.scorecard.repository.MatchSummaryRepository;

import java.util.List;

import static java.util.Comparator.comparing;

public class MatchSummaryService {

    private final MatchSummaryRepository matchSummaryRepository;

    public MatchSummaryService() {
        this(new MatchSummaryRepository());
    }

    public MatchSummaryService(MatchSummaryRepository matchSummaryRepository) {
        this.matchSummaryRepository = matchSummaryRepository;
    }

    public String startMatch(String homeTeam, String awayTeam) {
        return matchSummaryRepository.create(homeTeam, awayTeam);
    }

    public void updateScore(String matchId, int homeTeamScore, int awayTeamScore) {
        matchSummaryRepository.updateScore(matchId, homeTeamScore, awayTeamScore);
    }

    public void endMatch(String matchId) {
        matchSummaryRepository.remove(matchId);
    }

    public List<MatchSummary> getAllMatchSummaries() {
        return matchSummaryRepository.getAll().stream()
            .sorted(comparing((MatchSummary m) -> m.homeTeamScore + m.awayTeamScore)
                .thenComparing(m -> m.createdTimestamp)
                .reversed())
            .toList();
    }
}
