package pl.lugowski.routes.routing;

import java.util.Objects;

public class PathPoint<T> {
    private final T point;
    private final int cost;
    public PathPoint(T point, int cost){
        this.point = point;
        this.cost = cost;
    }

    public T getPoint() {
        return point;
    }

    public int getCost() {
        return cost;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        PathPoint pathPoint = (PathPoint) o;
        return cost == pathPoint.cost && Objects.equals(point, pathPoint.point);
    }

    @Override
    public String toString() {
        return "PathPoint{" +
                "point=" + point +
                ", cost=" + cost +
                '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(point, cost);
    }
}
