package istd.code;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import istd.graph.Edge;
import istd.graph.MODE;
import istd.graph.Vertex;
import istd.graph.Graph;
import istd.graph.Vertex;

/**
 * Created by yaojie on 22/11/17.
 */

public class DistanceSolver {

    private ArrayList<Object[]> modeCombinations = new ArrayList<>();

    public List<List<Vertex>> permute(List<Vertex> vertices) {
        List<List<Vertex>> results = new ArrayList<List<Vertex>>();
        if (vertices == null || vertices.size() == 0) {
            return results;
        }
        List<Vertex> result = new ArrayList<>();
        dfs(vertices, results, result);
        return results;
    }

    public void dfs(List<Vertex> vertices, List<List<Vertex>> results, List<Vertex> result){
        if (vertices.size() == result.size()) {
            List<Vertex> temp = new ArrayList<>(result);
            results.add(temp);
        }
        for (Vertex vertex: vertices) {
            if (!result.contains(vertex)) {
                result.add(vertex);
                dfs(vertices, results, result);
                result.remove(result.size() - 1);
            }
        }
    }

    private void combinationsWithRepetition(MODE[] objects, int size, ArrayList<MODE> output){
        if (size == 0){
            modeCombinations.add(output.toArray());
        } else {
            for (int i = 0; i < objects.length; i++) {
                output.add(objects[i]);
                combinationsWithRepetition(objects, size- 1, output);
                output.remove(output.size() - 1);
            }
        }
    }

    private List<List<Edge>> generateEdgeCombinations(Graph graph){
        List<Vertex> vertices = graph.getVertices();
        Map<String, Edge> edgeMap = graph.getEdgeMap();
        List<List<Vertex>> results = permute(vertices.subList(1, vertices.size()));
        List<List<Edge>> tours = new ArrayList<List<Edge>>();
        for (List<Vertex> permutation: results){

            permutation.add(0, vertices.get(0)); // add back the root.
            int edgeCount = permutation.size() - 1;
            // choose mode combinations with repetition.
            combinationsWithRepetition(MODE.values(),edgeCount, new ArrayList<MODE>());
            // for every mode combination, generate arraylist of edges based on vertex permutations.
            for (Object[] combination: modeCombinations){
                List<Edge> edges = new ArrayList<>();
                for (int i = 0, j = 1; j < permutation.size(); i++, j++){
                    String identity = permutation.get(i).getIdentifier(permutation.get(j), (MODE)combination[i]);
                    Edge edge = edgeMap.get(identity);
                    if (edge != null)
                        edges.add(edge);
                }
//                System.out.println(Arrays.toString(edges.toArray()));
                if (edges.size() == edgeCount)
                    tours.add(edges);
            }
        }
        return tours;
    }

    public List<Edge> bruteForce(Graph graph){
        // generate a permutation of vertices
        int shortestTime = Integer.MAX_VALUE;
        List<Edge> mostEfficientTour = null;
        List<List<Edge>> tours = generateEdgeCombinations(graph);
        for (List<Edge> tour: tours){
            // calculate the budget and the time taken.
            int budget = 0;
            int travelTime = 0;
            for (Edge edge: tour){
                budget += edge.getCost();
                travelTime += edge.getTravelTime();
            }
            if (budget <= graph.getBudget() && travelTime < shortestTime){
                mostEfficientTour = tour;
                shortestTime = travelTime;
            }
        }
        return mostEfficientTour;
    }
}
