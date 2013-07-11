package org.neo4j.cc;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.neo4j.graphdb.*;
import org.neo4j.test.TestGraphDatabaseFactory;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class MyServiceTest {

    private GraphDatabaseService db;
    private MyService service;
    private ObjectMapper objectMapper = new ObjectMapper();
    private static final RelationshipType KNOWS = DynamicRelationshipType.withName("KNOWS");
    private static final RelationshipType HATES = DynamicRelationshipType.withName("HATES");

    @Before
    public void setUp() {
        db = new TestGraphDatabaseFactory().newImpermanentDatabase();
        dropRootNode(db);
        populateDb(db);
        service = new MyService();
    }

    private void dropRootNode(GraphDatabaseService db){
        Transaction tx = db.beginTx();
        try
        {
            Node root = db.getNodeById(0);
            root.delete();
            tx.success();
        }
        finally
        {
            tx.finish();
        }

    }

    private void populateDb(GraphDatabaseService db) {
        Transaction tx = db.beginTx();
        try
        {
            Node personA = createPerson(db, "A");
            Node personB = createPerson(db, "B");
            Node personC = createPerson(db, "C");
            Node personD = createPerson(db, "D");
            personA.createRelationshipTo(personB, KNOWS);
            personB.createRelationshipTo(personC, KNOWS);
            personC.createRelationshipTo(personD, KNOWS);

            Node personAA = createPerson(db, "AA");
            Node personAB = createPerson(db, "AB");
            Node personAC = createPerson(db, "AC");
            Node personAD = createPerson(db, "AD");
            personAA.createRelationshipTo(personAB, KNOWS);
            personAB.createRelationshipTo(personAC, KNOWS);
            personAC.createRelationshipTo(personAD, KNOWS);

            Node personCA = createPerson(db, "CA");
            Node personCB = createPerson(db, "CB");
            Node personCC = createPerson(db, "CC");
            Node personCD = createPerson(db, "CD");

            personCA.createRelationshipTo(personCB, KNOWS);
            personCB.createRelationshipTo(personCC, HATES);
            personCC.createRelationshipTo(personCD, KNOWS);

            Node personDA = createPerson(db, "DA");
            Node personDB = createPerson(db, "DB");
            Node personDC = createPerson(db, "DC");
            Node personDD = createPerson(db, "DD");

            personDA.createRelationshipTo(personDB, HATES);
            personDB.createRelationshipTo(personDC, HATES);
            personDC.createRelationshipTo(personDD, HATES);


            tx.success();
        }
        finally
        {
            tx.finish();
        }
    }

    private Node createPerson(GraphDatabaseService db, String name) {
        Node node = db.createNode();
        node.setProperty("name", name);
        return node;
    }

    @After
    public void tearDown() throws Exception {
        db.shutdown();

    }

    @Test
    public void shouldGetConnectedComponentsCount() throws IOException {
        assertEquals("8", service.getConnectedComponentsCount("KNOWS", db));
    }


    public GraphDatabaseService graphdb() {
        return db;
    }
}
