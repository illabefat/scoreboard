package org.ilzi.scorecard.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import javax.validation.ValidationException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MatchSummaryTest implements MatchSummaryTestData {

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", " "})
    void fails_to_create_match_summary_with_incorrect_home_team(String incorrectTeamName) {
        // then
        assertThatThrownBy(() -> aMatchSummary().homeTeam(incorrectTeamName).build())
            .isInstanceOf(ValidationException.class)
            .hasMessage("homeTeam is null or empty");
    }

    @Test
    void fails_to_create_match_with_negative_home_team_score() {
        // then
        assertThatThrownBy(() -> aMatchSummary().homeTeamScore(-1).build())
            .isInstanceOf(ValidationException.class)
            .hasMessage("homeTeamScore must be greater or equal to 0, but is -1");
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", " "})
    void fails_to_create_match_summary_with_incorrect_away_team(String incorrectTeamName) {
        // then
        assertThatThrownBy(() ->  aMatchSummary().awayTeam(incorrectTeamName).build())
            .isInstanceOf(ValidationException.class)
            .hasMessage("awayTeam is null or empty");
    }

    @Test
    void fails_to_create_match_with_negative_away_team_score() {
        // then
        assertThatThrownBy(() -> aMatchSummary().awayTeamScore(-1).build())
            .isInstanceOf(ValidationException.class)
            .hasMessage("awayTeamScore must be greater or equal to 0, but is -1");
    }

    @Test
    void updates_score() {
        // given
        var matchSummary = aMatchSummary().build();

        // when
        var updatedMatchSummary = matchSummary.withNewScore(10, 2);

        // then
        assertThat(updatedMatchSummary.homeTeamScore).isEqualTo(10);
        assertThat(updatedMatchSummary.awayTeamScore).isEqualTo(2);
    }

    @Test
    void updates_to_the_same_score() {
        // given
        var matchSummary = aMatchSummary()
            .homeTeamScore(10)
            .awayTeamScore(2)
            .build();

        // when
        var updatedMatchSummary = matchSummary.withNewScore(10, 2);

        // then
        assertThat(updatedMatchSummary).isEqualTo(matchSummary);
    }

    @Test
    void fails_to_update_score_when_new_home_score_is_less_than_current_value() {
        // given
        var matchSummary = aMatchSummary()
            .homeTeamScore(10)
            .awayTeamScore(2)
            .build();

        // then
        assertThatThrownBy(() -> matchSummary.withNewScore(matchSummary.homeTeamScore - 1, matchSummary.awayTeamScore))
            .isInstanceOf(ValidationException.class)
            .hasMessage("newHomeTeamScore must be greater or equal to 10, but is 9");
    }

    @Test
    void fails_to_update_score_when_new_away_score_is_less_than_current_value() {
        // given
        var matchSummary = aMatchSummary()
            .homeTeamScore(10)
            .awayTeamScore(2)
            .build();

        // then
        assertThatThrownBy(() -> matchSummary.withNewScore(matchSummary.homeTeamScore, matchSummary.awayTeamScore - 1))
            .isInstanceOf(ValidationException.class)
            .hasMessage("newAwayTeamScore must be greater or equal to 2, but is 1");
    }
}
