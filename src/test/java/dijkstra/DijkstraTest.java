package dijkstra;

import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class DijkstraTest {

    @Test
    void dijkstraReturnCorrectValuesGraphABCD() {

        DijkstraResult dijkstraMethodResult = start.dijkstra("A", start.graphABCD());
        Map<String, Integer> expectedDijkstraResult = new HashMap<>();
        expectedDijkstraResult.put("A", 0);
        expectedDijkstraResult.put("B", 7);
        expectedDijkstraResult.put("C", 3);
        expectedDijkstraResult.put("D", 9);

        assertEquals(expectedDijkstraResult, dijkstraMethodResult.getPeakTimeFromStart());


    }

    @Test
    void dijkstraReturnCorrectValuesGraphKASP() {
        DijkstraResult dijkstraMethodResult = start.dijkstra("KUZ", start.graphKaspr());
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

        assertEquals(expectedDijkstraResult, dijkstraMethodResult.getPeakTimeFromStart());
    }
    @Test
    void dijkstraHandlesPointWithoutOutgoingTrails(){
        Map<String, Map<String, Integer>> graph = start.graphKaspr();
        graph.get("KASP").put("TOP",5);

        DijkstraResult dijkstraMethodResult = start.dijkstra("KUZ", graph);
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

        assertEquals(expectedDijkstraResult, dijkstraMethodResult.getPeakTimeFromStart());

    }

    @Test
    void bestPathToReachablePointReturnsPath() {
        Optional<List<pathPoint>> bestPathResult = start.bestPath("D", start.dijkstra("A", start.graphABCD()));
        List<pathPoint> expectedList = new ArrayList<>(List.of(new pathPoint("A", 0), new pathPoint("C", 3),
                new pathPoint("B", 7), new pathPoint("D", 9)));
        assertEquals(expectedList, bestPathResult.get());
    }

    @Test
    void bestPathToUnreachablePointIsEmpty(){
        Map<String, Map<String, Integer>> graph = start.graphKaspr();
        Map<String, Integer> neighborsOfPark = new HashMap<>();
        neighborsOfPark.put("KUZ", 10);
        graph.put("PARK", neighborsOfPark);
        Optional<List<pathPoint>> result = start.bestPath("PARK", start.dijkstra("KUZ", graph));
        assertTrue(result.isEmpty());
    }

    @Test
    void mandatoryPointsRouteReturnsGluedPath() {
        List<String> listOfPoints = new ArrayList<>(List.of("KUZ", "CZS","KASP", "KUZ"));
        List<pathPoint> expectedList = new ArrayList<>(List.of(new pathPoint("KUZ", 0), new pathPoint("MUR", 140), new pathPoint("CZS", 191),
                new pathPoint("MUR", 226), new pathPoint("HALA", 229), new pathPoint("KASP", 326), new pathPoint("KUZ", 465)));
        Optional<List<pathPoint>> methodResult = start.pathWithMandatoryPoints(listOfPoints, start.graphKaspr());
        assertEquals(expectedList, methodResult.get());
    }

    @Test
    void mandatoryPointsRouteThroughUnreachablePointIsEmpty(){
        Map<String, Map<String, Integer>> graph = start.graphKaspr();
        Map<String, Integer> neighborsOfPark = new HashMap<>();
        neighborsOfPark.put("KUZ", 10);
        graph.put("PARK", neighborsOfPark);
        List<String> listOfPoints = new ArrayList<>(List.of("KUZ", "PARK", "KUZ"));
        Optional<List<pathPoint>> methodResult = start.pathWithMandatoryPoints(listOfPoints, graph);
        assertTrue(methodResult.isEmpty());
    }

}
