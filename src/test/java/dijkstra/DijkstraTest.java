package dijkstra;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

    @Test
    void pathWithMandatoryPointsReturnCorrectValues(){
        List<String> listOfPoints = new ArrayList<>(List.of("KUZ","CZS","KASP","KUZ"));
        List<pathPoint> expectedList = new ArrayList<>(List.of(new pathPoint("KUZ",0),new pathPoint("MUR",140), new pathPoint("CZS" ,191),
                new pathPoint("MUR", 226), new pathPoint("HALA", 229), new pathPoint("KASP", 326), new pathPoint("KUZ", 465)));
        List<pathPoint> methodResult = start.pathWithMandatoryPoints(listOfPoints);
        assertEquals(expectedList, methodResult );
    }

}
