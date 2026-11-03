package dijkstra;

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
}
