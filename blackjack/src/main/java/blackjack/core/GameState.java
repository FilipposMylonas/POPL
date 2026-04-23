package blackjack.core;

// A GameState is a (Hand playerHand, Hand dealerHand, Deck remainingDeck).
// Interpretation: a complete snapshot of one round of blackjack at a single
// moment in time. All game transitions (dealing a card, hitting, etc.) take
// a GameState in and produce a new GameState out; this record is never mutated.
public record GameState(Hand playerHand, Hand dealerHand, Deck remainingDeck) {}
