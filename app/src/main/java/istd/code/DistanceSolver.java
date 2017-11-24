package istd.code;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import istd.graph.Edge;
import istd.graph.MODE;
import istd.graph.TreeNode;
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
                double cost = 0;
                for (int i = 0, j = 1; j < permutation.size(); i++, j++){
                    String identity = permutation.get(i).getIdentifier(permutation.get(j), (MODE)combination[i]);
                    Edge edge = edgeMap.get(identity);
                    if (edge != null) {
                        edges.add(edge);
                        cost += edge.getCost();
                    }
                }
//                System.out.println(Arrays.toString(edges.toArray()));
                // add to tours if cost is below the budget and has no null
                if (edges.size() == edgeCount && cost <= graph.getBudget())
                    tours.add(edges);

            }
        }
        return tours;
    }

    public TreeNode createMST(List<Vertex> visited, int total, TreeNode tree){
        if (total == visited.size()){
            return tree;
        }

        List<Edge> sortedEdges = new ArrayList<>();
        for (Vertex vertex: visited){
            sortedEdges.addAll(vertex.getEdges()); // add all the candidate edges into the list to sort.
        }
        Collections.sort(sortedEdges);
        for (int i = 0; i < sortedEdges.size(); i++){
            Edge smallest = sortedEdges.get(i);
            if (!visited.contains(smallest.getDestination())){
                // if the node is smallest and not visited yet, populate the tree and break the loop
                visited.add(smallest.getDestination());
                if (tree == null) {
                    tree = new TreeNode(smallest.getSource());
                    tree.addChild(smallest.getDestination());
                } else {
                    // traverse the tree to find the node and add to it as the children.
                    tree.traverseTreeAndAdd(smallest.getSource(), smallest.getDestination());
                }
                break;
            }
        }

        return createMST(visited, total, tree);
    }

    public List<Edge> smartSolve(Graph graph){
        List<Edge> mostEfficientTour = new LinkedList<>();
        // use Prim's Algorithm to find a MST. Starting node will be root.
        List<Vertex> vertices = graph.getVertices();
        TreeNode mst = createMST(vertices.subList(0,1), vertices.size(), null);

        // traverse the tree in preorder to approximate a path.
        mst.preorder();
        Stack<Vertex> stack = mst.getStack();
        double tourBudget = 0;
        for (int i = 0, j = 1; j < stack.size(); i++, j++){
            String identity = stack.get(i).getIdentifier(stack.get(j), MODE.PUBLIC);
            System.out.println(identity);
            Edge edge = graph.getEdgeMap().get(identity);
            if (edge != null && (edge.getCost() + tourBudget) <= graph.getBudget()) {
                // if the edge is found and the cost is within budget, add to the start of the tour.
                mostEfficientTour.add(edge);
                tourBudget += edge.getCost();
            } else {
                // public transport too expensive, walk
                identity = stack.get(i).getIdentifier(stack.get(j), MODE.WALK);
                mostEfficientTour.add(graph.getEdgeMap().get(identity));
            }
        }

        // upgrade some routes to taxi if they are PUBLIC transports. Start from the furthest routes.
        for (int i =0, j = 1; j < stack.size(); i++, j++){
            String identity = stack.get(i).getIdentifier(stack.get(j), MODE.TAXI);
            Edge currentEdge = mostEfficientTour.get(mostEfficientTour.size() - j);
            Edge edge = graph.getEdgeMap().get(identity);
            if (edge != null && (edge.getCost() - currentEdge.getCost() + tourBudget) <= graph.getBudget()){
                // if the upgrade to taxi is within budget, replace the item.
                tourBudget += (edge.getCost() - currentEdge.getCost());
                mostEfficientTour.set(mostEfficientTour.size() - j, currentEdge);
            }
        }

        return mostEfficientTour;
    }

    public List<Edge> bruteForce(Graph graph){
        // generate a permutation of vertices
        // http://www.geeksforgeeks.org/travelling-salesman-problem-set-1/
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
            if (travelTime < shortestTime){
                mostEfficientTour = tour;
                shortestTime = travelTime;
            }
        }
        return mostEfficientTour;
    }
}
