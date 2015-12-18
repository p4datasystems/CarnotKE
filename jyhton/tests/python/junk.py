conn = connectTo 'jdbc:oracle:thin:@sayonara.microlab.cs.utexas.edu:1521:orcl' 'C##cs329e_UTEid' 'orcl_UTEid' 'rdf_mode' 'A0';
# print Neo4j on conn "MATCH(a:emp)-[:dept]->(b:dept)  RETURN b.dname, a.ename"
# print SIM on conn "FROM emp RETRIEVE *"
print SQL on conn "select * from emp"