package dijkstra;

import tools.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class Main {
    public static void main(String[] args) throws IOException {
        List<String> names = new ArrayList<>(List.of("Kuźnice", "MurowaniecD", "Czarny Staw Gąsienicowy", "Kasprowy Wierch", "Kuźnice"));

        ObjectMapper mapper = new ObjectMapper();
        String osmFilePath = "src/main/resources/export.json";
        String elevationFilePath = "src/main/resources/elevation.json";

        Map<Long, Double> elevations = OsmReader.readElevationFile(mapper, elevationFilePath);
        OsmFile file = OsmReader.readOsmFile(mapper, osmFilePath);

        Map<Long, OsmElement> indexNodesById = OsmReader.indexNodesById(file);

        Map<Long, Map<Long, Integer>> graph = OsmGraphBuilder.buildGraph(file, elevations, 6.5);
        List<PathPoint<Long>> route = new ArrayList<>();
        List<Long> pathPoints = new ArrayList<>();
        for (int i = 0; i < names.size(); i++) {
            Coordinates coordinates = PlaceFinder.findCoordinatesByName(names.get(i), file).get();
            pathPoints.add(PlaceFinder.nearestVertex(coordinates.lat(), coordinates.lon(), indexNodesById, graph).get());
        }
        if (RouteFinder.routeThrough(pathPoints, graph).isPresent()) {
            route = RouteFinder.routeThrough(pathPoints, graph).get();
        } else {
            System.out.println("No route is found");
        }
        Gpx.save(Gpx.build(route, indexNodesById, names));
        System.out.println(Gpx.routeLengthMeters(route, indexNodesById) + " km\n" + Gpx.formatDuration(route.getLast().getCost()));

    }
}
