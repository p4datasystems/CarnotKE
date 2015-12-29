import unittest

conn1 = connectTo 'jdbc:oracle:thin:@sayonara.microlab.cs.utexas.edu:1521:orcl' 'C##cs329e_UTEid' 'orcl_UTEid' 'rdf_mode' 'A0' nodebug

class SIMTestCase(unittest.TestCase):
    def runTest(self):
        results = SIM on conn1 "From emp Retrieve ename"
        assert results == (('ENAME',), ('JONES',), ('SMITH',), ('ALLEN',), ('SCOTT',), ('ADAMS',), ('MILLER',), ('JAMES',), ('CLARK',), ('FORD',), ('WARD',), ('BLAKE',), ('TURNER',), ('MARTIN',), ('KING',)), 'SIM query failed'

class Neo4jTestCase(unittest.TestCase):
    def runTest(self):
        results = Neo4j on conn1 "MATCH(a:emp)-[:dept]->(b:dept) RETURN b.dname, a.ename"
        assert results == (('ENAME', 'X0_1'), ('FORD', 'RESEARCH'), ('WARD', 'SALES'), ('CLARK', 'ACCOUNTING'), ('SMITH', 'RESEARCH'), ('ALLEN', 'SALES'), ('MARTIN', 'SALES'), ('SCOTT', 'RESEARCH'), ('JAMES', 'SALES'), ('KING', 'ACCOUNTING'), ('TURNER', 'SALES'), ('ADAMS', 'RESEARCH'), ('MILLER', 'null'), ('BLAKE', 'SALES'), ('JONES', 'RESEARCH')), 'Neo4j query failed'

conn2 = connectTo 'OracleNoSQL' 'C##cs329e_UTEid' 'orcl_UTEid' 'rdf_mode' 'A0'

class OracleNoSQLTestCase(unittest.TestCase):
    def runTest(self):
        results = Neo4j on conn2 "MATCH(a:emp)-[:dept]->(b:dept) RETURN b.dname, a.ename"
        assert results == (('NoSQL',),), 'Oracle NoSQL query failed'

if __name__ == "__main__":
    unittest.main()
