package returnbasedstrategy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

class Hand {
    private final List<Card> cards;
    private final boolean splitted;

    public Hand() {
        this.cards = new ArrayList<>();
        this.splitted = false;
    }

    public Hand(boolean splitted) {
        this.cards = new ArrayList<>();
        this.splitted = splitted;
    }

    public Hand(List<Card> cards, boolean splitted) {
        this.splitted = splitted;
        this.cards = new ArrayList<>(cards);
    }

    public void addCard(Card card) {
        cards.add(card);
    }

    public List<Card> getCards() {
        return Collections.unmodifiableList(cards);
    }

    public boolean isSplitted() {
        return splitted;
    }

    public boolean isPair() {
        return cards.size() == 2 && cards.get(0).getValue() == cards.get(1).getValue();
    }

    public boolean isSoft() {
        return cards.stream().anyMatch(c -> c.getValue() == Card.ACE) && getHardValue() <= 10;
    }

    public int getHardValue() {
        return cards.stream().mapToInt(Card::getValue).sum();
    }

    public int getBestValue() {
        int value = getHardValue();
        int aces = (int) cards.stream().filter(c -> c.getValue() == Card.ACE).count();

        while (value > 21 && aces > 0) {
            value -= 10;
            aces--;
        }

        return value;
    }

    @Override
    public String toString() {
        String cards = this.cards.stream()
                .map(Card::toString)
                .collect(Collectors.joining(" "));
        return cards + " (" + getBestValue() + ")";
    }
}

