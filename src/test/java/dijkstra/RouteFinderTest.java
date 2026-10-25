package dijkstra;

import org.junit.jupiter.api.Test;

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
    void dijkstraHandlesPointWithoutOutgoingTrails(){
        Map<String, Map<String, Integer>> graph = RouteFinder.graphKaspr();
        graph.get("KASP").put("TOP",5);

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
        expectedDijkstraResult.put("TOP",215);

        assertEquals(expectedDijkstraResult, dijkstraMethodResult.getTimeFromStart());

    }

    @Test
    void bestPathToReachablePointReturnsPath() {
        Optional<List<PathPoint>> bestPathResult = RouteFinder.pathTo("D", RouteFinder.dijkstra("A", RouteFinder.graphABCD()));
        List<PathPoint> expectedList = new ArrayList<>(List.of(new PathPoint("A", 0), new PathPoint("C", 3),
                new PathPoint("B", 7), new PathPoint("D", 9)));
        assertEquals(expectedList, bestPathResult.get());
    }

    @Test
    void bestPathToUnreachablePointIsEmpty(){
        Map<String, Map<String, Integer>> graph = RouteFinder.graphKaspr();
        Map<String, Integer> neighborsOfPark = new HashMap<>();
        neighborsOfPark.put("KUZ", 10);
        graph.put("PARK", neighborsOfPark);
        Optional<List<PathPoint>> result = RouteFinder.pathTo("PARK", RouteFinder.dijkstra("KUZ", graph));
        assertTrue(result.isEmpty());
    }

    @Test
    void mandatoryPointsRouteReturnsGluedPath() {
        List<String> listOfPoints = new ArrayList<>(List.of("KUZ", "CZS","KASP", "KUZ"));
        List<PathPoint> expectedList = new ArrayList<>(List.of(new PathPoint("KUZ", 0), new PathPoint("MUR", 140), new PathPoint("CZS", 191),
                new PathPoint("MUR", 226), new PathPoint("HALA", 229), new PathPoint("KASP", 326), new PathPoint("KUZ", 465)));
        Optional<List<PathPoint>> methodResult = RouteFinder.routeThrough(listOfPoints, RouteFinder.graphKaspr());
        assertEquals(expectedList, methodResult.get());
    }

    @Test
    void mandatoryPointsRouteThroughUnreachablePointIsEmpty(){
        Map<String, Map<String, Integer>> graph = RouteFinder.graphKaspr();
        Map<String, Integer> neighborsOfPark = new HashMap<>();
        neighborsOfPark.put("KUZ", 10);
        graph.put("PARK", neighborsOfPark);
        List<String> listOfPoints = new ArrayList<>(List.of("KUZ", "PARK", "KUZ"));
        Optional<List<PathPoint>> methodResult = RouteFinder.routeThrough(listOfPoints, graph);
        assertTrue(methodResult.isEmpty());
    }

}
