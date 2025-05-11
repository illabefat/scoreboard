package org.ilzi.scorecard.service;

import org.ilzi.scorecard.model.MatchSummary;
import org.ilzi.scorecard.model.MatchSummaryTestData;
import org.ilzi.scorecard.repository.TestMatchSummaryRepository;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MatchSummaryServiceTest implements MatchSummaryTestData {

    private final TestMatchSummaryRepository repository = new TestMatchSummaryRepository();
    private final MatchSummaryService service = new MatchSummaryService(repository);

    @Test
    void startMatch_shouldCreateAndStoreMatchSummary() {
        // given
        final var homeTeam = "TeamA";
        final var awayTeam = "TeamB";
        final var expectedId = homeTeam + "_" + awayTeam;

        // when
        String matchId = service.startMatch(homeTeam, awayTeam);

        // then
        assertThat(matchId).isEqualTo(expectedId);

        final var createdMatch = repository.get(expectedId);
        assertThat(createdMatch.homeTeam).isEqualTo(homeTeam);
        assertThat(createdMatch.awayTeam).isEqualTo(awayTeam);
        assertThat(createdMatch.homeTeamScore).isEqualTo(0);
        assertThat(createdMatch.awayTeamScore).isEqualTo(0);
    }

    @Test
    void updateScore_shouldUpdateMatchSummaryScore() {
        // given
        final var existingSummary = givenExists(aMatchSummary()
            .homeTeamScore(1)
            .awayTeamScore(1));

        // when
        service.updateScore(existingSummary.id, 3, 2);

        // then
        final var updatedMatch = repository.get(existingSummary.id);
        assertThat(updatedMatch.homeTeamScore).isEqualTo(3);
        assertThat(updatedMatch.awayTeamScore).isEqualTo(2);
    }

    @Test
    void endMatch_shouldRemoveMatchSummary() {
        // given
        final var existingSummary = givenExists(aMatchSummary());

        // when
        service.endMatch(existingSummary.id);

        // then
        assertThat(repository.find(existingSummary.id)).isEmpty();
    }

    @Test
    void getAll_returns_all_matches_sorted_by_score_and_created_date() {
        // given
        final var createdDate = System.currentTimeMillis();
        final var firstMatch = givenExists(aMatchSummary()
            .homeTeamScore(10)
            .awayTeamScore(10)
            .createdDate(createdDate));
        final var secondMatch = givenExists(aMatchSummary()
            .homeTeamScore(0)
            .awayTeamScore(5)
            .createdDate(createdDate + 1L));
        final var thirdMatch = givenExists(aMatchSummary()
            .homeTeamScore(10)
            .awayTeamScore(2)
            .createdDate(createdDate + 2L));
        final var fourthMatch = givenExists(aMatchSummary()
            .homeTeamScore(6)
            .awayTeamScore(6)
            .createdDate(createdDate + 3L));

        // when
        final var result = service.getAllMatchSummaries();

        // then
        assertThat(result).containsExactly(firstMatch, fourthMatch, thirdMatch, secondMatch);
    }

    private MatchSummary givenExists(MatchSummary.Builder builder) {
        final var matchSummary = builder.build();
        repository.add(matchSummary);
        return matchSummary;
    }
}