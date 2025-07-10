package returnbasedstrategy;

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
}
