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
        for (Vertex Vertex: vertices) {
            if (!result.contains(Vertex)) {
                result.add(Vertex);
                dfs(vertices, results, result);
                result.remove(result.size() - 1);
            }
        }
    }

    public List<Vertex> bruteForce(Graph graph){
        // generate a permutation of vertices
        List<Vertex> vertices = graph.getVertices();
        Map<String, Edge> edgeMap = graph.getEdgeMap();
        List<List<Vertex>> results = permute(vertices.subList(1, vertices.size()));
        for (List<Vertex> permutation: results){

            permutation.add(0, vertices.get(0)); // add back the root.

            List<Edge> edges = new ArrayList<>();
            // generate arraylists of edges based on permutation.
            for (int i = 0, j = 1; j < permutation.size(); i++, j++){
                String identity = permutation.get(i).getIdentifier(permutation.get(j), MODE.PUBLIC);
                Edge edge = edgeMap.get(identity);
                edges.add(edge);
            }
        }
        return null;
    }
}
