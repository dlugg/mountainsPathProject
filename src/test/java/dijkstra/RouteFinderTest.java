package dijkstra;

import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class RouteFinderTest {

    @Test
    void dijkstraReturnCorrectValuesGraphABCD() {

        DijkstraResult dijkstraMethodResult = RouteFinder.dijkstra("A", RouteFinder.graphABCD());
        Map<String, Integer> expectedDijkstraResult = new HashMap<>();
        expectedDijkstraResult.put("A", 0);
        expectedDijkstraResult.put("B", 7);
        expectedDijkstraResult.put("C", 3);
        expectedDijkstraResult.put("D", 9);

        assertEquals(expectedDijkstraResult, dijkstraMethodResult.getTimeFromStart());


    }

    @Test
    void dijkstraReturnCorrectValuesGraphKASP() {
        DijkstraResult dijkstraMethodResult = RouteFinder.dijkstra("KUZ", RouteFinder.graphKaspr());
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

        assertEquals(expectedDijkstraResult, dijkstraMethodResult.getTimeFromStart());
    }

    @Test
    void dijkstraHandlesPointWithoutOutgoingTrails() {
        Map<String, Map<String, Integer>> graph = RouteFinder.graphKaspr();
        graph.get("KASP").put("TOP", 5);

        DijkstraResult dijkstraMethodResult = RouteFinder.dijkstra("KUZ", graph);
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

        assertEquals(expectedDijkstraResult, dijkstraMethodResult.getTimeFromStart());

    }

    @Test
    void bestPathToReachablePointReturnsPath() {
        Optional<List<PathPoint<String>>> bestPathResult = RouteFinder.pathTo("D", RouteFinder.dijkstra("A", RouteFinder.graphABCD()));
        List<PathPoint> expectedList = new ArrayList<>(List.of(new PathPoint("A", 0), new PathPoint("C", 3),
                new PathPoint("B", 7), new PathPoint("D", 9)));
        assertEquals(expectedList, bestPathResult.get());
    }

    @Test
    void bestPathToUnreachablePointIsEmpty() {
        Map<String, Map<String, Integer>> graph = RouteFinder.graphKaspr();
        Map<String, Integer> neighborsOfPark = new HashMap<>();
        neighborsOfPark.put("KUZ", 10);
        graph.put("PARK", neighborsOfPark);
        Optional<List<PathPoint<String>>> result = RouteFinder.pathTo("PARK", RouteFinder.dijkstra("KUZ", graph));
        assertTrue(result.isEmpty());
    }

    @Test
    void mandatoryPointsRouteReturnsGluedPath() {
        List<String> listOfPoints = new ArrayList<>(List.of("KUZ", "CZS", "KASP", "KUZ"));
        List<PathPoint> expectedList = new ArrayList<>(List.of(new PathPoint("KUZ", 0), new PathPoint("MUR", 140), new PathPoint("CZS", 191),
                new PathPoint("MUR", 226), new PathPoint("HALA", 229), new PathPoint("KASP", 326), new PathPoint("KUZ", 465)));
        Optional<List<PathPoint<String>>> methodResult = RouteFinder.routeThrough(listOfPoints, RouteFinder.graphKaspr());
        assertEquals(expectedList, methodResult.get());
    }

    @Test
    void mandatoryPointsRouteThroughUnreachablePointIsEmpty() {
        Map<String, Map<String, Integer>> graph = RouteFinder.graphKaspr();
        Map<String, Integer> neighborsOfPark = new HashMap<>();
        neighborsOfPark.put("KUZ", 10);
        graph.put("PARK", neighborsOfPark);
        List<String> listOfPoints = new ArrayList<>(List.of("KUZ", "PARK", "KUZ"));
        Optional<List<PathPoint<String>>> methodResult = RouteFinder.routeThrough(listOfPoints, graph);
        assertTrue(methodResult.isEmpty());
    }

    @Test
    void graphBuilderReturnCorrectGraphFromOsmFile() {
        ObjectMapper mapper = new ObjectMapper();
        String pathname = "src/main/resources/osm-murowaniec.json";
        OsmFile file = RouteFinder.osmFileReader(mapper, pathname);
        Map<Long, Map<Long, Integer>> graph = RouteFinder.graphBuilderFromOsmFile(file);
        assertEquals(475, graph.size());
        Map<Long, Integer> expected = new HashMap<>();
        expected.put(4797284557L, 1);
        expected.put(10032256371L, 1);

        assertEquals(expected, graph.get(5715755082L));
    }

    @Test
    void dijkstraAtGraphFromOsmData() {
        ObjectMapper mapper = new ObjectMapper();
        String pathname = "src/main/resources/osm-murowaniec.json";
        OsmFile file = RouteFinder.osmFileReader(mapper, pathname);
        Map<Long, Map<Long, Integer>> graph = RouteFinder.graphBuilderFromOsmFile(file);
        Optional<List<PathPoint<Long>>> result = RouteFinder.pathTo(10032256371L, RouteFinder.dijkstra(355986496L, graph));
        List<PathPoint<Long>> expectedResult = new ArrayList<>(List.of(
                new PathPoint<>(355986496L, 0),
                new PathPoint<>(5715755056L, 1),
                new PathPoint<>(4797284557L, 2),
                new PathPoint<>(5715755082L, 3),
                new PathPoint<>(10032256371L, 4)));
        assertEquals(expectedResult, result.get());
    }

    @Test
    void distanceBetweenTwoPointsMethodReturnCorrectDistance(){
        double lat1 = 49.2429441;
        double lat2 = 49.2429165;
        double lon1 = 20.0054246;
        double lon2 = 20.0053672;

        double result = RouteFinder.distanceBetweenTwoPoints(lat1,lon1,lat2,lon2);
        assertEquals(5.18,result,0.01);
    }
}
