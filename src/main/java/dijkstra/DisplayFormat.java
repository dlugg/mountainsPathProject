package dijkstra;

public class DisplayFormat {

    public static String duration(int seconds) {
        int hours = seconds / 3600;
        int minutes = (seconds % 3600) / 60;
        return hours + " h " + minutes + " min";
    }

    public static String kilometers(double meters) {
        return String.format("%.2f", meters / 1000) + " km";
    }
}
