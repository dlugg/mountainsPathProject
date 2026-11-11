package pl.lugowski.routes.geo;

import pl.lugowski.routes.osm.OsmElement;
import pl.lugowski.routes.routing.PathPoint;

import java.util.List;
import java.util.Map;

public class Geo {
    final static int EARTH_RADIUS_IN_METERS = 6_371_000;
    final static int DESCENT_METERS_PER_UNIT = 145;
    final static int ASCENT_METERS_PER_UNIT = 100;


    public static double distanceMeters(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double meanLat = Math.toRadians((lat1 + lat2) / 2);
        double dx = dLon * EARTH_RADIUS_IN_METERS * Math.cos(meanLat);
        double dy = dLat * EARTH_RADIUS_IN_METERS;
        return Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));
    }

    public static int munterTime(double distanceMeters, double startElevation, double finishElevation, double pace) {
        double units = distanceMeters / 1_000;
        if (finishElevation - startElevation > 0) {
            double ascentUnits = (finishElevation - startElevation) / ASCENT_METERS_PER_UNIT;
            units += ascentUnits;
        } else {
            double descentUnits = Math.abs((finishElevation - startElevation)) / DESCENT_METERS_PER_UNIT;
            units += descentUnits;
        }
        return (int) Math.round(units / pace * 3600);
    }

    public static double routeLengthMeters(List<PathPoint<Long>> route, Map<Long, OsmElement> pointIdAndItCoordinates) {
        double routeLengthMeters = 0;
        for (int i = 0; i < route.size() - 1; i++) {
            double lat1 = pointIdAndItCoordinates.get(route.get(i).getPoint()).lat();
            double lat2 = pointIdAndItCoordinates.get(route.get(i + 1).getPoint()).lat();
            double lon1 = pointIdAndItCoordinates.get(route.get(i).getPoint()).lon();
            double lon2 = pointIdAndItCoordinates.get(route.get(i + 1).getPoint()).lon();
            routeLengthMeters += distanceMeters(lat1, lon1, lat2, lon2);
        }
        return routeLengthMeters;
    }
}
