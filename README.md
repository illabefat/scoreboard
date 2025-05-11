# Live Football World Cup Scoreboard Library

## Overview

This library provides a simple implementation of a Live Football World Cup Scoreboard. The scoreboard keeps track of ongoing football matches, allows updating scores, and provides functionality to get a summary of matches ordered by total score and start time.

## Features

- Start a new match with initial score 0-0
- Update the score of an ongoing match
- Finish (remove) a match from the scoreboard
- Get a summary of matches in progress ordered by:
  1. Total score (descending)
  2. Most recently started (for matches with equal scores)

## Usage

```java
import org.ilzi.scorecard.service.MatchSummaryService;
import org.ilzi.scorecard.model.MatchSummary;

// Create a new scoreboard
MatchSummaryService scoreboard = new MatchSummaryService();

// Start new matches
String mexicoCanadaId = scoreboard.create("Mexico", "Canada");
String spainBrazilId = scoreboard.create("Spain", "Brazil");
String germanyFranceId = scoreboard.create("Germany", "France");
String uruguayItalyId = scoreboard.create("Uruguay", "Italy");
String argentinaAustraliaId = scoreboard.create("Argentina", "Australia");

// Update scores
scoreboard.updateScore(mexicoCanadaId, 0, 5);
scoreboard.updateScore(spainBrazilId, 10, 2);
scoreboard.updateScore(germanyFranceId, 2, 2);
scoreboard.updateScore(uruguayItalyId, 6, 6);
scoreboard.updateScore(argentinaAustraliaId, 3, 1);

// Get summary of matches in progress
List<MatchSummary> summary = scoreboard.getAll();
// Print summary
for (MatchSummary match : summary) {
    System.out.println(match);
}

// Finish a match
scoreboard.endMatch(mexicoCanadaId);
```

### Threading and Concurrency
The library uses a thread-safe implementation to ensure operations can be performed concurrently:
- Concurrent map for storing match data
- Proper synchronization for accessing the match collection
- Cached results for optimizing summary retrieval

### Performance Optimizations
The library is optimized for scenarios where reads are more frequent than writes:
- Match summaries are cached and only recalculated when data changes
- This makes it efficient for applications with high read-to-write ratios
- Ideal for scoreboard displays that are frequently viewed but less frequently updated
