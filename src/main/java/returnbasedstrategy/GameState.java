package returnbasedstrategy;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

class GameState {
    private final List<Hand> playerHands;
    private final Hand dealerHand;
    private final List<Card> seenCards;
    private final int decks;
    private int hitDepth = 0;

    public GameState(List<Hand> playerHands, Hand dealerHand, List<Card> seenCards, int decks, int hitDepth) {
        this.playerHands = playerHands.stream()
                .map(hand -> new Hand(new ArrayList<>(hand.getCards()), hand.isSplitted()))
                .collect(Collectors.toList());
        this.dealerHand = new Hand(new ArrayList<>(dealerHand.getCards()), false);
        this.seenCards = new ArrayList<>(seenCards);
        this.decks = decks;
        this.hitDepth = hitDepth;
    }

    // Getters
    public List<Hand> getPlayerHands() { return playerHands; }
    public Hand getDealerHand() { return dealerHand; }
    public List<Card> getSeenCards() { return seenCards; }
    public int getDecks() { return decks; }

    public Card getDealerUpCard() {
        return dealerHand.getCards().get(0);
    }

    void markHitDone() {
        ++hitDepth;
    }

    public int getHitDepth() {
        return hitDepth;
    }
}

