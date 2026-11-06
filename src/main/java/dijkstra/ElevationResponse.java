package dijkstra;

import java.util.List;

public record ElevationResponse(List<ElevationResult> results, String status) {
}
