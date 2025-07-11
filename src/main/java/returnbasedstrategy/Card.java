package returnbasedstrategy;

import java.util.Objects;

class Card {
    public static final int ACE = 11;
    public static final int TEN = 10;

    private final int value;

    public Card(int value) {
        this.value = value;
    }

    public int getValue() { return value; }

    public boolean isTen() {
        return value == TEN || value == 11; // Jack, Queen, King
    }

    @Override
    public String toString() {
        switch(value) {
            case 11: return "A";
            case 10: return "T";
            default: return String.valueOf(value);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Card card = (Card) o;
        return value == card.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
