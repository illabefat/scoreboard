package org.ilzi.scorecard.model;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;
import static org.ilzi.scorecard.model.MatchSummary.Builder.matchSummary;

public interface MatchSummaryTestData {

    default MatchSummary.Builder aMatchSummary() {
        final var homeTeam = randomAlphanumeric(10);
        final var awayTeam = randomAlphanumeric(10);
        return matchSummary()
            .id(homeTeam + "_" + awayTeam)
            .createdDate(123456789L)
            .homeTeam(homeTeam)
            .awayTeam(awayTeam)
            .homeTeamScore(0)
            .awayTeamScore(0);
    }
}
