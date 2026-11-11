package pl.lugowski.routes.osm;

import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class OsmReader {
    public static OsmFile readOsmFile(ObjectMapper mapper, String pathname) {
        return mapper.readValue(new File(pathname), OsmFile.class);
    }

    public static Map<Long, OsmElement> indexNodesById(OsmFile file) {
        Map<Long, OsmElement> pointIdAndItCoordinates = new HashMap<>();
        for (int i = 0; i < file.elements().size(); i++) {
            if (file.elements().get(i).isNode()) {
                pointIdAndItCoordinates.put(file.elements().get(i).id(), file.elements().get(i));
            }

        }
        return pointIdAndItCoordinates;
    }

    public static Map<Long,Double>  readElevationFile(ObjectMapper mapper, String pathname){
        Map<Long, Double> elevations = mapper.readValue(
                new File(pathname),
                new TypeReference<Map<Long, Double>>() {});
        return elevations;
    }

}
