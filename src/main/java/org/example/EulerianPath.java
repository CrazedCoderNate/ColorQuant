import java.util.*;

public class EulerianPath {
    private int edges;
    private LinkedList<Integer>[] adj; 

    @SuppressWarnings("unchecked")
    public EulerianPath(int vertices) {
        this.adj = new LinkedList[vertices];
        for (int i = 0; i < vertices; ++i) {
            adj[i] = new LinkedList<>();
        }
    }

    public void addEdge(int v, int w) {
        adj[v].add(w);
        adj[w].add(v);
        edges++;
    }

    private void removeEdge(int v, int w) {
        adj[v].remove((Integer) w);
        adj[w].remove((Integer) v);
    }

    public void printEulerianPath() {
        int oddDegreeVertices = 0, startVertex = 0;
        for (int i = 0; i < adj.length; i++) {
            if (adj[i].size() % 2 != 0) {
                oddDegreeVertices++;
                startVertex = i;
            }
        }

        if (oddDegreeVertices > 2) {
            System.out.println("Eulerian Path doesn't exist");
            return;
        }

        System.out.println("Eulerian Path: ");
        printEulerUtil(startVertex);
    }

    private void printEulerUtil(int u) {
        for (int i = 0; i < adj[u].size(); i++) {
            int v = adj[u].get(i);
            if (isValidNextEdge(u, v)) {
                System.out.print(u + "-" + v + " ");

                removeEdge(u, v);
                printEulerUtil(v);
            }
        }
    }

    private boolean isValidNextEdge(int u, int v) {

        if (adj[u].size() == 1) {
            return true;
        }

        boolean[] isVisited = new boolean[this.adj.length];
        int count1 = dfsCount(u, isVisited);

        removeEdge(u, v);
        Arrays.fill(isVisited, false);
        int count2 = dfsCount(u, isVisited);

        addEdge(u, v);

        return count1 > count2;
    }

    private int dfsCount(int v, boolean[] isVisited) {
        isVisited[v] = true;
        int count = 1;
        for (int adj : this.adj[v]) {
            if (!isVisited[adj]) {
                count += dfsCount(adj, isVisited);
            }
        }
        return count;
    }

    public static void main(String[] args) {

    }
}
