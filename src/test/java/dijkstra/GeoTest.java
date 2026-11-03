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
}
