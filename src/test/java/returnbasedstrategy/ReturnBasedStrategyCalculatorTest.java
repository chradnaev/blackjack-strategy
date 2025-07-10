package returnbasedstrategy;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class ReturnBasedStrategyCalculatorTest {

    @Test
    void simpleCase() {
        // Create a game state
        List<Card> seenCards = new ArrayList<>();
        seenCards.add(new Card(10)); // Dealer up card
        seenCards.add(new Card(8));  // Player card 1
        seenCards.add(new Card(8));  // Player card 2

        Hand playerHand = new Hand();
        playerHand.addCard(new Card(8));
        playerHand.addCard(new Card(8));

        Hand dealerHand = new Hand();
        dealerHand.addCard(new Card(10));
        dealerHand.addCard(new Card(2)); // Hole card

        GameState state = new GameState(
                Collections.singletonList(playerHand),
                dealerHand,
                seenCards,
                6 // decks
        );

        ReturnBasedStrategyCalculator decider = new ReturnBasedStrategyCalculator(new ProbabilityCalculator());
        DecisionType action = decider.recommendAction(state, playerHand);
        System.out.println("Recommended action: " + action);
    }
}