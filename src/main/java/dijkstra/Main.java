package dijkstra;

import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) throws IOException {
        long readFile = System.nanoTime();
        ObjectMapper mapper = new ObjectMapper();
        String pathname = "src/main/resources/export.json";
        OsmFile file = OsmReader.readOsmFile(mapper, pathname);
        Map<Long, OsmElement> indexNodesById = OsmReader.indexNodesById(file);
        long msFile = (System.nanoTime() - readFile) / 1_000_000;
        System.out.println("чтение осм файла заняло: " + msFile + " мс");
        long graphBuild = System.nanoTime();
        Map<Long, Map<Long, Integer>> graph = OsmGraphBuilder.buildGraph(file);
        long msGraph = (System.nanoTime() - graphBuild) / 1_000_000;
        System.out.println("построение графа заняло: " + msGraph + " мс");
        List<String> names = new ArrayList<>(List.of("Kuźnice", "Świnica", "Orla Perć","Murowaniec" ,"Kuźnice"));
        List<Long> pathPoints = new ArrayList<>();
        long start = System.nanoTime();
        for (int i = 0; i < names.size(); i++) {
            Coordinates coordinates = PlaceFinder.findCoordinatesByName(names.get(i), file).get();
            pathPoints.add(PlaceFinder.nearestVertex(coordinates.lat(), coordinates.lon(), indexNodesById, graph).get());
        }
        Gpx.save(Gpx.build(RouteFinder.routeThrough(pathPoints, graph).get(), indexNodesById, names));
        long ms = (System.nanoTime() - start) / 1_000_000;
        System.out.println("заняло: " + ms + " мс");

    }
}
