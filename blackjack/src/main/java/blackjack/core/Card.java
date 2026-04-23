package blackjack.core;

// A Card is a (Suit, int) where int is in [1..13].
// Interpretation: a standard playing card. Rank 1 = Ace, 2..10 = pip cards,
// 11 = Jack, 12 = Queen, 13 = King. Ranks outside [1..13] are not valid cards.
public record Card(Suit suit, int rank) {

    // Example instances (used in tests and for readability):
    public static final Card EXAMPLE_ACE  = new Card(Suit.SPADES, 1);
    public static final Card EXAMPLE_KING = new Card(Suit.HEARTS, 13);
    public static final Card EXAMPLE_FIVE = new Card(Suit.CLUBS, 5);
}
