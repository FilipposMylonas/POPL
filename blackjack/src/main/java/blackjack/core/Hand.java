package blackjack.core;

import java.util.List;

// A Hand is a List<Card>.
// Interpretation: all cards currently held by one participant (player or dealer)
// during a single round of blackjack. Order is preserved (the order in which
// the cards were dealt), but is not significant for scoring.
public record Hand(List<Card> cards) {

    // Compact constructor: defensively copy to an immutable list so the
    // Hand cannot be mutated after construction, even if the caller holds
    // a reference to the original list.
    public Hand {
        cards = List.copyOf(cards);
    }

    // Example instance: a hand with no cards yet.
    public static final Hand EMPTY = new Hand(List.of());
}
