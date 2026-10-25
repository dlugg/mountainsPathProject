package dijkstra;

import java.util.HashMap;
import java.util.Map;

public class DijkstraResult {
    private final  Map<String,Integer> timeFromStart = new HashMap<>();
    private final Map<String,String> previous = new HashMap<>();

    public Map<String, Integer> getTimeFromStart() {
        return timeFromStart;
    }

    public Map<String, String> getPrevious() {
        return previous;
    }

    public void putTimeFromStart(String peak, Integer time) {
        timeFromStart.put(peak,time);
    }

    public void putPrevious(String peak, String fromWho) {
        previous.put(peak,fromWho);
    }

}
