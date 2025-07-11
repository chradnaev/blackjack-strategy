package returnbasedstrategy;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ReturnBasedStrategyCalculatorTest {
    private final ReturnBasedStrategyCalculator decider = new ReturnBasedStrategyCalculator(new ProbabilityCalculator(),
            2.5);

    @Test
    public void playerBlackjack() {
        DecisionType decisionType = makeDecision(5, 11, 6);

        assertEquals(DecisionType.STAND, decisionType);
    }

    @Test
    public void playerSoft17() {
        DecisionType decisionType = makeDecision(5, 11, 6);

        assertEquals(DecisionType.HIT, decisionType);
    }

    @Test
    public void player20() {
        DecisionType decisionType = makeDecision(5, 10, 9);

        assertEquals(DecisionType.STAND, decisionType);
    }

    private DecisionType makeDecision(int dealerCardValue, int... playerCardValues) {
        Hand hand = buildHand(playerCardValues);
        GameState state = buildState(dealerCardValue, hand);
        return decider.recommendAction(state, hand);
    }

    private GameState buildState(int dealerCardValue, Hand playerHand) {
        Card dealerCard = new Card(dealerCardValue);
        List<Card> seenCards = new ArrayList<>();
        seenCards.add(dealerCard);
        seenCards.addAll(playerHand.getCards());
        return new GameState(dealerCard, seenCards, 6);
    }

    private Hand buildHand(int... playerCardValues) {
        List<Card> playerCards = IntStream.of(playerCardValues)
                .mapToObj(Card::new)
                .collect(Collectors.toList());
        return new Hand(playerCards, false);
    }
}