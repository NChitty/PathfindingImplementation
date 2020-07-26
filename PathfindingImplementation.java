import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;

public class PathfindingImplementation {
    static Vertex[] vertices;
    static ArrayList<Edge> edges = new ArrayList<>();
    static int[][] matrix;

    public static void main(String[] args) {
        File input = new File("cop3503-asn3-input.txt");
        File bfOutput = new File("cop3503-asn3-output-chitty-nicholas-bf.txt");
        File fwOutput = new File("cop3503-asn3-output-chitty-nicholas-fw.txt");
        readFile(input);
        try {
            doBellmanFord();
        } catch (Exception e) {
            e.printStackTrace();
        }
        doFloydWarshall();
        writeBellmanFord(bfOutput);
        writeFloydWarshall(fwOutput);
    }
    
    /**
     * 
     * @param input the file to read the input from
     * @return the serial number of the source vertex
     */
    public static int readFile(File input) {
        int sourceVertex = -1, noOfVertices, numberOfEdges;
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(input));
        } catch (FileNotFoundException e) {
            System.out.println("Could not find input file.");
            System.exit(0);
        }
        try {
            noOfVertices = Integer.parseInt(reader.readLine());
            vertices = new Vertex[noOfVertices+1];
            matrix = new int[noOfVertices+1][noOfVertices+1];
            for(int i = 0; i < noOfVertices+1; i++)
                for(int j = 0; j < noOfVertices+1; j++)
                    if(i == j) matrix[i][j] = 0;
                    else matrix[i][j] = Integer.MAX_VALUE;
            for (int i = 0; i < noOfVertices; i++) {
                Vertex vertex = new Vertex(i+1);
                vertices[i+1] = vertex;
            }
            sourceVertex = Integer.parseInt(reader.readLine());
            numberOfEdges = Integer.parseInt(reader.readLine());
            for (int i = 0; i < numberOfEdges; i++) {
                String[] edgeInfo = reader.readLine().replace(".", "").split(" ");
                int vertexSerial1 = Integer.parseInt(edgeInfo[0]);
                int vertexSerial2 = Integer.parseInt(edgeInfo[1]);
                int weight = Integer.parseInt(edgeInfo[2]);
                Vertex vertex1 = vertices[vertexSerial1];
                Vertex vertex2 = vertices[vertexSerial2];
                Edge newEdge = new Edge(vertex1, vertex2, weight);
                edges.add(newEdge);
                matrix[vertex1.serial][vertex2.serial] = weight;
                matrix[vertex2.serial][vertex1.serial] = weight;
            }
            vertices[sourceVertex].setParent(0);
            vertices[sourceVertex].setCost(0);
        } catch (NumberFormatException | IOException e) {
            e.printStackTrace();
        }
        try {
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sourceVertex;
    }

    /**
     * Based on pseudocode on from slides
     */
    public static void doBellmanFord() throws Exception {
        for(int i = 1; i < vertices.length-1; i++) {
            //For every edge associated with this vertex
            for(Edge edge : edges) {
                int w = edge.getWeight();
                if(edge.a.cost + w < edge.b.cost && edge.a.cost + w > 0) {
                    edge.b.cost = edge.a.cost + w;
                    edge.b.parent = edge.a.serial;
                    continue;
                }
                if(edge.b.cost + w < edge.a.cost && edge.b.cost + w > 0) {
                    edge.a.cost = edge.b.cost + w;
                    edge.a.parent = edge.b.serial;
                }
            }
        }
        for(Edge e : edges) {
            int w = e.getWeight();
            if(e.a.cost + w < e.b.cost) {
                throw new Exception("Negative weight cycle");
            }
        }
    }

    /**
     * Based on pseudocode from floyd-warshall wiki page
     */
    public static void doFloydWarshall() {
        //choose the intermediate vertex
        for(int k = 1; k < vertices.length; k++) {
            //choose the source
            for(int i = 1; i < vertices.length; i++) {
                //choose every destination
                for(int j = 1; j < vertices.length; j++) {
                    //avoid integer overflow by checking if there is an edge
                    if(matrix[i][k] == Integer.MAX_VALUE || matrix[k][j] == Integer.MAX_VALUE)
                        continue;
                    //if there is a shorter path through k
                    if(matrix[i][k] + matrix[k][j] < matrix[i][j])
                        //update distance from i to j
                        matrix[i][j] = matrix[i][k] + matrix[k][j];
                }
            }
        }
    }

    public static void writeBellmanFord(File output) {
        if (!output.exists()) {
            try {
                output.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("Creating the output file");
        }
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(output));
            writer.write(vertices.length - 1 + "");
            for (int i = 1; i < vertices.length; i++) {
                writer.newLine();
                writer.write(vertices[i].getSerial() + " " + vertices[i].getCost() + " " + vertices[i].getParent());
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeFloydWarshall(File output) {
        if (!output.exists()) {
            try {
                output.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("Creating the output file");
        }
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(output));
            writer.write(vertices.length - 1 + "\n");
            for(int i = 1; i < vertices.length; i++) {
                for(int j = 1; j < vertices.length; j++) {
                    if(matrix[i][j] == Integer.MAX_VALUE)
                        writer.write("INF ");
                    else
                        writer.write(matrix[i][j] + " ");
                }
                writer.write("\n");
            }
            writer.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
}

class Vertex implements Comparator<Vertex> {

    // The number associated with this vertex
    int serial;
    // the node preceding is parent, cost is how much it costs to get there
    int parent = 0, cost = Integer.MAX_VALUE; // 0 = unvisited, max int represents infinity

    public Vertex(int serial) {
        this.serial = serial;
    }

    public void setParent(int parent) {
        this.parent = parent;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public int getSerial() {
        return this.serial;
    }

    public int getParent() {
        return this.parent;
    }

    public int getCost() {
        return this.cost;
    }

    @Override
    public String toString() {
        return "Vertex: " + this.serial;
    }

    @Override
    public int compare(Vertex arg0, Vertex arg1) {
        if(arg0.cost - arg1.cost == 0)
            return arg0.serial - arg1.serial;
        else
            return arg0.cost - arg1.cost;
    }
}

class Edge {
    Vertex a, b;
    int weight;

    public Edge(Vertex a, Vertex b, int weight) {
        this.a = a;
        this.b = b;
        this.weight = weight;
    }

    public int getWeight() {
        return this.weight;
    }

}