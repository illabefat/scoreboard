package org.ilzi.scorecard.model;

import javax.validation.ValidationException;
import java.util.Objects;

import static java.util.Objects.hash;
import static java.util.Objects.requireNonNull;
import static org.ilzi.scorecard.model.MatchSummary.Builder.matchSummary;

public class MatchSummary {

    public final String id;
    public final long createdTimestamp;
    
    public final String homeTeam;
    public final String awayTeam;
    public final int homeTeamScore;
    public final int awayTeamScore;

    private MatchSummary(Builder builder) {
        this.id = requireNonNull(builder.id);
        this.createdTimestamp = builder.createdDate;
        this.homeTeam = checkNotEmpty("homeTeam", builder.homeTeam);
        this.awayTeam = checkNotEmpty("awayTeam", builder.awayTeam);
        this.homeTeamScore = checkGreaterOrEqual("homeTeamScore", builder.homeTeamScore, 0);
        this.awayTeamScore = checkGreaterOrEqual("awayTeamScore", builder.awayTeamScore, 0);
    }

    public MatchSummary withNewScore(int newHomeTeamScore, int newAwayTeamScore) {
        return copy()
            .homeTeamScore(checkGreaterOrEqual("newHomeTeamScore", newHomeTeamScore, homeTeamScore))
            .awayTeamScore(checkGreaterOrEqual("newAwayTeamScore", newAwayTeamScore, awayTeamScore))
            .build();
    }
    
    public Builder copy() {
        return matchSummary()
            .id(id)
            .createdDate(createdTimestamp)
            .homeTeam(homeTeam)
            .awayTeam(awayTeam)
            .homeTeamScore(homeTeamScore)
            .awayTeamScore(awayTeamScore);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        MatchSummary that = (MatchSummary) o;
        return createdTimestamp == that.createdTimestamp
            && homeTeamScore == that.homeTeamScore
            && awayTeamScore == that.awayTeamScore
            && Objects.equals(id, that.id)
            && Objects.equals(homeTeam, that.homeTeam)
            && Objects.equals(awayTeam, that.awayTeam);
    }

    @Override
    public int hashCode() {
        return hash(id, createdTimestamp, homeTeam, awayTeam, homeTeamScore, awayTeamScore);
    }

    @Override
    public String toString() {
        return "MatchSummary{" +
            "id='" + id + '\'' +
            ", createdTimestamp=" + createdTimestamp +
            ", homeTeam='" + homeTeam + '\'' +
            ", awayTeam='" + awayTeam + '\'' +
            ", homeTeamScore=" + homeTeamScore +
            ", awayTeamScore=" + awayTeamScore +
            '}';
    }

    private static String checkNotEmpty(String param, String value) {
        if (value == null || value.isBlank()) {
            throw new ValidationException("%s is null or empty".formatted(param));
        }
        return value;
    }

    private static int checkGreaterOrEqual(String param, int value, int min) {
        if (value < min) {
            throw new ValidationException("%s must be greater or equal to %d, but is %d".formatted(param, min, value));
        }
        return value;
    }
    
    public static class Builder {

        private String id;
        private long createdDate = System.currentTimeMillis();
        private String homeTeam;
        private String awayTeam;
        private int homeTeamScore;
        private int awayTeamScore;

        public static Builder matchSummary() {
            return new Builder();
        }

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder createdDate(long createdDate) {
            this.createdDate = createdDate;
            return this;
        }

        public Builder homeTeam(String homeTeam) {
            this.homeTeam = homeTeam;
            return this;
        }

        public Builder awayTeam(String awayTeam) {
            this.awayTeam = awayTeam;
            return this;
        }

        public Builder homeTeamScore(int homeTeamScore) {
            this.homeTeamScore = homeTeamScore;
            return this;
        }

        public Builder awayTeamScore(int awayTeamScore) {
            this.awayTeamScore = awayTeamScore;
            return this;
        }

        public MatchSummary build() {
            return new MatchSummary(this);
        }
    }
}
