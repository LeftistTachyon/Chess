package Engine;

import java.util.ArrayList;
import java.util.List;

public class Database {
    
    private final List<PositionStats> whitePositions = new ArrayList<>();
    private final List<PositionStats> blackPositions = new ArrayList<>();
    
    public Database() {
        
    }
    
    public void addWhitePosition(String position, int depth, int score) {
        whitePositions.add(new PositionStats(position, depth, score));
    }
    
    public void addBlackPosition(String position, int depth, int score) {
        blackPositions.add(new PositionStats(position, depth, score));
    }
    
    public String getWhitePositionStats(String position, int depth) {
        for (int index = 0, size = whitePositions.size(); index != size; ++index) {
            PositionStats savedData = whitePositions.get(index);
            if (position.equals(savedData.position) && depth == savedData.depth) {
                return Integer.toString(savedData.value);
            }
        }
        return null;
    }
    
    public String getBlackPositionStats(String position, int depth) {
        for (int index = 0, size = blackPositions.size(); index != size; ++index) {
            PositionStats savedData = blackPositions.get(index);
            if (position.equals(savedData.position) && depth == savedData.depth) {
                return Integer.toString(savedData.value);
            }
        }
        return null;
    }
    
    private class PositionStats {
        
        private String position;
        private int depth;
        private int value;
        
        private PositionStats(String encodedPosition, int depthSearched, int score) {
            position = encodedPosition;
            depth = depthSearched;
            value = score;
        }
    }
}