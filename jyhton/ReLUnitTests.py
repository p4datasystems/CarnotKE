import unittest

connOracleRDFNoSQL = connectTo 'OracleNoSQL' 'WDB' 'localhost:5010' 'native_mode' 'A0' 
connOracleRDFNoSQL = connectTo 'OracleNoSQL' 'WDB' 'localhost:5010' 'rdf_mode' 'A0' 
connOracleRDFNoSQL = connectTo 'OracleNoSQL' 'WDB' 'localhost:5010' 'rdf_mode' 'A0' nodebug

print "Connections are opened, start loading Databases"

# A heterogeneous set of inserts is used for testing RDF NoSQL
SQL on connOracleRDFNoSQL "INSERT INTO EMP (EMPNO, ENAME, JOB, MGR, HIREDATE, SAL, COMM, DEPTNO) VALUES (7369, 'SMITH',  'CLERK',     7902, TO_DATE('17-DEC-1980', 'DD-MON-YYYY'),  800, NULL, 20);"
SQL on connOracleRDFNoSQL "INSERT INTO EMP (EMPNO, ENAME, JOB, MGR, HIREDATE, SAL, COMM, DEPTNO) VALUES (7499, 'ALLEN',  'SALESMAN',  7698, TO_DATE('20-FEB-1981', 'DD-MON-YYYY'), 1600,  300, 30);"
SQL on connOracleRDFNoSQL "INSERT INTO EMP (EMPNO, ENAME, JOB, MGR, HIREDATE, SAL, COMM, DEPTNO) VALUES (7521, 'WARD',   'SALESMAN',  7698, TO_DATE('22-FEB-1981', 'DD-MON-YYYY'), 1250,  500, 30);"
SQL on connOracleRDFNoSQL "INSERT INTO EMP (EMPNO, ENAME, JOB, MGR, HIREDATE, SAL, COMM, DEPTNO) VALUES (7566, 'JONES',  'MANAGER',   7839, TO_DATE('2-APR-1981', 'DD-MON-YYYY'),  2975, NULL, 20);"
SQL on connOracleRDFNoSQL "INSERT INTO EMP (EMPNO, ENAME, JOB, MGR, HIREDATE, SAL, COMM, DEPTNO) VALUES (7654, 'MARTIN', 'SALESMAN',  7698, TO_DATE('28-SEP-1981', 'DD-MON-YYYY'), 1250, 1400, 30);"
SQL on connOracleRDFNoSQL "INSERT INTO EMP (EMPNO, ENAME, JOB, MGR, HIREDATE, SAL, COMM, DEPTNO) VALUES (7698, 'BLAKE',  'MANAGER',   7839, TO_DATE('1-MAY-1981', 'DD-MON-YYYY'),  2850, NULL, 30);"
SQL on connOracleRDFNoSQL "INSERT INTO EMP (EMPNO, ENAME, JOB, MGR, HIREDATE, SAL, COMM, DEPTNO) VALUES (7782, 'CLARK',  'MANAGER',   7839, TO_DATE('9-JUN-1981', 'DD-MON-YYYY'),  2450, NULL, 10);"
SQL on connOracleRDFNoSQL "INSERT INTO EMP (EMPNO, ENAME, JOB, MGR, HIREDATE, SAL, COMM, DEPTNO) VALUES (7788, 'SCOTT',  'ANALYST',   7566, TO_DATE('09-DEC-1982', 'DD-MON-YYYY'), 3000, NULL, 20);"
SQL on connOracleRDFNoSQL "INSERT INTO EMP (EMPNO, ENAME, JOB, MGR, HIREDATE, SAL, COMM, DEPTNO) VALUES (7839, 'KING',   'PRESIDENT', NULL, TO_DATE('17-NOV-1981', 'DD-MON-YYYY'), 5000, NULL, 10);"
SQL on connOracleRDFNoSQL "INSERT INTO EMP (EMPNO, ENAME, JOB, MGR, HIREDATE, SAL, COMM, DEPTNO) VALUES (7844, 'TURNER', 'SALESMAN',  7698, TO_DATE('8-SEP-1981', 'DD-MON-YYYY'),  1500, NULL, 30);"
SQL on connOracleRDFNoSQL "INSERT INTO EMP (EMPNO, ENAME, JOB, MGR, HIREDATE, SAL, COMM, DEPTNO) VALUES (7876, 'ADAMS',  'CLERK',     7788, TO_DATE('12-JAN-1983', 'DD-MON-YYYY'), 1100, NULL, 20);"
SQL on connOracleRDFNoSQL "INSERT INTO EMP (EMPNO, ENAME, JOB, MGR, HIREDATE, SAL, COMM, DEPTNO) VALUES (7900, 'JAMES',  'CLERK',     7698, TO_DATE('3-DEC-1981', 'DD-MON-YYYY'),   950, NULL, 30);"
SQL on connOracleRDFNoSQL "INSERT INTO EMP (EMPNO, ENAME, JOB, MGR, HIREDATE, SAL, COMM, DEPTNO) VALUES (7902, 'FORD',   'ANALYST',   7566, TO_DATE('3-DEC-1981', 'DD-MON-YYYY'),  3000, NULL, 20);"
SQL on connOracleRDFNoSQL "INSERT INTO EMP (EMPNO, ENAME, JOB, MGR, HIREDATE, SAL, COMM, DEPTNO) VALUES (7934, 'MILLER', 'CLERK',     7782, TO_DATE('23-JAN-1982', 'DD-MON-YYYY'), 1300, NULL, 50);"

Neo4j on connOracleRDFNoSQL "CREATE (:dept { DEPTNO : 10, DNAME : 'ACCOUNTING', LOC : 'NEW YORK' })"
Neo4j on connOracleRDFNoSQL "CREATE (:dept { DEPTNO : 20, DNAME : 'RESEARCH', LOC : 'DALLAS' })"
Neo4j on connOracleRDFNoSQL "CREATE (:dept { DEPTNO : 30, DNAME : 'SALES', LOC : 'CHICAGO' })"
Neo4j on connOracleRDFNoSQL "CREATE (:dept { DEPTNO : 40, DNAME : 'OPERATIONS', LOC : 'BOSTON' })"

Neo4j on connOracleRDFNoSQL "MATCH (a:emp),(b:dept) WHERE a.deptno = 10 AND b.deptno = 10 CREATE (a)<-[:employees]-(b)"
Neo4j on connOracleRDFNoSQL "MATCH (a:emp),(b:dept) WHERE a.deptno = 20 AND b.deptno = 20 CREATE (a)<-[:employees]-(b)"
Neo4j on connOracleRDFNoSQL "MATCH (a:emp),(b:dept) WHERE a.deptno = 30 AND b.deptno = 30 CREATE (a)<-[:employees]-(b)"
Neo4j on connOracleRDFNoSQL "MATCH (a:emp),(b:dept) WHERE a.deptno = 40 AND b.deptno = 40 CREATE (a)<-[:employees]-(b)"

Neo4j on connOracleRDFNoSQL "MATCH (a:emp),(b:dept) WHERE a.deptno = 10 AND b.deptno = 10 CREATE (a)-[:dept]->(b)"
Neo4j on connOracleRDFNoSQL "MATCH (a:emp),(b:dept) WHERE a.deptno = 20 AND b.deptno = 20 CREATE (a)-[:dept]->(b)"
Neo4j on connOracleRDFNoSQL "MATCH (a:emp),(b:dept) WHERE a.deptno = 30 AND b.deptno = 30 CREATE (a)-[:dept]->(b)"
Neo4j on connOracleRDFNoSQL "MATCH (a:emp),(b:dept) WHERE a.deptno = 40 AND b.deptno = 40 CREATE (a)-[:dept]->(b)"

print "Finished loading the NoSQL Database"

class OracleNoSQLTestCase_Neo4J_where(unittest.TestCase):
    def runTest(self):
        results = Neo4j on connOracleRDFNoSQL "MATCH(a:emp) WHERE a.deptno = 20 RETURN a.ename"
        assert sorted(results) == [('ADAMS',), ('FORD',), ('JONES',), ('SCOTT',), ('SMITH',), ('ename',)], 'OracleNoSQLTestCase_Neo4J_where query failed'

class OracleNoSQLTestCase_SIM_select(unittest.TestCase):
    def runTest(self):
        results = SIM on connOracleRDFNoSQL "FROM emp RETRIEVE *"
        assert sorted(results) == [(7566, 7788, 'NULL', 'SCOTT', 20, '09-DEC-1982,', 3000, 'ANALYST'), (7566, 7902, 'NULL', 'FORD', 20, '3-DEC-1981,', 3000, 'ANALYST'), (7698, 7499, 300, 'ALLEN', 30, '20-FEB-1981,', 1600, 'SALESMAN'), (7698, 7521, 500, 'WARD', 30, '22-FEB-1981,', 1250, 'SALESMAN'), (7698, 7654, 1400, 'MARTIN', 30, '28-SEP-1981,', 1250, 'SALESMAN'), (7698, 7844, 'NULL', 'TURNER', 30, '8-SEP-1981,', 1500, 'SALESMAN'), (7698, 7900, 'NULL', 'JAMES', 30, '3-DEC-1981,', 950, 'CLERK'), (7782, 7934, 'NULL', 'MILLER', 50, '23-JAN-1982,', 1300, 'CLERK'), (7788, 7876, 'NULL', 'ADAMS', 20, '12-JAN-1983,', 1100, 'CLERK'), (7839, 7566, 'NULL', 'JONES', 20, '2-APR-1981,', 2975, 'MANAGER'), (7839, 7698, 'NULL', 'BLAKE', 30, '1-MAY-1981,', 2850, 'MANAGER'), (7839, 7782, 'NULL', 'CLARK', 10, '9-JUN-1981,', 2450, 'MANAGER'), (7902, 7369, 'NULL', 'SMITH', 20, '17-DEC-1980,', 800, 'CLERK'), ('NULL', 7839, 'NULL', 'KING', 10, '17-NOV-1981,', 5000, 'PRESIDENT'), ('mgr', 'empno', 'comm', 'ename', 'deptno', 'hiredate', 'sal', 'job')], 'OracleNoSQLTestCase_SIM_select query failed'

class OracleNoSQLTestCase_SIM_join(unittest.TestCase):
    def runTest(self):
        results = SIM on connOracleRDFNoSQL "FROM emp RETRIEVE dname OF dept, ename"
        assert sorted(results) == [('ADAMS', 'RESEARCH'), ('ALLEN', 'SALES'), ('BLAKE', 'SALES'), ('CLARK', 'ACCOUNTING'), ('FORD', 'RESEARCH'), ('JAMES', 'SALES'), ('JONES', 'RESEARCH'), ('KING', 'ACCOUNTING'), ('MARTIN', 'SALES'), ('MILLER',), ('SCOTT', 'RESEARCH'), ('SMITH', 'RESEARCH'), ('TURNER', 'SALES'), ('WARD', 'SALES'), ('ename', 'x0_1')], 'OracleNoSQLTestCase_SIM_join query failed'

class OracleNoSQLTestCase_SIM_where(unittest.TestCase):
    def runTest(self):
        results = SIM on connOracleRDFNoSQL "FROM EMP RETRIEVE ENAME where deptno = 20"
        assert sorted(results) == [('ADAMS',), ('FORD',), ('JONES',), ('SCOTT',), ('SMITH',), ('ename',)], 'OracleNoSQLTestCase_SIM_where query failed'

class OracleNoSQLTestCase_SQL_project(unittest.TestCase):
    def runTest(self):
        results = SQL on connOracleRDFNoSQL "select ename, sal from emp order by ename"
        assert sorted(results) == [('ADAMS', 1100), ('ALLEN', 1600), ('BLAKE', 2850), ('CLARK', 2450), ('FORD', 3000), ('JAMES', 950), ('JONES', 2975), ('KING', 5000), ('MARTIN', 1250), ('MILLER', 1300), ('SCOTT', 3000), ('SMITH', 800), ('TURNER', 1500), ('WARD', 1250), ('ename', 'sal')], 'OracleNoSQLTestCase_SIM_project query failed'

class OracleNoSQLTestCase_SQL_join(unittest.TestCase):
    def runTest(self):
        results = SQL on connOracleRDFNoSQL "select ename, sal, dname, loc from emp e join dept d on (e.deptno=d.deptno)"
        assert sorted(results) == [('ADAMS', 1100, 'RESEARCH', 'DALLAS'), ('ALLEN', 1600, 'SALES', 'CHICAGO'), ('BLAKE', 2850, 'SALES', 'CHICAGO'), ('CLARK', 2450, 'ACCOUNTING', 'NEW YORK'), ('FORD', 3000, 'RESEARCH', 'DALLAS'), ('JAMES', 950, 'SALES', 'CHICAGO'), ('JONES', 2975, 'RESEARCH', 'DALLAS'), ('KING', 5000, 'ACCOUNTING', 'NEW YORK'), ('MARTIN', 1250, 'SALES', 'CHICAGO'), ('SCOTT', 3000, 'RESEARCH', 'DALLAS'), ('SMITH', 800, 'RESEARCH', 'DALLAS'), ('TURNER', 1500, 'SALES', 'CHICAGO'), ('WARD', 1250, 'SALES', 'CHICAGO'), ('ename', 'sal', 'dname', 'loc')], 'OracleNoSQLTestCase_SIM_join query failed'

class OracleNoSQLTestCase_SPARQL_join(unittest.TestCase):
    def runTest(self):
        results = SPARQL on connOracleRDFNoSQL "select ?ename ?x0_1  where { GRAPH c:emp_SCHEMA { ?indiv rdf:type c:emp } GRAPH c:emp {  ?indiv c:ename ?ename . }  OPTIONAL { GRAPH ?0 { ?indiv c:dept ?x0_0 . } GRAPH ?1 { ?x0_0 c:dname ?x0_1 . }  }  }"
        assert sorted(results) == [('ADAMS', 'RESEARCH'), ('ALLEN', 'SALES'), ('BLAKE', 'SALES'), ('CLARK', 'ACCOUNTING'), ('FORD', 'RESEARCH'), ('JAMES', 'SALES'), ('JONES', 'RESEARCH'), ('KING', 'ACCOUNTING'), ('MARTIN', 'SALES'), ('MILLER',), ('SCOTT', 'RESEARCH'), ('SMITH', 'RESEARCH'), ('TURNER', 'SALES'), ('WARD', 'SALES'), ('ename', 'x0_1')], 'OracleNoSQLTestCase_SPARQL_join query failed'

if __name__ == "__main__":
    unittest.main()
