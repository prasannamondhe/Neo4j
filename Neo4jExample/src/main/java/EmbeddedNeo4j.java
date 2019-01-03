/**
 * Created by Prasanna.Mondhe on 1/3/2019.
 */
/*
 * Licensed to Neo4j under one or more contributor
 * license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright
 * ownership. Neo4j licenses this file to you under
 * the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
import java.io.File;
import java.io.IOException;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.io.fs.FileUtils;

public class EmbeddedNeo4j
{
    private static final File databaseDirectory = new File( "E:\\D_Driver\\Database" );

    public String greeting;

    // tag::vars[]
    GraphDatabaseService graphDb;
    Node firstNode;
    Node secondNode;
    Node thirdNode;
    Node fourthNode;
    Node fifthNode;
    // end::vars[]

    // tag::createReltype[]
    private enum RelTypes implements RelationshipType
    {
        KNOWS,FRIEND
    }
    // end::createReltype[]

    public static void main( final String[] args ) throws IOException
    {
        EmbeddedNeo4j hello = new EmbeddedNeo4j();
        hello.createDb();
        hello.removeData();
        hello.shutDown();
    }

    void createDb() throws IOException
    {
        FileUtils.deleteRecursively( databaseDirectory );

        // tag::startDb[]
        graphDb = new GraphDatabaseFactory().newEmbeddedDatabase( databaseDirectory );
        registerShutdownHook( graphDb );
        // end::startDb[]

        // tag::transaction[]
        try ( Transaction tx = graphDb.beginTx() )
        {
            // Database operations go here
            // end::transaction[]
            // tag::addData[]
            firstNode = graphDb.createNode();
            firstNode.setProperty( "message", "First" );
            secondNode = graphDb.createNode();
            secondNode.setProperty( "message", "Second" );

            Relationship relationship1 = firstNode.createRelationshipTo( secondNode, RelTypes.KNOWS );
            relationship1.setProperty( "message", " KNOWS " );
            // end::addData[]

            thirdNode = graphDb.createNode();
            thirdNode.setProperty("message","Third");

            Relationship relationship2 = firstNode.createRelationshipTo(thirdNode,RelTypes.FRIEND);
            relationship2.setProperty("message"," FRIEND ");




            // tag::readData[]
            System.out.print( firstNode.getProperty( "message" ) );
            System.out.print( relationship1.getProperty( "message" ) );
            System.out.print( secondNode.getProperty( "message" ) );

            System.out.println("\n\n");

            System.out.print( firstNode.getProperty( "message" ) );
            System.out.print( relationship2.getProperty( "message" ) );
            System.out.print( thirdNode.getProperty( "message" ) );

            // end::readData[]

            greeting = ( (String) firstNode.getProperty( "message" ) )
                    + ( (String) relationship1.getProperty( "message" ) )
                    + ( (String) secondNode.getProperty( "message" ) );

            // tag::transaction[]
            tx.success();
        }
        // end::transaction[]
    }

    void removeData()
    {
        try ( Transaction tx = graphDb.beginTx() )
        {
            // tag::removingData[]
            // let's remove the data
            firstNode.getSingleRelationship( RelTypes.KNOWS, Direction.OUTGOING ).delete();
            firstNode.getSingleRelationship( RelTypes.FRIEND, Direction.OUTGOING ).delete();
            firstNode.delete();
            secondNode.delete();
            thirdNode.delete();
            // end::removingData[]

            tx.success();
        }
    }

    void shutDown()
    {
        System.out.println();
        System.out.println( "Shutting down database ..." );
        // tag::shutdownServer[]
        graphDb.shutdown();
        // end::shutdownServer[]
    }

    // tag::shutdownHook[]
    private static void registerShutdownHook( final GraphDatabaseService graphDb )
    {
        // Registers a shutdown hook for the Neo4j instance so that it
        // shuts down nicely when the VM exits (even if you "Ctrl-C" the
        // running application).
        Runtime.getRuntime().addShutdownHook( new Thread()
        {
            @Override
            public void run()
            {
                graphDb.shutdown();
            }
        } );
    }
    // end::shutdownHook[]
}
