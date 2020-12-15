package com.company;

import java.util.*;

public class Graph {
    private ArrayList<Node> nodes = new ArrayList<>();
    private HashMap<Integer, LinkedList<Node>> allPAth = new HashMap<>();

    private Node topSource;
    private Node resultTop;

    public Graph(Node topSource, Node resultTop) {
        this.topSource = topSource;
        this.resultTop = resultTop;
    }

    private void findPathUtil(Node source, Node dest, LinkedList<Node> list, int destination) {
        if (source.getName().equals(dest.getName())) {
            allPAth.put(destination, new LinkedList<>(list));
        } else {
            for (Map.Entry<Node, Integer> adjacencyPair : source.getAdjacentNodes().entrySet()) {
                Node node = adjacencyPair.getKey();
                destination += adjacencyPair.getValue();
                list.add(node);
                findPathUtil(node, dest, list, destination);
                list.removeLast();
                destination -= adjacencyPair.getValue();
            }
        }
    }

    public void findPath() {
        LinkedList<Node> list = new LinkedList<>();
        list.add(topSource);
        findPathUtil(topSource, resultTop, list, 0);
    }

    public void addNode(Node nodeA) {
        nodes.add(nodeA);
    }

    public int getEccentricityGraph() {
        int[][] matrix = getMatrixEccentricity();
        int[] arr = ArrMaxEccentricity(matrix);
        return findMinInArr(arr);
    }

    private int[][] getMatrixEccentricity() {
        int[][] matrix = new int[nodes.size()][nodes.size()];
        for (int i = 0; i < nodes.size(); i++) {
            calculateShortestPathFromSource(nodes.get(i));
            for (int j = 0; j < nodes.size(); j++) {
                matrix[j][i] = nodes.get(j).getDistance();
                nodes.get(j).setDistance(Integer.MAX_VALUE);
                System.out.printf("%10d     ",matrix[j][i]);
            }
            System.out.println("\n");
        }
        return matrix;
    }

    private int[] ArrMaxEccentricity(int[][] matrix) {
        int[] arr = new int[matrix.length];
        for (int i = 0; i < arr.length; i++) {
            int temp = 0;
            for (int j = 0; j < matrix.length; j++) {
                if (matrix[j][i] == Integer.MAX_VALUE || matrix[j][i] == 0)
                    continue;
                if (temp < matrix[j][i])
                    temp = matrix[j][i];
            }
            arr[i] = temp;
        }
        return arr;
    }

    private int findMinInArr(int[] arr) {
        int temp = Integer.MAX_VALUE;
        for (int i = 1; i < arr.length; i++) {
            if (arr[i] == 0)
                continue;
            if (temp > arr[i])
                temp = arr[i];
        }
        return temp;
    }

    public static void calculateShortestPathFromSource(Node source) {
        source.setDistance(0);

        Set<Node> settledNodes = new HashSet<>();
        Set<Node> unsettledNodes = new HashSet<>();

        unsettledNodes.add(source);

        while (unsettledNodes.size() != 0) {
            Node currentNode = getLowestDistanceNode(unsettledNodes);
            unsettledNodes.remove(currentNode);
            for (Map.Entry<Node, Integer> adjacencyPair :
                    currentNode.getAdjacentNodes().entrySet()) {
                Node adjacentNode = adjacencyPair.getKey();
                Integer edgeWeight = adjacencyPair.getValue();
                if (!settledNodes.contains(adjacentNode)) {
                    calculateMinimumDistance(adjacentNode, edgeWeight, currentNode);
                    unsettledNodes.add(adjacentNode);
                }
            }
            settledNodes.add(currentNode);
        }
    }

    private static Node getLowestDistanceNode(Set<Node> unsettledNodes) {
        Node lowestDistanceNode = null;
        int lowestDistance = Integer.MAX_VALUE;
        for (Node node : unsettledNodes) {
            int nodeDistance = node.getDistance();
            if (nodeDistance < lowestDistance) {
                lowestDistance = nodeDistance;
                lowestDistanceNode = node;
            }
        }
        return lowestDistanceNode;
    }

    private static void calculateMinimumDistance(Node evaluationNode,
                                                 Integer edgeWeigh, Node sourceNode) {
        Integer sourceDistance = sourceNode.getDistance();
        if (sourceDistance + edgeWeigh < evaluationNode.getDistance()) {
            evaluationNode.setDistance(sourceDistance + edgeWeigh);
            LinkedList<Node> shortestPath = new LinkedList<>(sourceNode.getShortestPath());
            shortestPath.add(sourceNode);
            evaluationNode.setShortestPath(shortestPath);
        }
    }

    private String findMinPath() {
        int temp = Integer.MAX_VALUE;
        for (Integer dest : allPAth.keySet()) {
            if (dest < temp)
                temp = dest;
        }
        return "Минимальный путь равен = " + temp + ". Путь проходит через вершины " + allPAth.get(temp).toString();
    }


    @Override
    public String toString() {
        String string = "Вершина источник = " + topSource +
                "\nВершина приемник = " + resultTop;
        if (!allPAth.isEmpty())
            string += "\n" + findMinPath() + "\nВсе возможные пути между вершинами\n" + allPAth +
                    "\nЭксцентриситет графа = " + getEccentricityGraph();
        return string;
    }
}