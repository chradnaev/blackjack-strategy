package returnbasedstrategy;

import java.util.*;

import static returnbasedstrategy.DecisionType.*;

public class ReturnBasedStrategyCalculator {

    private final ProbabilityCalculator probabilityCalculator;

    public ReturnBasedStrategyCalculator(ProbabilityCalculator probabilityCalculator) {
        this.probabilityCalculator = probabilityCalculator;
    }

    public DecisionType recommendAction(GameState gameState, Hand currentHand) {
        Map<Integer, Double> cardProbs = probabilityCalculator.getCardProbabilities(gameState.getDecks(), gameState.getSeenCards());
        Card dealerUpCard = gameState.getDealerUpCard();

        // Calculate expected values for each possible action
        double standEV = calculateStandEV(currentHand, dealerUpCard, cardProbs);
        double hitEV = canHit(currentHand) ? calculateHitEV(currentHand, dealerUpCard, cardProbs, gameState) : Double.NEGATIVE_INFINITY;
        double doubleEV = canDouble(currentHand) ? calculateDoubleEV(currentHand, dealerUpCard, cardProbs) : Double.NEGATIVE_INFINITY;
        double splitEV = canSplit(currentHand) ? calculateSplitEV(currentHand, dealerUpCard, cardProbs, gameState) : Double.NEGATIVE_INFINITY;

        // Find best action
        double maxEV = Math.max(Math.max(standEV, hitEV), Math.max(doubleEV, splitEV));

        if (maxEV == standEV) return STAND;
        if (maxEV == hitEV) return HIT;
        if (maxEV == doubleEV) return DOUBLE;
        return SPLIT;
    }

    private boolean canHit(Hand hand) {
        return hand.getBestValue() <= 17;
    }

    private boolean canSplit(Hand currentHand) {
        return currentHand.isPair() && !currentHand.isSplitted();
    }

    private boolean canDouble(Hand currentHand) {
        int hardValue = currentHand.getHardValue();
        return hardValue >= 9 && hardValue <= 11 && currentHand.getCards().size() == 2;
    }

    private double calculateStandEV(Hand hand, Card dealerCard, Map<Integer, Double> cardProbs) {
        int playerValue = hand.getBestValue();
        if (playerValue > 21) return -1; // Already bust

        double dealerBustProb = calculateDealerBustProbability(dealerCard, cardProbs);
        Map<Integer, Double> dealerFinalValueProb = calculateDealerFinalValueProbability(dealerCard, cardProbs);

        double ev = dealerBustProb; // Dealer busts, we win

        for (int dealerValue = 17; dealerValue <= 21; dealerValue++) {
            double prob = dealerFinalValueProb.getOrDefault(dealerValue, 0.0);
            if (playerValue > dealerValue) {
                ev += prob; // Win
            } else if (playerValue == dealerValue) {
                // Push (no change to EV)
            } else {
                ev -= prob; // Lose
            }
        }

        return ev;
    }

    private double calculateHitEV(Hand hand, Card dealerCard, Map<Integer, Double> cardProbs, GameState gameState) {
        double totalEV = 0;

        for (Map.Entry<Integer, Double> entry : cardProbs.entrySet()) {
            int cardValue = entry.getKey();
            double prob = entry.getValue();

            Hand newHand = new Hand(hand.getCards(), hand.isSplitted());
            newHand.addCard(new Card(cardValue));

            if (newHand.getBestValue() > 21) {
                totalEV += prob * -1; // Bust
            } else {
                // Recursively calculate best action after hit
                GameState newState = updateGameState(gameState, new Card(cardValue));
                DecisionType nextAction = recommendAction(newState, newHand);

                double nextEV;
                if (nextAction == STAND) {
                    nextEV = calculateStandEV(newHand, dealerCard, cardProbs);
                } else if (nextAction == DOUBLE) {
                    nextEV = calculateDoubleEV(newHand, dealerCard, cardProbs);
                } else { // H
                    nextEV = calculateHitEV(newHand, dealerCard, cardProbs, newState);
                }

                totalEV += prob * nextEV;
            }
        }

        return totalEV;
    }

    private double calculateDoubleEV(Hand hand, Card dealerCard, Map<Integer, Double> cardProbs) {
        double totalEV = 0;

        for (Map.Entry<Integer, Double> entry : cardProbs.entrySet()) {
            int cardValue = entry.getKey();
            double prob = entry.getValue();

            Hand newHand = new Hand(hand.getCards(), hand.isSplitted());
            newHand.addCard(new Card(cardValue));

            if (newHand.getBestValue() > 21) {
                totalEV += prob * -2; // Bust, lose double
            } else {
                double standEV = calculateStandEV(newHand, dealerCard, cardProbs);
                totalEV += prob * standEV * 2; // Double the win/loss
            }
        }

        return totalEV;
    }

    private double calculateSplitEV(Hand hand, Card dealerCard, Map<Integer, Double> cardProbs, GameState gameState) {
        int pairValue = hand.getCards().get(0).getValue();
        double totalEV = 0;

        // Calculate EV for each possible first card
        for (Map.Entry<Integer, Double> entry : cardProbs.entrySet()) {
            int card1 = entry.getKey();
            double prob1 = entry.getValue();

            // Calculate EV for each possible second card
            for (Map.Entry<Integer, Double> entry2 : cardProbs.entrySet()) {
                int card2 = entry2.getKey();
                double prob2 = entry2.getValue();

                // Create two new hands
                Hand hand1 = new Hand(true);
                hand1.addCard(new Card(pairValue));
                hand1.addCard(new Card(card1));

                Hand hand2 = new Hand(true);
                hand2.addCard(new Card(pairValue));
                hand2.addCard(new Card(card2));

                // Calculate EV for each hand
                GameState newState = updateGameState(gameState, new Card(card1), new Card(card2));
                double ev1 = calculateHandEV(hand1, dealerCard, cardProbs, newState);
                double ev2 = calculateHandEV(hand2, dealerCard, cardProbs, newState);

                totalEV += prob1 * prob2 * (ev1 + ev2);
            }
        }

        return totalEV;
    }

    private double calculateHandEV(Hand hand, Card dealerCard, Map<Integer, Double> cardProbs, GameState gameState) {
        DecisionType action = recommendAction(gameState, hand);

        switch(action) {
            case STAND: return calculateStandEV(hand, dealerCard, cardProbs);
            case HIT: return calculateHitEV(hand, dealerCard, cardProbs, gameState);
            case DOUBLE: return calculateDoubleEV(hand, dealerCard, cardProbs);
            default: return 0;
        }
    }

    private Map<Integer, Double> calculateDealerFinalValueProbability(Card upCard, Map<Integer, Double> cardProbs) {
        Map<Integer, Double> probabilities = new HashMap<>();
        calculateDealerOutcomes(upCard.getValue(), 0, 1.0, cardProbs, probabilities);
        return probabilities;
    }

    private void calculateDealerOutcomes(int currentValue, int numAces, double currentProb,
                                         Map<Integer, Double> cardProbs, Map<Integer, Double> result) {
        // Dealer stands on hard 17 or higher
        int hardValue = currentValue - (numAces * 10);
        if (hardValue >= 17) {
            // Dealer stands
            int finalValue = Math.min(22, currentValue); // Cap at 22 (bust)
            result.merge(finalValue, currentProb, Double::sum);
            return;
        }

        // Dealer must hit
        for (Map.Entry<Integer, Double> entry : cardProbs.entrySet()) {
            int cardValue = entry.getKey();
            double prob = entry.getValue();

            int newValue = currentValue + cardValue;
            int newAces = numAces + (cardValue == Card.ACE ? 1 : 0);

            // Handle soft totals
            while (newValue > 21 && newAces > 0) {
                newValue -= 10;
                newAces--;
            }

            if (newValue > 21) {
                // Dealer busts
                result.merge(22, currentProb * prob, Double::sum);
            } else {
                // Recurse
                calculateDealerOutcomes(newValue, newAces, currentProb * prob, cardProbs, result);
            }
        }
    }

    private double calculateDealerBustProbability(Card upCard, Map<Integer, Double> cardProbs) {
        Map<Integer, Double> finalProbs = calculateDealerFinalValueProbability(upCard, cardProbs);
        return finalProbs.getOrDefault(22, 0.0);
    }

    private GameState updateGameState(GameState current, Card... newCards) {
        List<Card> newSeenCards = new ArrayList<>(current.getSeenCards());
        Collections.addAll(newSeenCards, newCards);
        return new GameState(current.getPlayerHands(), current.getDealerHand(), newSeenCards, current.getDecks());
    }
}
