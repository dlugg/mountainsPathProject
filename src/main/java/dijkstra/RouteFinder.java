package dijkstra;

import tools.jackson.databind.ObjectMapper;

import java.io.File;
import java.util.*;

public class RouteFinder {
    public static void main(String[] args) {
    }

    public static OsmFile osmFileReader(ObjectMapper mapper, String pathname) {
        return mapper.readValue(new File(pathname), OsmFile.class);
    }

    public static double distanceBetweenTwoPoints(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double meanLat = Math.toRadians((lat1 + lat2) / 2);
        double dx = dLon * 6371000 * Math.cos(meanLat);
        double dy = dLat * 6371000;
        return Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));
    }

    public static Map<Long, Map<Long, Integer>> graphBuilderFromOsmFile(OsmFile file) {
        Map<Long, Map<Long, Integer>> graph = new HashMap<>();
        for (int i = 0; i < file.elements().size(); i++) {
            OsmElement element = file.elements().get(i);
            if (element.isTrail()) {
                for (int j = 0; j < element.nodes().size() - 1; j++) {
                    graph.computeIfAbsent(element.nodes().get(j), k -> new HashMap<>()).put(element.nodes().get(j + 1), 1);
                    graph.computeIfAbsent(element.nodes().get(j + 1), k -> new HashMap<>()).put(element.nodes().get(j), 1);
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
        dijkstraResult.putTimeFromStart(from, 0);
        while (dijkstraResult.getTimeFromStart().size() > fixed.size()) {
            int currentTime = Integer.MAX_VALUE;
            T current = null;
            for (Map.Entry<T, Integer> entry : dijkstraResult.getTimeFromStart().entrySet()) {
                if (!fixed.contains(entry.getKey())) {
                    if (entry.getValue() < currentTime) {
                        currentTime = entry.getValue();
                        current = entry.getKey();
                    }
                }
            }
            fixed.add(current);
            for (Map.Entry<T, Integer> entry : graph.getOrDefault(current, Map.of()).entrySet()) {
                T neighbor = entry.getKey();
                Integer neighborTime = dijkstraResult.getTimeFromStart().get(neighbor);
                if (!dijkstraResult.getTimeFromStart().containsKey(neighbor)) {
                    dijkstraResult.putTimeFromStart(neighbor, entry.getValue() + currentTime);
                    dijkstraResult.putPrevious(neighbor, current);
                } else {
                    if (entry.getValue() + currentTime < neighborTime) {
                        dijkstraResult.putTimeFromStart(neighbor, entry.getValue() + currentTime);
                        dijkstraResult.putPrevious(neighbor, current);
                    }
                }
            }
        }
        return dijkstraResult;

    }

    public static <T> Optional<List<PathPoint<T>>> pathTo(T to, DijkstraResult<T> dijkstraResult) {
        List<PathPoint<T>> path = new ArrayList<>();
        if (!dijkstraResult.getTimeFromStart().containsKey(to)) {
            return Optional.empty();
        }
        T current = to;
        while (current != null) {
            path.add(new PathPoint<>(current, dijkstraResult.getTimeFromStart().get(current)));
            current = dijkstraResult.getPrevious().get(current);
        }
        return Optional.of(path.reversed());
    }

    public static <T> Optional<List<PathPoint<T>>> routeThrough(List<T> pointList, Map<T, Map<T, Integer>> graph) {
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
                int offset = route.getLast().getTime();
                for (int j = 0; j < piece.size(); j++) {
                    route.add(new PathPoint<T>(piece.get(j).getPoint(), piece.get(j).getTime() + offset));
                }
            }
        }
        return Optional.of(route);
    }
}
