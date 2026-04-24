package blackjack.core;

// a GameState is a (Hand playerHand, Hand dealerHand, Deck remainingDeck).
// snapshot of one round at a single moment. never mutated — every transition
// returns a new GameState.
public record GameState(Hand playerHand, Hand dealerHand, Deck remainingDeck) {}
