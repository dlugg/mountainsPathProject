package dijkstra;

public class Geo {
    final static int EARTH_RADIUS_IN_METERS=6_371_000;
    public static double distanceMeters(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double meanLat = Math.toRadians((lat1 + lat2) / 2);
        double dx = dLon * EARTH_RADIUS_IN_METERS * Math.cos(meanLat);
        double dy = dLat * EARTH_RADIUS_IN_METERS;
        return Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));
    }
}
