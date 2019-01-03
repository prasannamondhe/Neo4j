import java.io.File;
import java.io.IOException;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.traversal.Evaluators;
import org.neo4j.graphdb.traversal.TraversalDescription;
import org.neo4j.graphdb.traversal.Traverser;
import org.neo4j.io.fs.FileUtils;

/**
 * Sample code 1
 */

public class NewMatrix {
    public enum RelTypes implements RelationshipType {
        NEO_NODE,
        KNOWS,
        CODED_BY,
        HELP;
    }

    private static final File MATRIX_DB = new File("target/matrix-new-db");
    private GraphDatabaseService graphDb;
    private long matrixNodeId;

    public static void main(String[] args) throws IOException {
        NewMatrix matrix = new NewMatrix();
        matrix.setUp();
        System.out.println(matrix.printMatrixHackers());
        System.out.println(matrix.traversalPath());
        matrix.shutdown();
    }

    public void setUp() throws IOException {
        FileUtils.deleteRecursively(MATRIX_DB);
        graphDb = new GraphDatabaseFactory().newEmbeddedDatabase(MATRIX_DB);
        registerShutdownHook();
        createNodespace();
    }

    public void shutdown() {
        graphDb.shutdown();
    }

    private void createNodespace() {
        try (Transaction tx = graphDb.beginTx()) {
            // Create matrix node
            Node matrix = graphDb.createNode();
            matrixNodeId = matrix.getId();

            // Create Neo
            Node one = graphDb.createNode();
            one.setProperty("name", "1");

            // connect Neo/Thomas to the matrix node
            matrix.createRelationshipTo(one, RelTypes.NEO_NODE);

            Node two = graphDb.createNode();
            two.setProperty("name", "2");

            // connect Neo/Thomas to the matrix node
            Relationship relationship1 = one.createRelationshipTo(two, RelTypes.CODED_BY);
            relationship1.setProperty("relName", " CODED_BY ");


            Node three = graphDb.createNode();
            three.setProperty("name", "3");

            // connect Neo/Thomas to the matrix node
            Relationship relationship2 = two.createRelationshipTo(three, RelTypes.HELP);
            relationship2.setProperty("relName", " HELP ");

            Node four = graphDb.createNode();
            four.setProperty("name", "4");

            // connect Neo/Thomas to the matrix node
            Relationship relationship3 = two.createRelationshipTo(four, RelTypes.CODED_BY);
            relationship3.setProperty("relName", " CODED_BY ");


            Node five = graphDb.createNode();
            five.setProperty("name", "5");

            // connect Neo/Thomas to the matrix node
            Relationship relationship4 = four.createRelationshipTo(five, RelTypes.KNOWS);
            relationship4.setProperty("relName", " KNOWS ");


            Node six = graphDb.createNode();
            six.setProperty("name", "6");

            // connect Neo/Thomas to the matrix node
            Relationship relationship5 = four.createRelationshipTo(six, RelTypes.HELP);
            relationship5.setProperty("relName", " HELP ");

            Node seven = graphDb.createNode();
            seven.setProperty("name", "7");

            // connect Neo/Thomas to the matrix node
            Relationship relationship6 = five.createRelationshipTo(seven, RelTypes.HELP);
            relationship6.setProperty("relName", " HELP ");

            Node eight = graphDb.createNode();
            eight.setProperty("name", "8");

            // connect Neo/Thomas to the matrix node
            Relationship relationship7 = five.createRelationshipTo(eight, RelTypes.CODED_BY);
            relationship7.setProperty("relName", " CODED_BY ");

//            Node nine = graphDb.createNode();
//            nine.setProperty("name", "9");
//
//            // connect Neo/Thomas to the matrix node
//            Relationship relationship8 = six.createRelationshipTo(nine, RelTypes.CODED_BY);
//            relationship8.setProperty("relName", " CODED_BY ");
//
//            Node ten = graphDb.createNode();
//            ten.setProperty("name", "10");
//
//            // connect Neo/Thomas to the matrix node
//            Relationship relationship9 = six.createRelationshipTo(ten, RelTypes.KNOWS);
//            relationship9.setProperty("relName", " KNOWS ");


            tx.success();
        }
    }

    /**
     * Get the Neo node. (a.k.a. Thomas Anderson node)
     *
     * @return the Neo node
     */
    private Node getNeoNode() {
        return graphDb.getNodeById(matrixNodeId)
                .getSingleRelationship(RelTypes.NEO_NODE, Direction.OUTGOING)
                .getEndNode();
    }


    public String printMatrixHackers() {
        try (Transaction tx = graphDb.beginTx()) {
            String output = "Hackers:\n";
            int numberOfHackers = 0;
            Traverser traverser = findHackers(getNeoNode());
            for (Path hackerPath : traverser) {

                for (Relationship relationship : hackerPath.relationships()) {

                    if (relationship.getType().name().contains(RelTypes.CODED_BY.name())) {
                        output += "At depth " + hackerPath.length() + " => "
                                + hackerPath.endNode()
                                .getProperty("name") + "\n";
                        numberOfHackers++;
                        break;
                    } else if (relationship.getType().name().contains(RelTypes.KNOWS.name())) {
                        output += "At depth " + hackerPath.length() + " => "
                                + hackerPath.endNode()
                                .getProperty("name") + "\n";
                        numberOfHackers++;
                        break;
                    }

                }
            }
            output += "Number of hackers found: " + numberOfHackers + "\n";
            return output;
        }
    }

    private Traverser findHackers(final Node startNode) {
        TraversalDescription td = graphDb.traversalDescription()
                .depthFirst()
                .relationships(RelTypes.CODED_BY, Direction.OUTGOING)
                .relationships(RelTypes.KNOWS, Direction.OUTGOING);
        //.evaluator(Evaluators.toDepth( 4 ));
        return td.traverse(startNode);
    }

    public String traversalPath() {
        try (Transaction tx = graphDb.beginTx()) {
            String output = "";
            for (Path position : graphDb.traversalDescription()
                    .depthFirst()
                    .relationships(RelTypes.CODED_BY, Direction.OUTGOING)
                    .relationships(RelTypes.KNOWS, Direction.OUTGOING)
                    .evaluator(Evaluators.toDepth(5))
                    .traverse(getNeoNode())) {
                output += position + "\n";
            }
            return output;
        }
    }

    private void registerShutdownHook() {
        // Registers a shutdown hook for the Neo4j instance so that it
        // shuts down nicely when the VM exits (even if you "Ctrl-C" the
        // running example before it's completed)
        Runtime.getRuntime().addShutdownHook(new Thread(() -> graphDb.shutdown()));
    }
}