package pl.lugowski.routes;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.lugowski.routes.osm.OsmElement;
import pl.lugowski.routes.output.DisplayFormat;
import pl.lugowski.routes.output.Gpx;
import pl.lugowski.routes.routing.PathPoint;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GpxTest {

    static List<String> names = new ArrayList<>();
    static Map<Long, OsmElement> idNode = new HashMap<>();
    static List<PathPoint<Long>> pathPointList = new ArrayList<>();
    static String xml;

    @BeforeEach
    void createTestData() {

        names = List.of("Kuźnice", "Murowaniec");
        OsmElement p1 = new OsmElement("node", 1L, 49.2432731, 20.0069321, null, null, null);
        OsmElement p2 = new OsmElement("node", 2L, 49.023239, 20.3323211, null, null, null);
        OsmElement p3 = new OsmElement("node", 3L, 49.123235701, 20.21112545, null, null, null);
        idNode = new HashMap<>();
        idNode.put(1L, p1);
        idNode.put(2L, p2);
        idNode.put(3L, p3);
        pathPointList = new ArrayList<>(List.of(
                new PathPoint<>(1L, 0),
                new PathPoint<>(2L, 10),
                new PathPoint<>(3L, 6300)));
        List<List<PathPoint<Long>>> legs = new ArrayList<>(List.of(pathPointList));
        xml = Gpx.build(legs, idNode, names);
    }

    @Test
    void buildWritesOneTrackPointPerRoutePoint() {
        assertEquals(3, xml.split("<trkpt").length - 1);
    }

    @Test
    void buildWritesCoordinatesWithDecimalDot() {
        assertTrue(xml.contains("lat=\"49.2432731\" lon=\"20.0069321\""));
    }

    @Test
    void buildNamesTrackAfterPlacesWithTime() {
        assertTrue(xml.contains("<name>Kuźnice - Murowaniec " + "(1 h 45 min)</name>"));
    }

    @Test
    void buildContainesCoordinatesOfAllPoints(){
        assertTrue(xml.contains("lat=\"49.2432731\""));
        assertTrue(xml.contains("lat=\"49.023239\""));
        assertTrue(xml.contains("lat=\"49.123235701\""));

    }
}
