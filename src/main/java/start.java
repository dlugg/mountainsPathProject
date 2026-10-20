import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class start {
    public static void main(String[] args) {
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
        neighborsOfCZS.put("MUR", 3);
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


        System.out.println(peakAndNeighbors.get("KUZ"));
        System.out.println(peakAndNeighbors.get("MUR"));


    }

    static void dijkstra(String from, Map<String, Map<String, Integer>> peakAndNeighbors) {
        Set<String> checked = new HashSet<>();
        Map<String, Integer> peakAndTime = new HashMap<>();
        peakAndTime.put(from,0);
        while (checked.size()<peakAndNeighbors.size()){

        }

    }
}
