package org.neo4j.cc;

import org.codehaus.jackson.map.ObjectMapper;
import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.cypher.javacompat.ExecutionResult;
import org.neo4j.graphdb.*;
import org.neo4j.graphdb.traversal.Evaluators;
import org.neo4j.kernel.Uniqueness;
import org.neo4j.tooling.GlobalGraphOperations;
import org.neo4j.graphdb.traversal.Traverser;
import org.neo4j.kernel.Traversal;


import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Path("/service")
public class MyService {

    ObjectMapper objectMapper = new ObjectMapper();

    @GET
    @Path("/cc/{name}")
    public String getConnectedComponentsCount(@PathParam("name") String name, @Context GraphDatabaseService db) throws IOException {
        int CCid = 0;
        for ( Node n : GlobalGraphOperations.at( db ).getAllNodes() ) {
            if(!n.hasProperty("CCId")) {
                Transaction tx = db.beginTx();
                try {
                    Traverser traverser = Traversal.description()
                            .breadthFirst()
                            .relationships(DynamicRelationshipType.withName(name), Direction.BOTH)
                            .evaluator(Evaluators.excludeStartPosition())
                            .uniqueness(Uniqueness.NODE_GLOBAL)
                            .traverse(n);
                    int currentCCid = CCid;
                    CCid++;
                    n.setProperty("CCId", currentCCid);
                    for ( org.neo4j.graphdb.Path p : traverser )
                    {
                        p.endNode().setProperty("CCId", currentCCid);
                    }
                    tx.success();
                }
                catch ( Exception e )
                {
                    tx.failure();
                }
                finally
                {
                    tx.finish();
                }
            }
        }
        return String.valueOf(CCid);
    }
}

/*
Iterable<Node> allNodes = graphDbService.getAllNodes();
> > >> >        ArrayList labelled = new ArrayList();
> > >> >        for (Node currentNode : allNodes) {
        > > >> >            if (currentNode.hasProperty("number") &&
        > > >> > !labelled.contains(currentNode.getId())) {
        > > >> >                Traverser traverser =
        > > >> > currentNode.traverse(Order.BREADTH_FIRST,
        > > >> >                        StopEvaluator.END_OF_GRAPH,
        > > >> >                        ReturnableEvaluator.ALL_BUT_START_NODE,
        > > >> >                        RelTypes.CALLS, Direction.BOTH);
> > >> >                int currentID = initialID;
> > >> >                initialID++;
> > >> >                currentNode.setProperty("componentID", currentID);
> > >> >                labelled.add(currentNode.getId());
> > >> >                for (Node friend : traverser) {
        > > >> >                    friend.setProperty("componentID", currentID);
> > >> >                    // mark each node as labelled
        > > >> >                    labelled.add(friend.getId());
> > >> >                }
        > > >> >           }
        > > >> >     }

Would it be possible for you to replace
> > >> the !labelled.contains(currentNode.getId())-check with
> > >> currentNode.getProperty("componentID",null) == null? Or are there
> > >> situations
> > >> where the node could have that property and not be considered labeled?
*/