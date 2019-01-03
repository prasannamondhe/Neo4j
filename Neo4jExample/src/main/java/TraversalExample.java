import org.neo4j.graphdb.*;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.traversal.Evaluators;
import org.neo4j.graphdb.traversal.TraversalDescription;
import org.neo4j.graphdb.traversal.Uniqueness;
import org.neo4j.io.fs.FileUtils;

import java.io.File;
import java.io.IOException;

/**
 * Created by Prasanna.Mondhe on 1/3/2019.
 */
public class TraversalExample {

    private GraphDatabaseService db;
    private TraversalDescription friendsTraversal;

    private static final File databaseDirectory = new File( "target/neo4j-traversal-example" );

    public static void main( String[] args ) throws IOException
    {
        FileUtils.deleteRecursively( databaseDirectory );
        GraphDatabaseService database = new GraphDatabaseFactory().newEmbeddedDatabase( databaseDirectory );
        TraversalExample example = new TraversalExample( database );
        Node joe = example.createData();
        example.run( joe );
        database.shutdown();
    }

    public TraversalExample(GraphDatabaseService db )
    {
        this.db = db;
        // tag::basetraverser[]
        friendsTraversal = db.traversalDescription()
                .depthFirst()
                .relationships( Rels.KNOWS )
                .uniqueness( Uniqueness.RELATIONSHIP_GLOBAL );
        // end::basetraverser[]
    }

    private Node createData()
    {
        String query = "CREATE (joe {name: 'Joe'}), (sara {name: 'Sara'}), "
                + "(lisa {name: 'Lisa'}), (peter {name: 'PETER'}), (dirk {name: 'Dirk'}), "
                + "(lars {name: 'Lars'}), (ed {name: 'Ed'}),"
                + "(joe)-[:KNOWS]->(sara), (lisa)-[:LIKES]->(joe), "
                + "(peter)-[:KNOWS]->(sara), (dirk)-[:KNOWS]->(peter), "
                + "(lars)-[:KNOWS]->(drk), (ed)-[:KNOWS]->(lars), "
                + "(lisa)-[:KNOWS]->(lars) "
                + "RETURN joe";
        Result result = db.execute( query );
        Object joe = result.columnAs( "joe" ).next();
        if ( joe instanceof Node )
        {
            return (Node) joe;
        }
        else
        {
            throw new RuntimeException( "Joe isn't a node!" );
        }
    }

    private void run( Node joe )
    {
        try (Transaction tx = db.beginTx())
        {
            System.out.println( knowsLikesTraverser( joe ) );
            System.out.println( traverseBaseTraverser( joe ) );
            System.out.println( depth3( joe ) );
            System.out.println( depth4( joe ) );
            System.out.println( nodes( joe ) );
            System.out.println( relationships( joe ) );
        }
    }

    public String knowsLikesTraverser( Node node )
    {
        String output = "";
        // tag::knowslikestraverser[]
        for ( Path position : db.traversalDescription()
                .depthFirst()
                .relationships( Rels.KNOWS )
                .relationships( Rels.LIKES, Direction.INCOMING )
                .evaluator( Evaluators.toDepth( 5 ) )
                .traverse( node ) )
        {
            output += position + "\n";
        }
        // end::knowslikestraverser[]
        return output;
    }

    public String traverseBaseTraverser( Node node )
    {
        String output = "";
        // tag::traversebasetraverser[]
        for ( Path path : friendsTraversal.traverse( node ) )
        {
            output += path + "\n";
        }
        // end::traversebasetraverser[]
        return output;
    }

    public String depth3( Node node )
    {
        String output = "";
        // tag::depth3[]
        for ( Path path : friendsTraversal
                .evaluator( Evaluators.toDepth( 3 ) )
                .traverse( node ) )
        {
            output += path + "\n";
        }
        // end::depth3[]
        return output;
    }

    public String depth4( Node node )
    {
        String output = "";
        // tag::depth4[]
        for ( Path path : friendsTraversal
                .evaluator( Evaluators.fromDepth( 2 ) )
                .evaluator( Evaluators.toDepth( 4 ) )
                .traverse( node ) )
        {
            output += path + "\n";
        }
        // end::depth4[]
        return output;
    }

    public String nodes( Node node )
    {
        String output = "";
        // tag::nodes[]
        for ( Node currentNode : friendsTraversal
                .traverse( node )
                .nodes() )
        {
            output += currentNode.getProperty( "name" ) + "\n";
        }
        // end::nodes[]
        return output;
    }

    public String relationships( Node node )
    {
        String output = "";
        // tag::relationships[]
        for ( Relationship relationship : friendsTraversal
                .traverse( node )
                .relationships() )
        {
            output += relationship.getType().name() + "\n";
        }
        // end::relationships[]
        return output;
    }

    // tag::sourceRels[]
    private enum Rels implements RelationshipType
    {
        LIKES, KNOWS
    }
    // end::sourceRels[]
}
