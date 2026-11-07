package dijkstra;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GeoTest {
    @Test
    void distanceBetweenTwoPointsMethodReturnCorrectDistance() {
        double lat1 = 49.2429441;
        double lat2 = 49.2429165;
        double lon1 = 20.0054246;
        double lon2 = 20.0053672;

        double result = Geo.distanceMeters(lat1, lon1, lat2, lon2);
        assertEquals(5.18, result, 0.01);
    }

    @Test
    void munterTimeDependsAtElevation(){
        int horizontal = Geo.munterTime(100, 1000., 1000., 4);
        int elevationGain = Geo.munterTime(100, 1000., 1100., 4);
        int elevationLoss = Geo.munterTime(100, 1100., 1000., 4);

        assertEquals(90,horizontal);
        assertEquals(990,elevationGain);
        assertEquals(540,elevationLoss);
    }
}
