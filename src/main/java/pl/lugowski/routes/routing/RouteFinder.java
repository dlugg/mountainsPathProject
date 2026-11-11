package pl.lugowski.routes.routing;

import java.util.*;

public class RouteFinder {


    public static <T> DijkstraResult<T> dijkstra(T from, Map<T, Map<T, Integer>> graph) {
        PriorityQueue<Point<T>> queue = new PriorityQueue<>(Comparator.comparingInt(Point::cost));
        DijkstraResult<T> dijkstraResult = new DijkstraResult<>();
        Set<T> fixed = new HashSet<>();
        dijkstraResult.putCostFromStart(from, 0);
        queue.add(new Point<>(from, 0));
        while (!queue.isEmpty()) {
            int currentCost;
            T current;
            Point<T> bestPoint = queue.poll();
            if (!fixed.contains(bestPoint.point())) {
                currentCost = bestPoint.cost();
                current = bestPoint.point();
            } else {
                continue;
            }


            fixed.add(current);
            for (Map.Entry<T, Integer> entry : graph.getOrDefault(current, Map.of()).entrySet()) {
                T neighbor = entry.getKey();
                Integer neighborCost = dijkstraResult.getCostFromStart().get(neighbor);
                if (!dijkstraResult.getCostFromStart().containsKey(neighbor)) {
                    dijkstraResult.putCostFromStart(neighbor, entry.getValue() + currentCost);
                    dijkstraResult.putPrevious(neighbor, current);
                    queue.add(new Point<>(neighbor, entry.getValue() + currentCost));
                } else {
                    if (entry.getValue() + currentCost < neighborCost) {
                        dijkstraResult.putCostFromStart(neighbor, entry.getValue() + currentCost);
                        dijkstraResult.putPrevious(neighbor, current);
                        queue.add(new Point<>(neighbor, entry.getValue() + currentCost));
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

    public static <T> Optional<List<List<PathPoint<T>>>> routeLegs(List<T> pointList, Map<T, Map<T, Integer>> graph) {
        List<List<PathPoint<T>>> legs = new ArrayList<>();

        for (int i = 0; i < pointList.size() - 1; i++) {
            Optional<List<PathPoint<T>>> pathPart = pathTo(pointList.get(i + 1), dijkstra(pointList.get(i), graph));
            if (pathPart.isEmpty()) {
                return Optional.empty();
            }
            legs.add(pathPart.get());
        }
        return Optional.of(legs);
    }

    public static <T> Optional<List<PathPoint<T>>> routeThrough(List<T> pointList, Map<T, Map<T, Integer>> graph) {
        List<PathPoint<T>> route = new ArrayList<>();
        Optional<List<List<PathPoint<T>>>> legs = routeLegs(pointList, graph);
        if (legs.isEmpty()) {
            return Optional.empty();
        }
        for (int i = 0; i < legs.get().size(); i++) {
            if (i == 0) {
                route.addAll(legs.get().get(i));
            } else {
                List<PathPoint<T>> piece = legs.get().get(i);
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
