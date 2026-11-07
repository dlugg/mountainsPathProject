package dijkstra;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PlaceFinderTest {
    static OsmFile file;
    @BeforeAll
    static void osmFileRead() {
        ObjectMapper mapper = new ObjectMapper();
        String pathname = "src/test/resources/osm-murowaniec.json";
        file = OsmReader.readOsmFile(mapper, pathname);
    }
    @Test
    void closestPointToMurowaniec() {
        Map<Long, OsmElement> indexNodesById = OsmReader.indexNodesById(file);
        Map<Long, Map<Long, Integer>> graph = OsmGraphBuilder.buildGraph(file);
        assertEquals(5810136954L, PlaceFinder.nearestVertex(49.2434181, 20.0071598, indexNodesById, graph).get());
    }

    @Test
    void findCoordinatesByNameReturnsCorrectCoordinatesForExistingPlace() {
        Optional<Coordinates> existingPlace = PlaceFinder.findCoordinatesByName("Murowaniec", file);

        assertEquals(49.2434181, existingPlace.get().lat());
        assertEquals(20.0071598, existingPlace.get().lon());

    }

    @Test
    void findCoordinatesByNameReturnsEmptyForUnknownPlace() {
        Optional<Coordinates> nonExistingPlace = PlaceFinder.findCoordinatesByName("Warsaw", file);
        assertEquals(Optional.empty(), nonExistingPlace);
    }
}
