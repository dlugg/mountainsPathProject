package dijkstra;

import java.util.*;

public class RouteFinder {


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
