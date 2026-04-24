package blackjack.core;

import java.util.List;
import java.util.stream.Stream;

// GameLogic is the functional core of the game.
// every method here is pure: same inputs -> same outputs, no I/O,
// no randomness, no mutation. all I/O lives in Main (the shell).
public final class GameLogic {

    // utility class, no instances
    private GameLogic() {}

    // Card -> int
    // point value of a single card: Ace = 1, face cards (J/Q/K) = 10,
    // otherwise the face number. ace-as-11 is handled in handValue, not here.
    // examples: see tests.
    public static int cardValue(Card card) {
        // template for Card:
        //   ... card.suit() ... card.rank() ...
        // (rank is int [1..13]; branch on 1 and 11/12/13)
        return switch (card.rank()) {
            case 1 -> 1;
            case 11, 12, 13 -> 10;
            default -> card.rank();
        };
    }

    // Hand -> int
    // best possible value of the hand: first sum with aces as 1,
    // then promote each ace from 1 to 11 while that keeps the total <= 21.
    // can still return > 21 if the hand already busted.
    // examples: see tests.
    public static int handValue(Hand hand) {
        // template for Hand:
        //   ... hand.cards() ...
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

    // Hand -> boolean
    // true if the hand value is over 21.
    // examples: see tests.
    public static boolean isBust(Hand hand) {
        // template for Hand:
        //   ... hand.cards() ...
        return handValue(hand) > 21;
    }

    // Hand, Card -> Hand
    // returns a new hand with the card appended. original is not modified.
    // examples: see tests.
    public static Hand addCardToHand(Hand hand, Card card) {
        // template for (Hand, Card):
        //   ... hand.cards() ... card.suit() ... card.rank() ...
        List<Card> newCards = Stream.concat(
                hand.cards().stream(),
                Stream.of(card)
        ).toList();
        return new Hand(newCards);
    }

    // GameState, boolean -> GameState
    // deals the top card to the player (true) or dealer (false).
    // input state is not modified. precondition: deck is non-empty.
    // examples: see tests.
    public static GameState dealCard(GameState state, boolean toPlayer) {
        // template for (GameState, boolean):
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

    // Hand, Hand -> GameOutcome
    // compares the player's final hand to the dealer's:
    //   player busts-> DealerWins
    //   dealer busts-> PlayerWins (and player didnt bust)
    //   higher total wins, equal = Push.
    // examples: see tests.
    public static GameOutcome determineOutcome(Hand playerHand, Hand dealerHand) {
        // template for (Hand, Hand):
        //   ... playerHand.cards() ... dealerHand.cards() ...
        // (result is GameOutcome: one of PlayerWins | DealerWins | Push)
        if (isBust(playerHand)) return new GameOutcome.DealerWins();
        if (isBust(dealerHand)) return new GameOutcome.PlayerWins();

        int p = handValue(playerHand);
        int d = handValue(dealerHand);
        if (p > d) return new GameOutcome.PlayerWins();
        if (d > p) return new GameOutcome.DealerWins();
        return new GameOutcome.Push();
    }

    // Hand -> boolean
    // true iff the dealer must draw: hits below 17, stands at 17+.
    // examples: see tests.
    public static boolean dealerShouldHit(Hand dealerHand) {
        // template for Hand:
        //   ... dealerHand.cards() ...
        return handValue(dealerHand) < 17;
    }

    // Deck -> GameState
    // game state at the start of a round: two cards each to player and dealer,
    // in alternating order (player, dealer, player, dealer).
    // precondition: deck has at least 4 cards.
    // examples: see tests.
    public static GameState initialDeal(Deck deck) {
        // template for Deck:
        //   ... deck.cards() ...
        GameState s = new GameState(Hand.EMPTY, Hand.EMPTY, deck);
        s = dealCard(s, true);
        s = dealCard(s, false);
        s = dealCard(s, true);
        s = dealCard(s, false);
        return s;
    }
}
