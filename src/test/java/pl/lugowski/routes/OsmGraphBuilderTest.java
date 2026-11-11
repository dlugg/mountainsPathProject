package pl.lugowski.routes;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import pl.lugowski.routes.osm.OsmElement;
import pl.lugowski.routes.osm.OsmFile;
import pl.lugowski.routes.osm.OsmGraphBuilder;
import pl.lugowski.routes.osm.OsmReader;
import pl.lugowski.routes.routing.DijkstraResult;
import pl.lugowski.routes.routing.PathPoint;
import pl.lugowski.routes.routing.RouteFinder;
import tools.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class OsmGraphBuilderTest {
    static OsmFile file;
    static Map<Long, Double> elevations;

    @BeforeAll
    static void osmFileRead() {
        ObjectMapper mapper = new ObjectMapper();
        String osmPath = "src/test/resources/osm-murowaniec.json";
        file = OsmReader.readOsmFile(mapper, osmPath);
        String elevationPath = "src/test/resources/elevation-murowaniec.json";
        elevations = OsmReader.readElevationFile(mapper, elevationPath);
    }

    @Test
    void graphBuilderReturnCorrectGraphFromOsmFile() {

        Map<Long, Map<Long, Integer>> graph = OsmGraphBuilder.buildGraph(file, elevations,4);
        assertEquals(475, graph.size());
        Map<Long, Integer> expected = new HashMap<>();
        expected.put(4797284557L, 11);
        expected.put(10032256371L, 6);

        assertEquals(expected, graph.get(5715755082L));
    }

    @Test
    void onewayTrailIsRoutedOnlyForward() {
        Map<Long, Double> elevations = new HashMap<>();
        elevations.put(1L, 0.);
        elevations.put(2L, 0.);
        elevations.put(3L, 0.);
        OsmElement p1 = new OsmElement("node", 1L, 49.000, 20.000, null, null, null);
        OsmElement p2 = new OsmElement("node", 2L, 49.000, 20.001, null, null, null);
        OsmElement p3 = new OsmElement("node", 3L, 49.001, 20.0005, null, null, null);

        OsmElement pathA = new OsmElement("way", 100L, null, null,
                List.of(1L, 2L),
                Map.of("highway", "path", "oneway", "yes"),
                null);

        OsmElement pathB = new OsmElement("way", 200L, null, null,
                List.of(2L, 3L, 1L),
                Map.of("highway", "path"),
                null);

        OsmFile osmFile = new OsmFile(List.of(p1, p2, p3, pathA, pathB));
        Map<Long, Map<Long, Integer>> graph = OsmGraphBuilder.buildGraph(osmFile, elevations,4);
        DijkstraResult<Long> dijkstraMethodResult = RouteFinder.dijkstra(1L, graph);
        Optional<List<PathPoint<Long>>> result = RouteFinder.pathTo(2L, dijkstraMethodResult);
        assertEquals(66, result.get().getLast().getCost());
        DijkstraResult<Long> dijkstraMethodResult2 = RouteFinder.dijkstra(2L, graph);
        Optional<List<PathPoint<Long>>> result2 = RouteFinder.pathTo(1L, dijkstraMethodResult2);
        assertEquals(210, result2.get().getLast().getCost());
    }

    @Test
    void uphillEdgeTakesLongerThanDownhill()
    {
        Map<Long, Double> elevations = new HashMap<>();
        elevations.put(1L, 1000.);
        elevations.put(2L, 1050.);
        elevations.put(3L, 0.);
        OsmElement p1 = new OsmElement("node", 1L, 49.000, 20.000, null, null, null);
        OsmElement p2 = new OsmElement("node", 2L, 49.000, 20.001, null, null, null);
        OsmElement pathA = new OsmElement("way", 100L, null, null,

                List.of(1L, 2L),
                Map.of("highway", "path"),
                null);

        OsmElement pathB = new OsmElement("way", 200L, null, null,
                List.of(2L, 1L),
                Map.of("highway", "path"),
                null);
        OsmFile osmFile = new OsmFile(List.of(p1, p2, pathA, pathB));
        Map<Long, Map<Long, Integer>> graph = OsmGraphBuilder.buildGraph(osmFile, elevations,4);
        DijkstraResult<Long> dijkstraMethodResult = RouteFinder.dijkstra(1L, graph);
        Optional<List<PathPoint<Long>>> result = RouteFinder.pathTo(2L, dijkstraMethodResult);
        assertEquals(516, result.get().getLast().getCost());
        DijkstraResult<Long> dijkstraMethodResult2 = RouteFinder.dijkstra(2L, graph);
        Optional<List<PathPoint<Long>>> result2 = RouteFinder.pathTo(1L, dijkstraMethodResult2);
        assertEquals(376, result2.get().getLast().getCost());
    }

    @Test
    void graphEdgeWeightIsRoundedDistanceInMeters() {
        Map<Long, Map<Long, Integer>> graph = OsmGraphBuilder.buildGraph(file, elevations,4);
        assertEquals(10, graph.get(355986496L).get(5715755056L));
    }

}
