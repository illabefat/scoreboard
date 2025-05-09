package org.ilzi.scorecard.service;

import org.ilzi.scorecard.model.MatchSummary;
import org.ilzi.scorecard.repository.MatchSummaryRepository;

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
}
