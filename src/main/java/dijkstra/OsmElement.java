package dijkstra;

import java.util.List;
import java.util.Map;

public record OsmElement(String type, long id, Double lat, Double lon, List<Long> nodes, Map<String, String> tags) {
    boolean isTrail() {
        if (tags() != null) {
            return tags().get("highway") != null;
        }
        return false;
    }
    boolean isNode(){
        return type().equals("node");
    }
}