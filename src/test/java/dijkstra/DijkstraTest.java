package dijkstra;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DijkstraTest {
    @Test
    void dijkstraReturnCorrectValuesGraphABCD() {

        DijkstraResult dijkstraMethodResult = start.dijkstra("A",start.graphABCD());
        Map<String,Integer> expectedDijkstraResult = new HashMap<>();
        expectedDijkstraResult.put("A",0);
        expectedDijkstraResult.put("B",7);
        expectedDijkstraResult.put("C",3);
        expectedDijkstraResult.put("D",9);

        assertEquals(expectedDijkstraResult,dijkstraMethodResult.getPeakTimeFromStart());


    }
    @Test
    void dijkstraReturnCorrectValuesGraphKASP(){
       DijkstraResult dijkstraMethodResult = start.dijkstra("KUZ",start.graphKaspr());
        Map<String,Integer> expectedDijkstraResult = new HashMap<>();
        expectedDijkstraResult.put("KASP",210);
        expectedDijkstraResult.put("KUZ",0);
        expectedDijkstraResult.put("SG",283);
        expectedDijkstraResult.put("SWI",311);
        expectedDijkstraResult.put("KARB",241);
        expectedDijkstraResult.put("KOSC",287);
        expectedDijkstraResult.put("MUR",140);
        expectedDijkstraResult.put("CZS",191);
        expectedDijkstraResult.put("HALA",143);

        assertEquals(expectedDijkstraResult,dijkstraMethodResult.getPeakTimeFromStart());

    }

}
