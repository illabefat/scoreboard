package org.ilzi.scorecard.repository;

import org.ilzi.scorecard.model.MatchSummary;
import org.ilzi.scorecard.model.MatchSummaryTestData;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MatchSummaryRepositoryTest implements MatchSummaryTestData {

    private final MatchSummaryRepository repository = new MatchSummaryRepository();

    @Test
    void get__gets_match_summary_by_id() {
        // given
        final var matchSummary = givenExists(aMatchSummary());

        // when
        final var result = repository.get(matchSummary.id);

        // then
        assertThat(result).isEqualTo(matchSummary);
    }

    @Test
    void get__throws_exception_when_match_not_found() {
        // then
        assertThatThrownBy(() -> repository.get("nonexistentMatchId"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Match with id nonexistentMatchId not found");
    }

    @Test
    void find__finds_match_summary_by_id() {
        // given
        final var matchSummary = givenExists(aMatchSummary());

        // when
        final var result = repository.find(matchSummary.id);

        // then
        assertThat(result).hasValue(matchSummary);
    }

    @Test
    void find__returns_empty_optional_when_match_not_found() {
        // when
        final var result = repository.find("nonexistentMatchId");

        // then
        assertThat(result).isEmpty();
    }

    @Test
    void getAll__returns_match_summaries_sorted_by_total_score_and_created_date() {
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
        final var result = repository.getAll();

        // then
        assertThat(result).containsExactlyInAnyOrder(firstMatch, fourthMatch, thirdMatch, secondMatch);
    }

    @Test
    void getAll__returns_empty_list_when_no_match_summaries_found() {
        // when
        final var result = repository.getAll();

        // then
        assertThat(result).isEmpty();
    }

    @Test
    void create__creates_match_with_default_score_of() {
        // when
        final var id = repository.create("home", "away");

        // and
        final var created = repository.get(id);
        assertThat(created.homeTeam).isEqualTo("home");
        assertThat(created.homeTeamScore).isEqualTo(0);
        assertThat(created.awayTeam).isEqualTo("away");
        assertThat(created.awayTeamScore).isEqualTo(0);
    }

    @Test
    void create__throws_exception_when_match_summary_with_such_id_already_exists() {
        // given
        final var matchSummary = givenExists(aMatchSummary()
            .homeTeamScore(1)
            .awayTeamScore(2));

        // then
        assertThatThrownBy(() -> repository.create(matchSummary.homeTeam, matchSummary.awayTeam))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("Match with id %s already exists", matchSummary.id);

        // and
        assertThat(repository.get(matchSummary.id)).isEqualTo(matchSummary);
    }

    @Test
    void updateScore__updates_existing_match_summary() {
        // given
        final var matchSummary = givenExists(aMatchSummary());

        // when
        repository.updateScore(matchSummary.id, 10, 5);

        // then
        final var updatedMatch = repository.get(matchSummary.id);
        assertThat(updatedMatch.homeTeamScore).isEqualTo(10);
        assertThat(updatedMatch.awayTeamScore).isEqualTo(5);
    }

    @Test
    void updateScore__throws_exception_when_match_summary_does_not_exist() {
        // then
        assertThatThrownBy(() -> repository.updateScore("unknown_id", 10, 5))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("Match with id unknown_id not found");
    }

    @Test
    void remove__removes_match_summary() {
        // given
        final var matchSummary = givenExists(aMatchSummary());

        // when
        repository.remove(matchSummary.id);

        // then
        assertThat(repository.find(matchSummary.id)).isEmpty();
    }

    @Test
    void remove__does_not_throw_exception_when_match_summary_does_not_exist() {
        // when & then
        assertThatCode(() -> repository.remove("nonexistentMatchId"))
            .doesNotThrowAnyException();
    }

    private MatchSummary givenExists(MatchSummary.Builder builder) {
        final var matchSummary = builder.build();
        repository.add(matchSummary);
        return matchSummary;
    }
}