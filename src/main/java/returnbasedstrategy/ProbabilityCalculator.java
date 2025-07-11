package returnbasedstrategy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProbabilityCalculator {

    public Map<Integer, Double> getCardProbabilities(int decks, List<Card> seenCards) {
        Map<Integer, Integer> remainingCards = new HashMap<>();

        // Initialize with full deck counts
        for (int i = 2; i <= 11; i++) {
            int count = (i == 10) ? 4 * 4 * decks : 4 * decks; // 10,J,Q,K count as 10
            remainingCards.put(i, count);
        }

        // Subtract seen cards
        for (Card card : seenCards) {
            int value = card.getValue();
            remainingCards.put(value, remainingCards.get(value) - 1);
        }

        // Calculate probabilities
        int totalRemaining = remainingCards.values().stream()
                .mapToInt(Integer::intValue)
                .sum();
        Map<Integer, Double> probabilities = new HashMap<>();
        for (Map.Entry<Integer, Integer> entry : remainingCards.entrySet()) {
            probabilities.put(entry.getKey(), (double)entry.getValue() / totalRemaining);
        }

        return probabilities;
    }
}
