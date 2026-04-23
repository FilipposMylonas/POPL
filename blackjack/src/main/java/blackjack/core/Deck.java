package blackjack.core;

import java.util.ArrayList;
import java.util.List;

// A Deck is a List<Card>.
// Interpretation: the remaining undealt cards, where the card at index 0 is
// the "top" of the deck and will be drawn next.
public record Deck(List<Card> cards) {

    // Compact constructor: defensively copy to an immutable list.
    public Deck {
        cards = List.copyOf(cards);
    }

    /**
     * () -> Deck
     * Returns a full 52-card deck in a canonical (un-shuffled) order:
     * one card for every (suit, rank) pair with rank in [1..13].
     * The imperative shell is responsible for shuffling before play.
     */
    public static Deck fullDeck() {
        // Template: build all 52 (suit, rank) combinations, then freeze.
        List<Card> all = new ArrayList<>(52);
        for (Suit s : Suit.values()) {
            for (int r = 1; r <= 13; r++) {
                all.add(new Card(s, r));
            }
        }
        return new Deck(all);
    }
}
