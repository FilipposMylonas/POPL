package blackjack.core;

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

// a Deck is a List<Card>.
// the undealt cards. index 0 is the top of the deck (drawn next).
public record Deck(List<Card> cards) {

    // defensive copy to an immutable list
    public Deck {
        cards = List.copyOf(cards);
    }

    // () -> Deck
    // a full 52-card deck in canonical (unshuffled) order: one card per
    // (suit, rank) pair for rank [1..13]. shuffling happens in the shell.
    // examples: see tests.
    public static Deck fullDeck() {
        // template (constructor for Deck):
        //   new Deck(<List<Card>>)
        // where Suit is one of { CLUBS, DIAMONDS, HEARTS, SPADES }
        // and rank is int [1..13].
        List<Card> all = Arrays.stream(Suit.values())
                .flatMap(s -> IntStream.rangeClosed(1, 13)
                        .mapToObj(r -> new Card(s, r)))
                .toList();
        return new Deck(all);
    }
}
