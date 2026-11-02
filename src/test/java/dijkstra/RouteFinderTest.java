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

        assertEquals(expectedDijkstraResult, dijkstraMethodResult.getCostFromStart());


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

        assertEquals(expectedDijkstraResult, dijkstraMethodResult.getCostFromStart());
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

        assertEquals(expectedDijkstraResult, dijkstraMethodResult.getCostFromStart());

    }

    @Test
    void pathToReachablePointReturnsPath() {
        Optional<List<PathPoint<String>>> bestPathResult = RouteFinder.pathTo("D", RouteFinder.dijkstra("A", RouteFinder.graphABCD()));
        List<PathPoint> expectedList = new ArrayList<>(List.of(new PathPoint("A", 0), new PathPoint("C", 3),
                new PathPoint("B", 7), new PathPoint("D", 9)));
        assertEquals(expectedList, bestPathResult.get());
    }

    @Test
    void pathToUnreachablePointIsEmpty() {
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
        OsmFile file = RouteFinder.readOsmFile(mapper, pathname);
        Map<Long, Map<Long, Integer>> graph = RouteFinder.buildGraph(file);
        assertEquals(475, graph.size());
        Map<Long, Integer> expected = new HashMap<>();
        expected.put(4797284557L, 11);
        expected.put(10032256371L, 6);

        assertEquals(expected, graph.get(5715755082L));
    }

    @Test
    void dijkstraAtGraphFromOsmData() {
        ObjectMapper mapper = new ObjectMapper();
        String pathname = "src/main/resources/osm-murowaniec.json";
        OsmFile file = RouteFinder.readOsmFile(mapper, pathname);
        Map<Long, Map<Long, Integer>> graph = RouteFinder.buildGraph(file);
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
    void distanceBetweenTwoPointsMethodReturnCorrectDistance() {
        double lat1 = 49.2429441;
        double lat2 = 49.2429165;
        double lon1 = 20.0054246;
        double lon2 = 20.0053672;

        double result = RouteFinder.distanceMeters(lat1, lon1, lat2, lon2);
        assertEquals(5.18, result, 0.01);
    }

    @Test
    void graphEdgeWeightIsRoundedDistanceInMeters() {
        ObjectMapper mapper = new ObjectMapper();
        String pathname = "src/main/resources/osm-murowaniec.json";
        OsmFile file = RouteFinder.readOsmFile(mapper, pathname);
        Map<Long, Map<Long, Integer>> graph = RouteFinder.buildGraph(file);
        assertEquals(5, graph.get(355986496L).get(5715755056L));
    }

    @Test
    void closestPointToMurowaniec() {

        ObjectMapper mapper = new ObjectMapper();
        String pathname = "src/main/resources/osm-murowaniec.json";
        OsmFile file = RouteFinder.readOsmFile(mapper, pathname);
        Map<Long, OsmElement> indexNodesById = RouteFinder.indexNodesById(file);
        Map<Long, Map<Long, Integer>> graph = RouteFinder.buildGraph(file);
        assertEquals(5810136954L, RouteFinder.nearestVertex(49.2434181, 20.0071598, indexNodesById, graph).get());
    }

    @Test
    void routeFromAtoMurowaniecIsCorrect() {
        ObjectMapper mapper = new ObjectMapper();
        String pathname = "src/main/resources/osm-murowaniec.json";
        OsmFile file = RouteFinder.readOsmFile(mapper, pathname);
        Map<Long, Map<Long, Integer>> graph = RouteFinder.buildGraph(file);

        List<PathPoint<Long>> result = RouteFinder.pathTo(5810136954L, RouteFinder.dijkstra(355986496L, graph)).get();
        assertEquals(0, result.getFirst().getCost());
        assertEquals(153, result.getLast().getCost());
    }

    @Test
    void findCoordinatesByNameReturnsCorrectCoordinatesForExistingPlace() {
        ObjectMapper mapper = new ObjectMapper();
        String pathname = "src/main/resources/osm-murowaniec.json";
        OsmFile file = RouteFinder.readOsmFile(mapper, pathname);
        Optional<Coordinates> existingPlace = RouteFinder.findCoordinatesByName("Murowaniec", file);

        assertEquals(49.2434181, existingPlace.get().lat());
        assertEquals(20.0071598, existingPlace.get().lon());

    }

    @Test
    void findCoordinatesByNameReturnsEmptyForUnknownPlace() {
        ObjectMapper mapper = new ObjectMapper();
        String pathname = "src/main/resources/osm-murowaniec.json";
        OsmFile file = RouteFinder.readOsmFile(mapper, pathname);
        Optional<Coordinates> nonExistingPlace = RouteFinder.findCoordinatesByName("Warsaw", file);
        assertEquals(Optional.empty(), nonExistingPlace);
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
        Map<Long, Map<Long, Integer>> graph = RouteFinder.buildGraph(osmFile);
        DijkstraResult<Long> dijkstraMethodResult = RouteFinder.dijkstra(1L, graph);
        Optional<List<PathPoint<Long>>> result = RouteFinder.pathTo(2L, dijkstraMethodResult);
        assertEquals(73, result.get().getLast().getCost());
        DijkstraResult<Long> dijkstraMethodResult2 = RouteFinder.dijkstra(2L, graph);
        Optional<List<PathPoint<Long>>> result2 = RouteFinder.pathTo(1L, dijkstraMethodResult2);
        assertEquals(234, result2.get().getLast().getCost());
    }
}
