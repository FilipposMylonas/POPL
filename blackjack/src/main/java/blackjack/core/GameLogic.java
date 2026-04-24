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
     */
    public static int cardValue(Card card) {
        // Template: switch on rank.
        //   1          -> Ace (base value 1)
        //   11, 12, 13 -> Jack/Queen/King (10)
        //   default    -> numeric pip value
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
     */
    public static int handValue(Hand hand) {
        // Template: fold over cards (base total), then apply Ace-promotion rule.
        //   base  = sum of cardValue(c) for every c in hand
        //   aces  = count of Aces in hand
        //   value = base, then for each Ace: if +10 still <= 21, promote it.
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
     */
    public static boolean isBust(Hand hand) {
        // Template: compute handValue, compare against the bust threshold (21).
        return handValue(hand) > 21;
    }

    /**
     * Hand, Card -> Hand
     * Returns a new Hand containing every card of the input Hand plus the
     * given Card appended at the end. The input Hand is NOT modified.
     */
    public static Hand addCardToHand(Hand hand, Card card) {
        // Template: concatenate (old cards) ++ (new card), freeze as immutable.
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
     */
    public static GameState dealCard(GameState state, boolean toPlayer) {
        // Template: split deck into (top, rest); produce new GameState
        //   if toPlayer -> add top to playerHand, keep dealerHand
        //   else        -> add top to dealerHand, keep playerHand
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
     */
    public static GameOutcome determineOutcome(Hand playerHand, Hand dealerHand) {
        // Template: check busts first (they override raw totals), then compare.
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
     */
    public static boolean dealerShouldHit(Hand dealerHand) {
        // Template: compute handValue, compare against the dealer-stand
        // threshold (17): hit iff strictly below it.
        return handValue(dealerHand) < 17;
    }

    /**
     * Deck -> GameState
     * Returns the GameState at the start of a round: two cards dealt to the
     * player and two to the dealer, taken from the top of the given deck in
     * the standard alternating order (player, dealer, player, dealer).
     * Precondition: the deck has at least 4 cards.
     */
    public static GameState initialDeal(Deck deck) {
        // Template: start from empty hands + full deck, then thread 4 deals.
        GameState s = new GameState(Hand.EMPTY, Hand.EMPTY, deck);
        s = dealCard(s, true);
        s = dealCard(s, false);
        s = dealCard(s, true);
        s = dealCard(s, false);
        return s;
    }
}
