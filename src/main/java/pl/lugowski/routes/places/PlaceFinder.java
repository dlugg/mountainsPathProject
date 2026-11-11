package pl.lugowski.routes.places;

import pl.lugowski.routes.geo.Coordinates;
import pl.lugowski.routes.geo.Geo;
import pl.lugowski.routes.osm.OsmElement;
import pl.lugowski.routes.osm.OsmFile;

import java.util.Map;
import java.util.Optional;

public class PlaceFinder {
    public static Optional<Coordinates> findCoordinatesByName(String place, OsmFile file) {
        for (int i = 0; i < file.elements().size(); i++) {
            OsmElement element = file.elements().get(i);

            if (element.tags() != null) {
                if (place.equals(element.tags().get("name")) || place.equals(element.tags().get("name:pl"))) {
                    return Optional.of(element.coordinates());
                }
            }
        }
        return Optional.empty();
    }

    public static Optional<Long> nearestVertex(double lat, double lon, Map<Long, OsmElement> indexNodesById, Map<Long, Map<Long, Integer>> graph) {
        double closestPointDistance = Double.MAX_VALUE;
        long closestPointId = 0;


        for (Map.Entry<Long, Map<Long, Integer>> entry : graph.entrySet()) {
            OsmElement element = indexNodesById.get(entry.getKey());
            long currentId = element.id();
            double currentDistance = Geo.distanceMeters(lat, lon, element.lat(), element.lon());
            if (closestPointDistance > currentDistance) {
                closestPointDistance = currentDistance;
                closestPointId = currentId;
            }
        }
        if (closestPointId == 0) {
            return Optional.empty();
        } else {
            return Optional.of(closestPointId);
        }
    }
}
