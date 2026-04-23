package blackjack;

import blackjack.core.Card;
import blackjack.core.Deck;
import blackjack.core.GameLogic;
import blackjack.core.GameOutcome;
import blackjack.core.GameState;
import blackjack.core.Hand;
import blackjack.core.Suit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

// Main is the Imperative Shell.
// This is the ONLY file in the project permitted to:
//   - read from System.in (via Scanner)
//   - write to System.out
//   - use randomness (Collections.shuffle)
//   - use mutable collections (ArrayList, as a short-lived local builder)
// It drives the game loop by calling pure functions on GameLogic.
public final class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("=== Blackjack ===");

        boolean playing = true;
        while (playing) {
            GameState state = GameLogic.initialDeal(shuffledDeck());
            state = playRound(state, scanner);

            GameOutcome outcome = GameLogic.determineOutcome(
                    state.playerHand(), state.dealerHand());
            System.out.println(describeOutcome(outcome));

            System.out.print("Play again? (y/n): ");
            String again = scanner.nextLine().trim().toLowerCase();
            playing = again.startsWith("y");
        }
        System.out.println("Thanks for playing!");
    }

    // Returns a freshly shuffled full deck.
    // Allowed here (and nowhere else) to use ArrayList + Collections.shuffle.
    private static Deck shuffledDeck() {
        List<Card> mutable = new ArrayList<>(Deck.fullDeck().cards());
        Collections.shuffle(mutable);
        return new Deck(mutable);
    }

    // Runs one round: shows the opening hands, runs the player's turn, then
    // (if the player did not bust) runs the dealer's turn. Returns the
    // final GameState so Main can compute the outcome.
    private static GameState playRound(GameState initial, Scanner scanner) {
        System.out.println();
        System.out.println("Dealer shows: " + describeCard(initial.dealerHand().cards().get(0)));
        printHand("Your hand ", initial.playerHand());

        GameState state = playerTurn(initial, scanner);

        if (GameLogic.isBust(state.playerHand())) {
            System.out.println("You bust!");
            return state;
        }

        System.out.println();
        printHand("Dealer's hand", state.dealerHand());
        return dealerTurn(state);
    }

    private static GameState playerTurn(GameState start, Scanner scanner) {
        GameState state = start;
        while (!GameLogic.isBust(state.playerHand())) {
            System.out.print("Hit or stand? (h/s): ");
            String input = scanner.nextLine().trim().toLowerCase();
            if (input.startsWith("h")) {
                state = GameLogic.dealCard(state, true);
                printHand("Your hand ", state.playerHand());
            } else if (input.startsWith("s")) {
                return state;
            } else {
                System.out.println("Please enter 'h' or 's'.");
            }
        }
        return state;
    }

    private static GameState dealerTurn(GameState start) {
        GameState state = start;
        while (GameLogic.dealerShouldHit(state.dealerHand())) {
            state = GameLogic.dealCard(state, false);
            printHand("Dealer draws", state.dealerHand());
        }
        return state;
    }

    private static void printHand(String label, Hand hand) {
        StringBuilder sb = new StringBuilder(label).append(": ");
        List<Card> cs = hand.cards();
        for (int i = 0; i < cs.size(); i++) {
            if (i > 0) sb.append(", ");
            sb.append(describeCard(cs.get(i)));
        }
        sb.append("  (value = ").append(GameLogic.handValue(hand)).append(")");
        System.out.println(sb);
    }

    private static String describeCard(Card c) {
        String rank = switch (c.rank()) {
            case 1 -> "A";
            case 11 -> "J";
            case 12 -> "Q";
            case 13 -> "K";
            default -> String.valueOf(c.rank());
        };
        return rank + " of " + describeSuit(c.suit());
    }

    private static String describeSuit(Suit s) {
        return switch (s) {
            case CLUBS    -> "Clubs";
            case DIAMONDS -> "Diamonds";
            case HEARTS   -> "Hearts";
            case SPADES   -> "Spades";
        };
    }

    private static String describeOutcome(GameOutcome outcome) {
        return switch (outcome) {
            case GameOutcome.PlayerWins pw -> "You win!";
            case GameOutcome.DealerWins dw -> "Dealer wins.";
            case GameOutcome.Push       p  -> "Push - it's a tie.";
        };
    }
}
