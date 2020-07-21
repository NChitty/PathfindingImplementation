public class PathfindingImplementation {
    static Vertex[] vertices;

}

class Vertex implements Comparator<Vertex> {

    // The number associated with this vertex
    int serial;
    //the index of the edge is the serial number of the vertex being travelled to
    //the integer value is the weight of that edge
    int[] edges;
    // the node preceding is parent, cost is how much it costs to get there
    int parent = 0, cost = Integer.MAX_VALUE; // 0 = unvisited, max int represents infinity

    public Vertex(int serial, int vertices) {
        this.serial = serial;
        edges = new int[vertices+1];
        for(int i = 0; i < vertices+1; i++) {
            edges[i] = Integer.MAX_VALUE;
        }
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

    /**
     * 
     * @param v The vertex to the edge
     * @return the weight of the edge associated with the vertex, if it does not exist, then the integer is max value
     */
    public int getEdgeWeight(Vertex v) {
        if(v == null) {
            return Integer.MAX_VALUE;
        } else {
            return weights.get(v);
        }
    }


    public int getParent() {
        return this.parent;
    }

    public int getCost() {
        return this.cost;
    }

    /**
     * Creates a bidirectional edge between two vertices
     * @param a A vertex
     * @param b The other vertex
     * @param weight The weight associated with the edge
     */
    public static void createEdge(Vertex a, Vertex b, int weight) {
        a.edges[b.serial] = weight;
        b.edges[a.serial] = weight;
    }

    @Override
    public String toString() {
        return "Vertex: " + this.serial + "| Visited: " + this.visited;
    }

    @Override
    public int compare(Vertex arg0, Vertex arg1) {
        if(arg0.cost - arg1.cost == 0)
            return arg0.serial - arg1.serial;
        else
            return arg0.cost - arg1.cost;
    }
}