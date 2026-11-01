package dijkstra;

import tools.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class RouteFinder {
    public static void main(String[] args) throws IOException {
        long readFile = System.nanoTime();
        ObjectMapper mapper = new ObjectMapper();
        String pathname = "src/main/resources/export.json";
        OsmFile file = RouteFinder.readOsmFile(mapper, pathname);
        Map<Long, OsmElement> indexNodesById = indexNodesById(file);
        long msFile = (System.nanoTime() - readFile) / 1_000_000;
        System.out.println("чтение осм файла заняло: " + msFile + " мс");
        long graphBuild = System.nanoTime();
        Map<Long, Map<Long, Integer>> graph = buildGraph(file);
        long msGraph = (System.nanoTime() - graphBuild) / 1_000_000;
        System.out.println("построение графа заняло: " + msGraph + " мс");
        List<String> names = new ArrayList<>(List.of("Kuźnice", "Murowaniec", "Kasprowy Wierch", "Kuźnice"));
        List<Long> pathPoints = new ArrayList<>();
        long start = System.nanoTime();
        for (int i = 0; i < names.size(); i++) {
            Coordinates coordinates = findCoordinatesByName(names.get(i), file).get();
            pathPoints.add(nearestVertex(coordinates.lat(), coordinates.lon(), indexNodesById, graph).get());
        }
        saveGpx(buildGpx(routeThrough(pathPoints, graph).get(), indexNodesById, names));
        long ms = (System.nanoTime() - start) / 1_000_000;
        System.out.println("заняло: " + ms + " мс");

    }


    public static String buildGpx(List<PathPoint<Long>> pathPoints, Map<Long, OsmElement> indexNodesById, List<String> names) {
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

    public static void saveGpx(String xml) throws IOException {
        Files.writeString(Path.of("route.gpx"), xml);
    }

    public static Optional<Coordinates> findCoordinatesByName(String place, OsmFile file) {
        for (int i = 0; i < file.elements().size(); i++) {
            OsmElement element = file.elements().get(i);

            if (element.tags() != null) {
                if (place.equals(element.tags().get("name")) || place.equals(element.tags().get("name:pl"))) {
                    return Optional.of(element.coordinates());
                }
            }
        }
        return Optional.empty();
    }

    public static OsmFile readOsmFile(ObjectMapper mapper, String pathname) {
        return mapper.readValue(new File(pathname), OsmFile.class);
    }

    public static double distanceMeters(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double meanLat = Math.toRadians((lat1 + lat2) / 2);
        double dx = dLon * 6371000 * Math.cos(meanLat);
        double dy = dLat * 6371000;
        return Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));
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

    public static Optional<Long> nearestVertex(double lat, double lon, Map<Long, OsmElement> indexNodesById, Map<Long, Map<Long, Integer>> graph) {
        double closestPointDistance = Double.MAX_VALUE;
        long closestPointId = 0;


        for (Map.Entry<Long, Map<Long, Integer>> entry : graph.entrySet()) {
            OsmElement element = indexNodesById.get(entry.getKey());
            long currentId = element.id();
            double currentDistance = distanceMeters(lat, lon, element.lat(), element.lon());
            if (closestPointDistance > currentDistance) {
                closestPointDistance = currentDistance;
                closestPointId = currentId;
            }
        }
        if (closestPointId == 0) {
            return Optional.empty();
        } else {
            return Optional.of(closestPointId);
        }
    }

    public static Map<Long, Map<Long, Integer>> buildGraph(OsmFile file) {
        Map<Long, Map<Long, Integer>> graph = new HashMap<>();
        Map<Long, OsmElement> pointIdAndItCoordinates = indexNodesById(file);

        for (int i = 0; i < file.elements().size(); i++) {
            OsmElement element = file.elements().get(i);
            if (element.isTrail()) {
                for (int j = 0; j < element.nodes().size() - 1; j++) {
                    double lat1 = pointIdAndItCoordinates.get(element.nodes().get(j)).lat();
                    double lat2 = pointIdAndItCoordinates.get(element.nodes().get(j + 1)).lat();
                    double lon1 = pointIdAndItCoordinates.get(element.nodes().get(j)).lon();
                    double lon2 = pointIdAndItCoordinates.get(element.nodes().get(j + 1)).lon();
                    graph.computeIfAbsent(element.nodes().get(j), k -> new HashMap<>()).put(element.nodes().get(j + 1),
                            (int) Math.round(distanceMeters(lat1, lon1, lat2, lon2)));
                    graph.computeIfAbsent(element.nodes().get(j + 1), k -> new HashMap<>()).put(element.nodes().get(j),
                            (int) Math.round(distanceMeters(lat1, lon1, lat2, lon2)));
                }
            }
        }
        return graph;
    }

    public static Map<String, Map<String, Integer>> graphABCD() {
        Map<String, Map<String, Integer>> graphABCD = new HashMap<>();
        Map<String, Integer> neighborsOfA = new HashMap<>();
        neighborsOfA.put("B", 10);
        neighborsOfA.put("C", 3);
        graphABCD.put("A", neighborsOfA);
        Map<String, Integer> neighborsOfB = new HashMap<>();
        neighborsOfB.put("C", 4);
        neighborsOfB.put("A", 10);
        neighborsOfB.put("D", 2);
        graphABCD.put("B", neighborsOfB);

        Map<String, Integer> neighborsOfC = new HashMap<>();
        neighborsOfC.put("B", 4);
        neighborsOfC.put("A", 3);
        neighborsOfC.put("D", 8);
        graphABCD.put("C", neighborsOfC);
        Map<String, Integer> neighborsOfD = new HashMap<>();
        neighborsOfD.put("B", 2);
        neighborsOfD.put("C", 8);
        graphABCD.put("D", neighborsOfD);

        return graphABCD;
    }

    public static Map<String, Map<String, Integer>> graphKaspr() {
        Map<String, Map<String, Integer>> peakAndNeighbors = new HashMap<>();
        Map<String, Integer> neighborsOfKuz = new HashMap<>();
        neighborsOfKuz.put("MUR", 140);
        neighborsOfKuz.put("KASP", 210);
        peakAndNeighbors.put("KUZ", neighborsOfKuz);

        Map<String, Integer> neighborsOfMur = new HashMap<>();
        neighborsOfMur.put("KUZ", 100);
        neighborsOfMur.put("HALA", 3);
        neighborsOfMur.put("CZS", 51);
        peakAndNeighbors.put("MUR", neighborsOfMur);

        Map<String, Integer> neighborsOfCZS = new HashMap<>();
        neighborsOfCZS.put("KARB", 50);
        neighborsOfCZS.put("MUR", 35);
        neighborsOfCZS.put("SG", 92);
        peakAndNeighbors.put("CZS", neighborsOfCZS);

        Map<String, Integer> neighborsOfHala = new HashMap<>();
        neighborsOfHala.put("MUR", 2);
        neighborsOfHala.put("KASP", 97);
        peakAndNeighbors.put("HALA", neighborsOfHala);

        Map<String, Integer> neighborsOfKasp = new HashMap<>();
        neighborsOfKasp.put("KUZ", 139);
        neighborsOfKasp.put("SWI", 101);
        neighborsOfKasp.put("HALA", 63);
        peakAndNeighbors.put("KASP", neighborsOfKasp);

        Map<String, Integer> neighborsOfSwi = new HashMap<>();
        neighborsOfSwi.put("KASP", 89);
        peakAndNeighbors.put("SWI", neighborsOfSwi);

        Map<String, Integer> neighborsOfSg = new HashMap<>();
        neighborsOfSg.put("CZS", 84);
        peakAndNeighbors.put("SG", neighborsOfSg);

        Map<String, Integer> neighborsOfKarb = new HashMap<>();
        neighborsOfKarb.put("KOSC", 46);
        neighborsOfKarb.put("CZS", 38);
        peakAndNeighbors.put("KARB", neighborsOfKarb);

        Map<String, Integer> neighborsOfKosc = new HashMap<>();
        neighborsOfKosc.put("KARB", 36);
        peakAndNeighbors.put("KOSC", neighborsOfKosc);


        return peakAndNeighbors;
    }

    public static <T> DijkstraResult<T> dijkstra(T from, Map<T, Map<T, Integer>> graph) {
        DijkstraResult<T> dijkstraResult = new DijkstraResult<>();
        Set<T> fixed = new HashSet<>();
        dijkstraResult.putCostFromStart(from, 0);
        while (dijkstraResult.getCostFromStart().size() > fixed.size()) {
            int currentCost = Integer.MAX_VALUE;
            T current = null;
            for (Map.Entry<T, Integer> entry : dijkstraResult.getCostFromStart().entrySet()) {
                if (!fixed.contains(entry.getKey())) {
                    if (entry.getValue() < currentCost) {
                        currentCost = entry.getValue();
                        current = entry.getKey();
                    }
                }
            }
            fixed.add(current);
            for (Map.Entry<T, Integer> entry : graph.getOrDefault(current, Map.of()).entrySet()) {
                T neighbor = entry.getKey();
                Integer neighborCost = dijkstraResult.getCostFromStart().get(neighbor);
                if (!dijkstraResult.getCostFromStart().containsKey(neighbor)) {
                    dijkstraResult.putCostFromStart(neighbor, entry.getValue() + currentCost);
                    dijkstraResult.putPrevious(neighbor, current);
                } else {
                    if (entry.getValue() + currentCost < neighborCost) {
                        dijkstraResult.putCostFromStart(neighbor, entry.getValue() + currentCost);
                        dijkstraResult.putPrevious(neighbor, current);
                    }
                }
            }
        }
        return dijkstraResult;

    }

    public static <T> Optional<List<PathPoint<T>>> pathTo(T to, DijkstraResult<T> dijkstraResult) {
        List<PathPoint<T>> path = new ArrayList<>();
        if (!dijkstraResult.getCostFromStart().containsKey(to)) {
            return Optional.empty();
        }
        T current = to;
        while (current != null) {
            path.add(new PathPoint<>(current, dijkstraResult.getCostFromStart().get(current)));
            current = dijkstraResult.getPrevious().get(current);
        }
        return Optional.of(path.reversed());
    }

    public static <
            T> Optional<List<PathPoint<T>>> routeThrough(List<T> pointList, Map<T, Map<T, Integer>> graph) {
        List<PathPoint<T>> route = new ArrayList<>();

        for (int i = 0; i < pointList.size() - 1; i++) {
            Optional<List<PathPoint<T>>> pathPart = pathTo(pointList.get(i + 1), dijkstra(pointList.get(i), graph));
            if (pathPart.isEmpty()) {
                return Optional.empty();
            }
            if (i == 0) {
                route.addAll(pathPart.get());
            } else {
                List<PathPoint<T>> piece = pathPart.get();
                piece.removeFirst();
                int offset = route.getLast().getCost();
                for (int j = 0; j < piece.size(); j++) {
                    route.add(new PathPoint<T>(piece.get(j).getPoint(), piece.get(j).getCost() + offset));
                }
            }
        }
        return Optional.of(route);
    }
}
