package returnbasedstrategy;

import java.util.Collections;
import java.util.List;

class GameState {
    private final Card dealerUpCard;
    private final List<Card> seenCards;
    private final int decks;

    public GameState(Card dealerUpCard, List<Card> seenCards, int decks) {
        this.dealerUpCard = dealerUpCard;
        this.seenCards = Collections.unmodifiableList(seenCards);
        this.decks = decks;
    }

    public List<Card> getSeenCards() { return seenCards; }
    public int getDecks() { return decks; }

    public Card getDealerUpCard() {
        return dealerUpCard;
    }

    @Override
    public String toString() {
        return "GameState{" +
                "dealerUpCard=" + dealerUpCard +
                ", seenCards=" + seenCards +
                '}';
    }
}

