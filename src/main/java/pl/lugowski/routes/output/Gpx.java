package pl.lugowski.routes.output;

import pl.lugowski.routes.osm.OsmElement;
import pl.lugowski.routes.routing.PathPoint;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class Gpx {
    public static String build(List<List<PathPoint<Long>>> legs, Map<Long, OsmElement> indexNodesById, List<String> names) {
        if (names.size() != legs.size() + 1) {
            throw new IllegalArgumentException("check your name list");
        }
        StringBuilder xml = new StringBuilder();
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n")
                .append("<gpx version=\"1.1\" creator=\"mountainsPathProject\" xmlns=\"http://www.topografix.com/GPX/1/1\">\n");
        for (int i = 0; i < legs.size(); i++) {
            List<PathPoint<Long>> currentLeg = legs.get(i);
            xml.append("<trk>\n")
                    .append("<name>")
                    .append(names.get(i) + " - " + names.get(i + 1) + " (" + DisplayFormat.duration(currentLeg.getLast().getCost()) + ")")
                    .append("</name>\n")
                    .append("<trkseg>\n");
            for (int j = 0; j < currentLeg.size(); j++) {
                OsmElement currentElement = indexNodesById.get(currentLeg.get(j).getPoint());
                xml.append("<trkpt lat=")
                        .append("\"" + currentElement.lat() + "\"")
                        .append(" ").append("lon=")
                        .append("\"" + currentElement.lon() + "\"></trkpt>\n");
            }
            xml.append("</trkseg>\n")
                    .append("</trk>\n");
        }
        xml.append("</gpx>");
        return xml.toString();
    }

    public static void save(String xml) throws IOException {
        Files.writeString(Path.of("route.gpx"), xml);
    }

}

