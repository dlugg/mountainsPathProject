package dijkstra;

import java.util.HashMap;
import java.util.Map;

public class DijkstraResult<T> {
    private final  Map<T,Integer> costFromStart = new HashMap<>();
    private final Map<T,T> previous = new HashMap<>();

    public Map<T, Integer> getCostFromStart() {
        return costFromStart;
    }

    public Map<T, T> getPrevious() {
        return previous;
    }

    public void putCostFromStart(T point, Integer time) {
        costFromStart.put(point,time);
    }

    public void putPrevious(T point, T previous) {
        this.previous.put(point,previous);
    }

}
