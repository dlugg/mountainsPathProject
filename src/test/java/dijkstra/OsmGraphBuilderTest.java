package dijkstra;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class OsmGraphBuilderTest {
    static OsmFile file;
    @BeforeAll
    static void osmFileRead() {
        ObjectMapper mapper = new ObjectMapper();
        String pathname = "src/test/resources/osm-murowaniec.json";
        file = OsmReader.readOsmFile(mapper, pathname);
    }
    @Test
    void graphBuilderReturnCorrectGraphFromOsmFile() {
        Map<Long, Map<Long, Integer>> graph = OsmGraphBuilder.buildGraph(file);
        assertEquals(475, graph.size());
        Map<Long, Integer> expected = new HashMap<>();
        expected.put(4797284557L, 11);
        expected.put(10032256371L, 6);

        assertEquals(expected, graph.get(5715755082L));
    }

    @Test
    void onewayTrailIsRoutedOnlyForward() {
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
        Map<Long, Map<Long, Integer>> graph = OsmGraphBuilder.buildGraph(osmFile);
        DijkstraResult<Long> dijkstraMethodResult = RouteFinder.dijkstra(1L, graph);
        Optional<List<PathPoint<Long>>> result = RouteFinder.pathTo(2L, dijkstraMethodResult);
        assertEquals(73, result.get().getLast().getCost());
        DijkstraResult<Long> dijkstraMethodResult2 = RouteFinder.dijkstra(2L, graph);
        Optional<List<PathPoint<Long>>> result2 = RouteFinder.pathTo(1L, dijkstraMethodResult2);
        assertEquals(234, result2.get().getLast().getCost());
    }

    @Test
    void graphEdgeWeightIsRoundedDistanceInMeters() {
        Map<Long, Map<Long, Integer>> graph = OsmGraphBuilder.buildGraph(file);
        assertEquals(5, graph.get(355986496L).get(5715755056L));
    }

}
