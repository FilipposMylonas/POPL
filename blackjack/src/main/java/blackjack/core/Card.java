package blackjack.core;

// a Card is a (Suit, int) with rank in [1..13].
// rank 1 = Ace, 2..10 = pip cards, 11 = Jack, 12 = Queen, 13 = King.
// anything outside [1..13] isnt a valid card.
public record Card(Suit suit, int rank) {

    // example cards used in tests
    public static final Card EXAMPLE_ACE  = new Card(Suit.SPADES, 1);
    public static final Card EXAMPLE_KING = new Card(Suit.HEARTS, 13);
    public static final Card EXAMPLE_FIVE = new Card(Suit.CLUBS, 5);
}
