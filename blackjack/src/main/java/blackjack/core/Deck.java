package blackjack.core;

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

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
        // Template: flat-map every Suit over every rank [1..13], collect to
        // an immutable list, wrap in a Deck.
        List<Card> all = Arrays.stream(Suit.values())
                .flatMap(s -> IntStream.rangeClosed(1, 13)
                        .mapToObj(r -> new Card(s, r)))
                .toList();
        return new Deck(all);
    }
}
