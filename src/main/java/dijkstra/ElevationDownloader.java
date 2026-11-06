package dijkstra;

import tools.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;

public class ElevationDownloader {
    public static void main(String[] args) throws IOException, InterruptedException {
        long start = System.nanoTime();
        HttpClient client = HttpClient.newHttpClient();
        ObjectMapper mapper = new ObjectMapper();
        String pathname = "src/main/resources/export.json";
        OsmFile file = OsmReader.readOsmFile(mapper, pathname);
        Map<Long, OsmElement> indexNodesById = OsmReader.indexNodesById(file);
        Map<Long, Map<Long, Integer>> graph = OsmGraphBuilder.buildGraph(file);
        Map<Long, Double> idElevation = new HashMap<>();
        Set<Long> idSet = new HashSet<>(graph.keySet());
        for (Map<Long, Integer> neighbors : graph.values()) {
            idSet.addAll(neighbors.keySet());
        }
        List<Long> idsAll = new ArrayList<>(idSet);
        for (int i = 0; i < idsAll.size(); i += 100) {
            List<Long> idPart = new ArrayList<>(idsAll.subList(i, Math.min(i + 100, idsAll.size())));
            StringBuilder url = new StringBuilder("https://api.opentopodata.org/v1/eudem25m?locations=");
            List<String> latLon = new ArrayList<>();
            for (int j = 0; j < idPart.size(); j++) {
                OsmElement current = indexNodesById.get(idPart.get(j));
                latLon.add(current.lat() + "," + current.lon());
            }
            url.append(String.join("%7C", latLon));
            HttpRequest request = HttpRequest.newBuilder(URI.create(url.toString())).build();
            String body = client.send(request, HttpResponse.BodyHandlers.ofString()).body();
            Thread.sleep(1000);
            ElevationResponse response = mapper.readValue(body, ElevationResponse.class);
            if ("OK".equals(response.status())) {
                for (int j = 0; j < response.results().size(); j++) {
                    idElevation.put(idPart.get(j), response.results().get(j).elevation());
                }
            } else {
                System.out.println("response status " + response.status());
            }
        }
        mapper.writeValue(new File("src/main/resources/elevation.json"), idElevation);
        long msFile = (System.nanoTime() - start) / 1_000_000;
        System.out.println("на все ушло: " + msFile + " мс");
        System.out.println("map size: " + idElevation.size());
    }
}
