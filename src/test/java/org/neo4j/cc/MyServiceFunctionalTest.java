package org.neo4j.cc;

import com.sun.jersey.api.client.Client;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;
import org.neo4j.graphdb.*;
import org.neo4j.server.NeoServer;
import org.neo4j.server.helpers.ServerBuilder;
import org.neo4j.server.rest.JaxRsResponse;
import org.neo4j.server.rest.RestRequest;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class MyServiceFunctionalTest {

    public static final Client CLIENT = Client.create();
    public static final String MOUNT_POINT = "/ext";
    private ObjectMapper objectMapper = new ObjectMapper();

    private static final RelationshipType KNOWS = DynamicRelationshipType.withName("KNOWS");

    @Test
    public void shouldReturnConnectedComponentCount() throws IOException {
        NeoServer server = ServerBuilder.server()
                .withThirdPartyJaxRsPackage("org.neo4j.cc", MOUNT_POINT)
                .build();
        server.start();
        populateDb(server.getDatabase().getGraph());
        RestRequest restRequest = new RestRequest(server.baseUri().resolve(MOUNT_POINT), CLIENT);
        JaxRsResponse response = restRequest.get("service/cc/KNOWS");
        assertEquals("2", response.getEntity());
        server.stop();

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

}
