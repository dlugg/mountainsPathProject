package dijkstra;

import java.util.HashMap;
import java.util.Map;

public class DijkstraResult {
    private final  Map<String,Integer> peakTimeFromStart = new HashMap<>();
    private final Map<String,String> peakFromWho = new HashMap<>();

    public Map<String, Integer> getPeakTimeFromStart() {
        return peakTimeFromStart;
    }

    public Map<String, String> getPeakFromWho() {
        return peakFromWho;
    }

    public void putPeakTimeFromStart(String peak, Integer time) {
        peakTimeFromStart.put(peak,time);
    }

    public void putPeakFromWho(String peak, String fromWho) {
        peakFromWho.put(peak,fromWho);
    }

}
