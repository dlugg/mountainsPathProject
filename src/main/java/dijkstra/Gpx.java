package dijkstra;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public class Gpx {
    public static String build(List<PathPoint<Long>> pathPoints, Map<Long, OsmElement> indexNodesById, List<String> names) {
        StringBuilder xml = new StringBuilder();
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n")
                .append("<gpx version=\"1.1\" creator=\"mountainsPathProject\" xmlns=\"http://www.topografix.com/GPX/1/1\">\n")
                .append("<trk>\n")
                .append("<name>")
                .append(String.join(" - ", names))
                .append("</name>\n")
                .append("<trkseg>\n");
        for (int i = 0; i < pathPoints.size(); i++) {
            Double lat = indexNodesById.get(pathPoints.get(i).getPoint()).coordinates().lat();
            Double lon = indexNodesById.get(pathPoints.get(i).getPoint()).coordinates().lon();
            xml.append("<trkpt lat=")
                    .append("\"" + lat + "\"")
                    .append(" ").append("lon=")
                    .append("\"" + lon + "\"></trkpt>\n");

        }
        xml.append("</trkseg>\n")
                .append("</trk>\n")
                .append("</gpx>");

        return xml.toString();
    }

    public static void save(String xml) throws IOException {
        Files.writeString(Path.of("route.gpx"), xml);
    }

}
