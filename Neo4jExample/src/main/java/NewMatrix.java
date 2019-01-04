import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.neo4j.graphdb.*;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.io.fs.FileUtils;

/**
 * Sample code 1
 */

public class NewMatrix {
    public enum RelTypes implements RelationshipType {
        NEO_NODE,
        KNOWS,
        FRIEND,
        HELP;
    }

    public enum Labels implements Label {
        USER,
    }

    private static final File MATRIX_DB = new File("target/matrix-new-db");
    private GraphDatabaseService graphDb;
    private long matrixNodeId;
    private static DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static LocalDateTime startTime;
    private static LocalDateTime endTime;

    public static void main(String[] args) throws IOException {
        NewMatrix matrix = new NewMatrix();
        matrix.setUp();
        System.out.println("\n");
        matrix.printMatrixHackers();
        endTime = LocalDateTime.now();
        System.out.println("\n");
        System.out.println("End time is "+dtf.format(endTime));
        System.out.println("Total execution time is ="+(endTime.getSecond()-startTime.getSecond())+"s");
        matrix.shutdown();
    }

    public void setUp() throws IOException {
        FileUtils.deleteRecursively(MATRIX_DB);
        graphDb = new GraphDatabaseFactory().newEmbeddedDatabase(MATRIX_DB);
        registerShutdownHook();
        startTime = LocalDateTime.now();
        System.out.println("Start time is "+dtf.format(startTime));
        createNodespace();
    }

    public void shutdown() {
        graphDb.shutdown();
    }

    private void createNodespace() {
        try (Transaction tx = graphDb.beginTx()) {

            Node matrix = graphDb.createNode();
            matrixNodeId = matrix.getId();
            matrix.setProperty("name", "Root Node");

            Node one = graphDb.createNode(Labels.USER);
            one.setProperty("name", "1");

            Node two = graphDb.createNode(Labels.USER);
            two.setProperty("name", "2");

            Node three = graphDb.createNode(Labels.USER);
            three.setProperty("name", "3");

            Node four = graphDb.createNode(Labels.USER);
            four.setProperty("name", "4");

            Node five = graphDb.createNode(Labels.USER);
            five.setProperty("name", "5");

            Node six = graphDb.createNode(Labels.USER);
            six.setProperty("name", "6");

            Node seven = graphDb.createNode(Labels.USER);
            seven.setProperty("name", "7");

            Node eight = graphDb.createNode(Labels.USER);
            eight.setProperty("name", "8");

            Node nine = graphDb.createNode(Labels.USER);
            nine.setProperty("name", "9");

            Node ten = graphDb.createNode(Labels.USER);
            ten.setProperty("name", "10");

            Node eleven = graphDb.createNode(Labels.USER);
            eleven.setProperty("name", "11");

            Node twelve = graphDb.createNode(Labels.USER);
            twelve.setProperty("name", "12");

            Node thirteen = graphDb.createNode(Labels.USER);
            thirteen.setProperty("name", "13");

            Node fourteen = graphDb.createNode(Labels.USER);
            fourteen.setProperty("name", "14");


            matrix.createRelationshipTo(one, RelTypes.NEO_NODE);

            Relationship relationship1 = one.createRelationshipTo(two, RelTypes.HELP);
            relationship1.setProperty("relName", " HELP ");

            Relationship relationship2 = one.createRelationshipTo(three, RelTypes.KNOWS);
            relationship2.setProperty("relName", " KNOWS ");

            Relationship relationship3 = two.createRelationshipTo(four, RelTypes.FRIEND);
            relationship3.setProperty("relName", " FRIEND ");

            Relationship relationship4 = two.createRelationshipTo(five, RelTypes.KNOWS);
            relationship4.setProperty("relName", " KNOWS ");

            Relationship relationship5 = three.createRelationshipTo(six, RelTypes.FRIEND);
            relationship5.setProperty("relName", " FRIEND ");

            Relationship relationship6 = three.createRelationshipTo(seven, RelTypes.KNOWS);
            relationship6.setProperty("relName", " KNOWS ");

            Relationship relationship7 = five.createRelationshipTo(eight, RelTypes.FRIEND);
            relationship7.setProperty("relName", " FRIEND ");

            Relationship relationship8 = six.createRelationshipTo(eight, RelTypes.FRIEND);
            relationship8.setProperty("relName", " FRIEND ");

            Relationship relationship9 = eight.createRelationshipTo(nine, RelTypes.HELP);
            relationship9.setProperty("relName", " HELP ");

            Relationship relationship10 = eight.createRelationshipTo(ten, RelTypes.FRIEND);
            relationship10.setProperty("relName", " FRIEND ");

            Relationship relationship11 = ten.createRelationshipTo(nine, RelTypes.FRIEND);
            relationship11.setProperty("relName", " FRIEND ");

            Relationship relationship12 = nine.createRelationshipTo(eleven, RelTypes.HELP);
            relationship12.setProperty("relName", " HELP ");

            Relationship relationship13 = nine.createRelationshipTo(twelve, RelTypes.KNOWS);
            relationship13.setProperty("relName", " KNOWS ");

            Relationship relationship14 = twelve.createRelationshipTo(thirteen, RelTypes.HELP);
            relationship14.setProperty("relName", " HELP ");

            Relationship relationship15 = twelve.createRelationshipTo(fourteen, RelTypes.FRIEND);
            relationship15.setProperty("relName", " FRIEND ");

            tx.success();
        }
    }


    public void printMatrixHackers() {
        try (Transaction tx = graphDb.beginTx()) {
            Node startNode = graphDb.findNode(Labels.USER,"name","1");
            while(startNode.hasRelationship(Direction.OUTGOING)){
                if(startNode.hasRelationship(Direction.OUTGOING,RelTypes.FRIEND)) {
                    for (Relationship relationship : startNode.getRelationships(Direction.OUTGOING, RelTypes.FRIEND)) {
                        try {
                            Node endNode = relationship.getOtherNode(startNode);
                            System.out.println(startNode.getProperties("name") + " -> " + endNode.getProperty("name"));
                            startNode = endNode;
                        }catch (NotFoundException e){
                            System.out.println("No more relationships to endnode");
                        }
                    }
                }else if(startNode.hasRelationship(Direction.OUTGOING,RelTypes.KNOWS)){
                    for (Relationship relationship : startNode.getRelationships(Direction.OUTGOING, RelTypes.KNOWS)) {
                        try {
                            Node endNode = relationship.getOtherNode(startNode);
                            System.out.println(startNode.getProperties("name") + " -> " + endNode.getProperty("name"));
                            startNode = endNode;
                        }catch (NotFoundException e){
                            System.out.println("No more relationships to endnode");
                        }
                    }
                }
            }
        }
    }

    private void registerShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> graphDb.shutdown()));
    }
}
