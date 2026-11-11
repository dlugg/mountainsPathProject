package pl.lugowski.routes.osm;


import pl.lugowski.routes.geo.Geo;

import java.util.HashMap;
import java.util.Map;

public class OsmGraphBuilder {
    public static Map<Long, Map<Long, Integer>> buildGraph(OsmFile file, Map<Long,Double> elevations,double pace) {
        Map<Long, Map<Long, Integer>> graph = new HashMap<>();
        Map<Long, OsmElement> pointIdAndItCoordinates = OsmReader.indexNodesById(file);
        for (int i = 0; i < file.elements().size(); i++) {
            OsmElement element = file.elements().get(i);
            if (element.isTrail()) {
                for (int j = 0; j < element.nodes().size() - 1; j++) {
                    double lat1 = pointIdAndItCoordinates.get(element.nodes().get(j)).lat();
                    double lat2 = pointIdAndItCoordinates.get(element.nodes().get(j + 1)).lat();
                    double lon1 = pointIdAndItCoordinates.get(element.nodes().get(j)).lon();
                    double lon2 = pointIdAndItCoordinates.get(element.nodes().get(j + 1)).lon();
                    double distanceMeter = Geo.distanceMeters(lat1, lon1, lat2, lon2);
                    if (element.isOneWay()) {

                        graph.computeIfAbsent(element.nodes().get(j), k -> new HashMap<>()).put(element.nodes().get(j + 1),
                                Geo.munterTime(distanceMeter, elevations.get(element.nodes().get(j)), elevations.get(element.nodes().get(j + 1)), pace));
                    } else {
                        graph.computeIfAbsent(element.nodes().get(j), k -> new HashMap<>()).put(element.nodes().get(j + 1),
                                Geo.munterTime(distanceMeter, elevations.get(element.nodes().get(j)), elevations.get(element.nodes().get(j + 1)), pace));
                        graph.computeIfAbsent(element.nodes().get(j + 1), k -> new HashMap<>()).put(element.nodes().get(j),
                                Geo.munterTime(distanceMeter, elevations.get(element.nodes().get(j+1)), elevations.get(element.nodes().get(j)), pace));
                    }
                }
            }
        }
        return graph;
    }
}
