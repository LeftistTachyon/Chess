package Engine;

import java.util.HashMap;
import java.util.Map;

//used to save evaluation scores of positions, to avoid redudant recalculations
public final class Database {

    private final Map<Grid, Integer> SAVED_POSITION_VALUES = new HashMap<>(1000);

    public Database() {

    }

    public void putEntry(Grid grid, int score) {
        SAVED_POSITION_VALUES.put(grid, score);
    }

    public boolean containsEntry(Grid grid) {
        return SAVED_POSITION_VALUES.containsKey(grid);
    }

    public int getValue(Grid grid) {
        return SAVED_POSITION_VALUES.get(grid);
    }
    
    public int size() {
        return SAVED_POSITION_VALUES.size();
    }
    
    public void clear() {
        SAVED_POSITION_VALUES.clear();
    }
}