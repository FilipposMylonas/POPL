package blackjack.core;

import java.util.List;
import java.util.stream.Stream;

// GameLogic is the Functional Core of the blackjack game.
// Every method in this class is pure: given the same inputs it returns the
// same outputs, performs no I/O, uses no randomness, and does not mutate its
// arguments or any shared state. All I/O lives in Main (the Imperative Shell).
public final class GameLogic {

    // Utility class: no instances.
    private GameLogic() {}

    /**
     * Card -> int
     * Returns the point value of a single card, using the base Blackjack scale:
     * Ace = 1, face cards (J/Q/K) = 10, everything else = its face number.
     * Ace flexibility (counting as 11) is NOT applied here; it is handled at
     * the Hand level by {@link #handValue(Hand)}.
     * Examples: see tests.
     */
    public static int cardValue(Card card) {
        // Template for Card:
        //   ... card.suit() ... card.rank() ...
        // (rank is int [1..13]; branch on the special values 1 and 11/12/13)
        return switch (card.rank()) {
            case 1 -> 1;
            case 11, 12, 13 -> 10;
            default -> card.rank();
        };
    }

    /**
     * Hand -> int
     * Returns the best possible value of the hand: first sums every card with
     * Aces worth 1, then promotes each Ace from 1 to 11 as long as doing so
     * keeps the total at or below 21. The returned value may exceed 21 if the
     * hand has already busted.
     * Examples: see tests.
     */
    public static int handValue(Hand hand) {
        // Template for Hand:
        //   ... hand.cards() ...
        // (cards is a List<Card>; process each element with cardValue)
        int base = hand.cards().stream().mapToInt(GameLogic::cardValue).sum();
        long aces = hand.cards().stream().filter(c -> c.rank() == 1).count();

        int value = base;
        for (long i = 0; i < aces; i++) {
            if (value + 10 <= 21) {
                value += 10;
            }
        }
        return value;
    }

    /**
     * Hand -> boolean
     * Returns true if the hand's value exceeds 21 (a bust).
     * Examples: see tests.
     */
    public static boolean isBust(Hand hand) {
        // Template for Hand:
        //   ... hand.cards() ...
        return handValue(hand) > 21;
    }

    /**
     * Hand, Card -> Hand
     * Returns a new Hand containing every card of the input Hand plus the
     * given Card appended at the end. The input Hand is NOT modified.
     * Examples: see tests.
     */
    public static Hand addCardToHand(Hand hand, Card card) {
        // Template for (Hand, Card):
        //   ... hand.cards() ... card.suit() ... card.rank() ...
        List<Card> newCards = Stream.concat(
                hand.cards().stream(),
                Stream.of(card)
        ).toList();
        return new Hand(newCards);
    }

    /**
     * GameState, boolean -> GameState
     * Returns a new GameState in which the top card of the remaining deck has
     * been dealt to the player (toPlayer=true) or the dealer (toPlayer=false).
     * The input GameState is NOT modified.
     * Precondition: the remaining deck is non-empty.
     * Examples: see tests.
     */
    public static GameState dealCard(GameState state, boolean toPlayer) {
        // Template for (GameState, boolean):
        //   ... state.playerHand() ... state.dealerHand() ... state.remainingDeck() ...
        //   if (toPlayer) { ... } else { ... }
        List<Card> deckCards = state.remainingDeck().cards();
        Card top = deckCards.get(0);
        Deck newDeck = new Deck(deckCards.subList(1, deckCards.size()));

        if (toPlayer) {
            return new GameState(
                    addCardToHand(state.playerHand(), top),
                    state.dealerHand(),
                    newDeck);
        }
        return new GameState(
                state.playerHand(),
                addCardToHand(state.dealerHand(), top),
                newDeck);
    }

    /**
     * Hand, Hand -> GameOutcome
     * Returns the outcome of comparing the player's final hand to the dealer's:
     *   - player busts  -> DealerWins
     *   - dealer busts  -> PlayerWins (and player did not bust)
     *   - higher total wins; equal totals -> Push.
     * Examples: see tests.
     */
    public static GameOutcome determineOutcome(Hand playerHand, Hand dealerHand) {
        // Template for (Hand, Hand):
        //   ... playerHand.cards() ... dealerHand.cards() ...
        // (the result is a GameOutcome, one of PlayerWins | DealerWins | Push)
        if (isBust(playerHand)) return new GameOutcome.DealerWins();
        if (isBust(dealerHand)) return new GameOutcome.PlayerWins();

        int p = handValue(playerHand);
        int d = handValue(dealerHand);
        if (p > d) return new GameOutcome.PlayerWins();
        if (d > p) return new GameOutcome.DealerWins();
        return new GameOutcome.Push();
    }

    /**
     * Hand -> boolean
     * Returns true iff the dealer must draw another card under the standard
     * casino rule: the dealer hits on any total below 17 and stands otherwise.
     * Examples: see tests.
     */
    public static boolean dealerShouldHit(Hand dealerHand) {
        // Template for Hand:
        //   ... dealerHand.cards() ...
        return handValue(dealerHand) < 17;
    }

    /**
     * Deck -> GameState
     * Returns the GameState at the start of a round: two cards dealt to the
     * player and two to the dealer, taken from the top of the given deck in
     * the standard alternating order (player, dealer, player, dealer).
     * Precondition: the deck has at least 4 cards.
     * Examples: see tests.
     */
    public static GameState initialDeal(Deck deck) {
        // Template for Deck:
        //   ... deck.cards() ...
        GameState s = new GameState(Hand.EMPTY, Hand.EMPTY, deck);
        s = dealCard(s, true);
        s = dealCard(s, false);
        s = dealCard(s, true);
        s = dealCard(s, false);
        return s;
    }
}
