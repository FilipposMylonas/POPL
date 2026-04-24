package blackjack.core;

// a GameOutcome is one of:
//   - PlayerWins  (player beat the dealer, or the dealer busted)
//   - DealerWins  (dealer beat the player, or the player busted)
//   - Push        (tie)
// the final result of a single round.
public sealed interface GameOutcome
        permits GameOutcome.PlayerWins, GameOutcome.DealerWins, GameOutcome.Push {

    record PlayerWins() implements GameOutcome {}
    record DealerWins() implements GameOutcome {}
    record Push()       implements GameOutcome {}
}
