package returnbasedstrategy;

import java.util.Objects;

public class CacheKey {
    private final GameState gameState;
    private final Hand hand;

    public CacheKey(GameState gameState, Hand hand) {
        this.gameState = gameState;
        this.hand = hand;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CacheKey cacheKey = (CacheKey) o;
        return gameState.equals(cacheKey.gameState) && hand.equals(cacheKey.hand);
    }

    @Override
    public int hashCode() {
        return Objects.hash(gameState, hand);
    }
}
