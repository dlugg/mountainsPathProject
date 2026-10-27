package dijkstra;

import java.util.HashMap;
import java.util.Map;

public class DijkstraResult<T> {
    private final  Map<T,Integer> timeFromStart = new HashMap<>();
    private final Map<T,T> previous = new HashMap<>();

    public Map<T, Integer> getTimeFromStart() {
        return timeFromStart;
    }

    public Map<T, T> getPrevious() {
        return previous;
    }

    public void putTimeFromStart(T peak, Integer time) {
        timeFromStart.put(peak,time);
    }

    public void putPrevious(T peak, T fromWho) {
        previous.put(peak,fromWho);
    }

}
