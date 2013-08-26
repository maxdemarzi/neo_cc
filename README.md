Neo4j Connected Components
================================

This is an unmanaged extension that labels the connected components of a graph.

1a. Build it: 

        mvn clean package

1b. If you don't have Maven, you can just use the pre-built jar at the root of this repository.

2. Copy target/unmanaged-extension-template-1.0.jar to the plugins/ directory of your Neo4j server.

3. Configure Neo4j by adding a line to conf/neo4j-server.properties:

        org.neo4j.server.thirdparty_jaxrs_classes=org.neo4j.cc=/ext

4. Start Neo4j server.

5. Query it over HTTP:

        curl http://localhost:7474/ext/service/cc/KNOWS
        or in the http console: get /ext/service/cc/KNOWS


