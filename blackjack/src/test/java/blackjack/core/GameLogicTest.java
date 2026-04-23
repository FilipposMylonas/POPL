package blackjack.core;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GameLogicTest {

    // ----- cardValue -----

    @Test
    void cardValue_aceIsOne() {
        assertEquals(1, GameLogic.cardValue(Card.EXAMPLE_ACE));
    }

    @Test
    void cardValue_faceCardIsTen() {
        assertEquals(10, GameLogic.cardValue(Card.EXAMPLE_KING));
    }

    @Test
    void cardValue_pipCardIsItsRank() {
        assertEquals(5, GameLogic.cardValue(Card.EXAMPLE_FIVE));
    }

    // ----- handValue -----

    @Test
    void handValue_emptyHandIsZero() {
        assertEquals(0, GameLogic.handValue(Hand.EMPTY));
    }

    @Test
    void handValue_aceCountsAs11WhenNoBust() {
        // Ace + 5 -> 11 + 5 = 16 (Ace promoted)
        Hand h = new Hand(List.of(Card.EXAMPLE_ACE, Card.EXAMPLE_FIVE));
        assertEquals(16, GameLogic.handValue(h));
    }

    @Test
    void handValue_aceCountsAs1WhenElse11Busts() {
        // Ace + King + 5 -> 1 + 10 + 5 = 16 (Ace stays at 1, promoting it would bust)
        Hand h = new Hand(List.of(Card.EXAMPLE_ACE, Card.EXAMPLE_KING, Card.EXAMPLE_FIVE));
        assertEquals(16, GameLogic.handValue(h));
    }

    @Test
    void handValue_twoAcesOnePromoted() {
        // Ace + Ace + 9 -> one Ace as 11, one as 1 -> 11 + 1 + 9 = 21
        Hand h = new Hand(List.of(
                Card.EXAMPLE_ACE,
                new Card(Suit.DIAMONDS, 1),
                new Card(Suit.CLUBS, 9)));
        assertEquals(21, GameLogic.handValue(h));
    }

    // ----- isBust -----

    @Test
    void isBust_trueWhenOver21() {
        // King + King + 5 = 25
        Hand h = new Hand(List.of(Card.EXAMPLE_KING, Card.EXAMPLE_KING, Card.EXAMPLE_FIVE));
        assertTrue(GameLogic.isBust(h));
    }

    @Test
    void isBust_falseWhenExactly21() {
        // King + Ace = 10 + 11 = 21 (natural blackjack)
        Hand h = new Hand(List.of(Card.EXAMPLE_KING, Card.EXAMPLE_ACE));
        assertFalse(GameLogic.isBust(h));
    }

    // ----- addCardToHand -----

    @Test
    void addCardToHand_returnsHandWithAppendedCard() {
        Hand h = GameLogic.addCardToHand(Hand.EMPTY, Card.EXAMPLE_FIVE);
        assertEquals(List.of(Card.EXAMPLE_FIVE), h.cards());
    }

    @Test
    void addCardToHand_originalHandUnchanged() {
        Hand original = Hand.EMPTY;
        GameLogic.addCardToHand(original, Card.EXAMPLE_FIVE);
        assertEquals(0, original.cards().size());
    }

    // ----- determineOutcome -----

    @Test
    void determineOutcome_playerBustIsDealerWin() {
        Hand p = new Hand(List.of(Card.EXAMPLE_KING, Card.EXAMPLE_KING, Card.EXAMPLE_FIVE)); // 25
        Hand d = new Hand(List.of(Card.EXAMPLE_FIVE));
        assertInstanceOf(GameOutcome.DealerWins.class,
                GameLogic.determineOutcome(p, d));
    }

    @Test
    void determineOutcome_dealerBustIsPlayerWin() {
        Hand p = new Hand(List.of(Card.EXAMPLE_FIVE));
        Hand d = new Hand(List.of(Card.EXAMPLE_KING, Card.EXAMPLE_KING, Card.EXAMPLE_FIVE)); // 25
        assertInstanceOf(GameOutcome.PlayerWins.class,
                GameLogic.determineOutcome(p, d));
    }

    @Test
    void determineOutcome_higherHandWins() {
        Hand p = new Hand(List.of(Card.EXAMPLE_KING, Card.EXAMPLE_ACE));   // 21
        Hand d = new Hand(List.of(Card.EXAMPLE_KING, Card.EXAMPLE_FIVE));  // 15
        assertInstanceOf(GameOutcome.PlayerWins.class,
                GameLogic.determineOutcome(p, d));
    }

    @Test
    void determineOutcome_equalHandsIsPush() {
        Hand p = new Hand(List.of(Card.EXAMPLE_KING, Card.EXAMPLE_FIVE));  // 15
        Hand d = new Hand(List.of(Card.EXAMPLE_FIVE, Card.EXAMPLE_KING));  // 15
        assertInstanceOf(GameOutcome.Push.class,
                GameLogic.determineOutcome(p, d));
    }

    // ----- dealerShouldHit -----

    @Test
    void dealerShouldHit_trueBelow17() {
        Hand d = new Hand(List.of(Card.EXAMPLE_KING, Card.EXAMPLE_FIVE)); // 15
        assertTrue(GameLogic.dealerShouldHit(d));
    }

    @Test
    void dealerShouldHit_falseAt17() {
        Hand d = new Hand(List.of(Card.EXAMPLE_KING, new Card(Suit.HEARTS, 7))); // 17
        assertFalse(GameLogic.dealerShouldHit(d));
    }

    // ----- initialDeal -----

    @Test
    void initialDeal_dealsTwoCardsToEach() {
        GameState s = GameLogic.initialDeal(Deck.fullDeck());
        assertEquals(2, s.playerHand().cards().size());
        assertEquals(2, s.dealerHand().cards().size());
        assertEquals(52 - 4, s.remainingDeck().cards().size());
    }
}
