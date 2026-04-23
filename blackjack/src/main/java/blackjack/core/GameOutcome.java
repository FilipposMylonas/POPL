package blackjack.core;

// A GameOutcome is one of:
//   - PlayerWins  (player's final hand beats the dealer's, or dealer busted)
//   - DealerWins  (dealer's final hand beats the player's, or player busted)
//   - Push        (both final hands have equal value; a tie)
// Interpretation: the final result of a single round of blackjack.
public sealed interface GameOutcome
        permits GameOutcome.PlayerWins, GameOutcome.DealerWins, GameOutcome.Push {

    record PlayerWins() implements GameOutcome {}
    record DealerWins() implements GameOutcome {}
    record Push()       implements GameOutcome {}
}
