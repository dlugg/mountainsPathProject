package dijkstra;

import java.util.Objects;

public class PathPoint<T> {
    private final T point;
    private final int time;
    PathPoint(T point, int time){
        this.point = point;
        this.time = time;
    }

    public T getPoint() {
        return point;
    }

    public int getTime() {
        return time;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        PathPoint pathPoint = (PathPoint) o;
        return time == pathPoint.time && Objects.equals(point, pathPoint.point);
    }

    @Override
    public int hashCode() {
        return Objects.hash(point, time);
    }
}
