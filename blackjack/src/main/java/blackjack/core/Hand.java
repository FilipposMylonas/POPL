package blackjack.core;

import java.util.List;

// a Hand is a List<Card>.
// all cards held by one participant (player or dealer) during a single round.
// order is preserved but doesnt affect scoring.
public record Hand(List<Card> cards) {

    // defensive copy so the hand cant be mutated through an outside reference
    public Hand {
        cards = List.copyOf(cards);
    }

    // a hand with no cards
    public static final Hand EMPTY = new Hand(List.of());
}
