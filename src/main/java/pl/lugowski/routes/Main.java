package pl.lugowski.routes;

import pl.lugowski.routes.geo.Coordinates;
import pl.lugowski.routes.geo.Geo;
import pl.lugowski.routes.osm.OsmElement;
import pl.lugowski.routes.osm.OsmFile;
import pl.lugowski.routes.osm.OsmGraphBuilder;
import pl.lugowski.routes.osm.OsmReader;
import pl.lugowski.routes.output.DisplayFormat;
import pl.lugowski.routes.output.Gpx;
import pl.lugowski.routes.places.PlaceFinder;
import pl.lugowski.routes.routing.PathPoint;
import pl.lugowski.routes.routing.RouteFinder;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.*;

public class Main {
    public static void main(String[] args) throws IOException {
        List<String> names = new ArrayList<>(List.of("Kuźnice", "Murowaniec", "Czarny Staw Gąsienicowy", "Kasprowy Wierch", "Kuźnice"));

        ObjectMapper mapper = new ObjectMapper();
        String osmFilePath = "src/main/resources/export.json";
        String elevationFilePath = "src/main/resources/elevation.json";

        Map<Long, Double> elevations = OsmReader.readElevationFile(mapper, elevationFilePath);
        OsmFile file = OsmReader.readOsmFile(mapper, osmFilePath);

        Map<Long, OsmElement> indexNodesById = OsmReader.indexNodesById(file);

        Map<Long, Map<Long, Integer>> graph = OsmGraphBuilder.buildGraph(file, elevations, 6.5);
        List<PathPoint<Long>> route;
        List<Long> pathPoints = new ArrayList<>();
        for (int i = 0; i < names.size(); i++) {
            Optional<Coordinates> coordinates = PlaceFinder.findCoordinatesByName(names.get(i), file);
            if (coordinates.isPresent()) {
                Optional<Long> nearestVertex = PlaceFinder.nearestVertex(coordinates.get().lat(), coordinates.get().lon(), indexNodesById, graph);
                if (nearestVertex.isPresent()) {
                    pathPoints.add(nearestVertex.get());
                }else {
                    System.out.println("No trails in the data");
                    return;
                }
            } else {
                System.out.println("Place by name: " + names.get(i) + " is not found");
                return;
            }
        }
        Optional<List<PathPoint<Long>>> routeThrough = RouteFinder.routeThrough(pathPoints, graph);
        if (routeThrough.isPresent()) {
            route = routeThrough.get();
        } else {
            System.out.println("No route is found");
            return;
        }
        Gpx.save(Gpx.build(RouteFinder.routeLegs(pathPoints,graph).get(), indexNodesById, names));
        System.out.println("Route from " + names.getFirst() + " to " + names.getLast() + " is");
        System.out.println(DisplayFormat.kilometers(Geo.routeLengthMeters(route, indexNodesById)));
        System.out.println(DisplayFormat.duration(route.getLast().getCost()));

    }
}
