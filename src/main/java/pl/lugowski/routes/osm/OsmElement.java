package pl.lugowski.routes.osm;

import pl.lugowski.routes.geo.Coordinates;

import java.util.List;
import java.util.Map;

public record OsmElement(String type, long id, Double lat, Double lon, List<Long> nodes, Map<String, String> tags,
                         Coordinates center) {
    public boolean isTrail() {
        if (tags() != null) {
            return tags().get("highway") != null;
        }
        return false;
    }

    public boolean isNode() {
        return type().equals("node");
    }

    public Coordinates coordinates() {
        if (this.isNode()) {
            return new Coordinates(lat, lon);
        } else {
            return center;
        }
    }

    boolean isOneWay() {
        if (tags() != null) {
            return "yes".equals(tags.get("oneway"));
        }
        return false;
    }
}