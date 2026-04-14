# ChatterBot 

A Markov Chain-based text generator that learns from social media posts and produces new, plausible-sounding output.

## How It Works

The program follows a four-stage pipeline:

1. **CSV Parsing** — Reads training data from a CSV file, handling quoted fields and commas within fields.
2. **Post Parsing** — Cleans raw posts by removing URLs and tokenizing text into words and punctuation.
3. **Markov Chain Training** — Builds a probability model by recording which tokens follow which other tokens (bigrams) across all training posts.
4. **Post Generation** — Walks through the trained Markov Chain, picking tokens according to their frequency distributions, and concatenates them with appropriate spacing into a generated post.

## File Overview

| File | Description |
|------|-------------|
| `FileUtilities.java` | Static methods for reading files into `BufferedReader` and writing strings to files. |
| `LineIterator.java` | An `Iterator<String>` wrapper around `BufferedReader` for line-by-line file reading. |
| `CSV.java` | Parses CSV records, extracts columns, and reads column data from a `BufferedReader`. |
| `PostParser.java` | Tokenizes and cleans raw post strings (provided, not modified). |
| `MarkovChain.java` | Stores bigram frequency data and start token distributions. Handles training via `addBigram` and `addSequence`. |
| `MarkovChainIterator.java` | Implements `Iterator<String>` to walk through the Markov Chain using a `NumberGenerator` for path selection. |
| `ChatterBot.java` | Uses a trained `MarkovChain` to generate formatted posts with proper spacing and punctuation. |
| `ChatterBotMain.java` | Entry point that ties the full pipeline together and prints generated posts (provided, not modified). |
| `ProbabilityDistribution.java` | Tracks frequency counts and supports weighted random selection (provided, not modified). |
| `NumberGenerator.java` | Interface for producing numbers used during chain walks (provided, not modified). |
| `RandomNumberGenerator.java` | Produces random numbers for non-deterministic walks (provided, not modified). |
| `ListNumberGenerator.java` | Produces predetermined numbers for deterministic testing (provided, not modified). |

## Running the ChatterBot

Open `ChatterBotMain.java` and run it. By default it uses `files/illustrative_example.csv`. To try other data sources, change `PATH_TO_POSTS` and `POST_COLUMN` at the top of the file.

## Testing

Test files are located alongside the source and cover each major component:

- `LineIteratorTest.java` — Tests for null input, empty readers, single/multiple lines, and `NoSuchElementException`.
- `MarkovChainTest.java` — Tests for bigram addition, sequence training, the illustrative example chain, and deterministic walks.
- `ChatterBotTest.java` — Tests for empty training data, punctuation spacing, single-word posts, and longer walks.
- `CSVTest.java` and `FileUtilitiesTest.java` — Provided tests for CSV parsing and file I/O.

## Key Design Decisions

- **Read-ahead pattern**: Both `LineIterator` and `MarkovChainIterator` use the same strategy of always having the next value ready before `hasNext()`/`next()` are called. This keeps the `hasNext()` check simple and avoids advancing the iterator on a check.
- **Defensive exception handling**: The `MarkovChainIterator` catches exceptions during construction and during `next()` to gracefully handle empty chains or invalid number generator output, rather than crashing.
- **Static utility classes**: `FileUtilities` and `CSV` use only static methods since they hold no state — they are pure utility classes.