package dijkstra;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class RouteFinderTest {
    static OsmFile file;

    @BeforeAll
    static void osmFileRead() {
        ObjectMapper mapper = new ObjectMapper();
        String pathname = "src/test/resources/osm-murowaniec.json";
        file = OsmReader.readOsmFile(mapper, pathname);
    }

    @Test
    void dijkstraReturnCorrectValuesGraphABCD() {

        DijkstraResult<String> dijkstraMethodResult = RouteFinder.dijkstra("A", TestGraphs.graphABCD());
        Map<String, Integer> expectedDijkstraResult = new HashMap<>();
        expectedDijkstraResult.put("A", 0);
        expectedDijkstraResult.put("B", 7);
        expectedDijkstraResult.put("C", 3);
        expectedDijkstraResult.put("D", 9);

        assertEquals(expectedDijkstraResult, dijkstraMethodResult.getCostFromStart());
    }

    @Test
    void dijkstraReturnCorrectValuesGraphKASP() {
        DijkstraResult<String> dijkstraMethodResult = RouteFinder.dijkstra("KUZ", TestGraphs.graphKaspr());
        Map<String, Integer> expectedDijkstraResult = new HashMap<>();
        expectedDijkstraResult.put("KASP", 210);
        expectedDijkstraResult.put("KUZ", 0);
        expectedDijkstraResult.put("SG", 283);
        expectedDijkstraResult.put("SWI", 311);
        expectedDijkstraResult.put("KARB", 241);
        expectedDijkstraResult.put("KOSC", 287);
        expectedDijkstraResult.put("MUR", 140);
        expectedDijkstraResult.put("CZS", 191);
        expectedDijkstraResult.put("HALA", 143);

        assertEquals(expectedDijkstraResult, dijkstraMethodResult.getCostFromStart());
    }

    @Test
    void dijkstraHandlesPointWithoutOutgoingTrails() {
        Map<String, Map<String, Integer>> graph = TestGraphs.graphKaspr();
        graph.get("KASP").put("TOP", 5);

        DijkstraResult<String> dijkstraMethodResult = RouteFinder.dijkstra("KUZ", graph);
        Map<String, Integer> expectedDijkstraResult = new HashMap<>();
        expectedDijkstraResult.put("KASP", 210);
        expectedDijkstraResult.put("KUZ", 0);
        expectedDijkstraResult.put("SG", 283);
        expectedDijkstraResult.put("SWI", 311);
        expectedDijkstraResult.put("KARB", 241);
        expectedDijkstraResult.put("KOSC", 287);
        expectedDijkstraResult.put("MUR", 140);
        expectedDijkstraResult.put("CZS", 191);
        expectedDijkstraResult.put("HALA", 143);
        expectedDijkstraResult.put("TOP", 215);

        assertEquals(expectedDijkstraResult, dijkstraMethodResult.getCostFromStart());
    }

    @Test
    void pathToReachablePointReturnsPath() {
        Optional<List<PathPoint<String>>> bestPathResult = RouteFinder.pathTo("D", RouteFinder.dijkstra("A", TestGraphs.graphABCD()));
        List<PathPoint> expectedList = new ArrayList<>(List.of(
                new PathPoint("A", 0), new PathPoint("C", 3),
                new PathPoint("B", 7), new PathPoint("D", 9)));
        assertEquals(expectedList, bestPathResult.get());
    }

    @Test
    void pathToUnreachablePointIsEmpty() {
        Map<String, Map<String, Integer>> graph = TestGraphs.graphKaspr();
        Map<String, Integer> neighborsOfPark = new HashMap<>();
        neighborsOfPark.put("KUZ", 10);
        graph.put("PARK", neighborsOfPark);
        Optional<List<PathPoint<String>>> result = RouteFinder.pathTo("PARK", RouteFinder.dijkstra("KUZ", graph));
        assertTrue(result.isEmpty());
    }

    @Test
    void mandatoryPointsRouteReturnsGluedPath() {
        List<String> listOfPoints = new ArrayList<>(List.of("KUZ", "CZS", "KASP", "KUZ"));
        List<PathPoint> expectedList = new ArrayList<>(List.of(
                new PathPoint("KUZ", 0),
                new PathPoint("MUR", 140), new PathPoint("CZS", 191),
                new PathPoint("MUR", 226), new PathPoint("HALA", 229),
                new PathPoint("KASP", 326), new PathPoint("KUZ", 465)));
        Optional<List<PathPoint<String>>> methodResult = RouteFinder.routeThrough(listOfPoints, TestGraphs.graphKaspr());
        assertEquals(expectedList, methodResult.get());
    }

    @Test
    void mandatoryPointsRouteThroughUnreachablePointIsEmpty() {
        Map<String, Map<String, Integer>> graph = TestGraphs.graphKaspr();
        Map<String, Integer> neighborsOfPark = new HashMap<>();
        neighborsOfPark.put("KUZ", 10);
        graph.put("PARK", neighborsOfPark);
        List<String> listOfPoints = new ArrayList<>(List.of("KUZ", "PARK", "KUZ"));
        Optional<List<PathPoint<String>>> methodResult = RouteFinder.routeThrough(listOfPoints, graph);
        assertTrue(methodResult.isEmpty());
    }

    @Test
    void dijkstraAtGraphFromOsmData() {
        Map<Long, Map<Long, Integer>> graph = OsmGraphBuilder.buildGraph(file);
        Optional<List<PathPoint<Long>>> result = RouteFinder.pathTo(10032256371L, RouteFinder.dijkstra(355986496L, graph));
        List<PathPoint<Long>> expectedResult = new ArrayList<>(List.of(
                new PathPoint<>(355986496L, 0),
                new PathPoint<>(5715755056L, 5),
                new PathPoint<>(4797284557L, 27),
                new PathPoint<>(5715755082L, 38),
                new PathPoint<>(10032256371L, 44)));
        assertEquals(expectedResult, result.get());
    }

    @Test
    void routeFromAtoMurowaniecIsCorrect() {
        Map<Long, Map<Long, Integer>> graph = OsmGraphBuilder.buildGraph(file);
        List<PathPoint<Long>> result = RouteFinder.pathTo(5810136954L, RouteFinder.dijkstra(355986496L, graph)).get();
        assertEquals(0, result.getFirst().getCost());
        assertEquals(153, result.getLast().getCost());
    }


}
