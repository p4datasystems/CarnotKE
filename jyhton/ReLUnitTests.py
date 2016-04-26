import unittest

connOracleEE_native = connectTo 'jdbc:oracle:thin:@sayonara.microlab.cs.utexas.edu:1521:orcl' 'C##cs329e_UTEid' 'orcl_UTEid' 'native_mode' nodebug

SQL on connOracleEE_native "{call SEM_APIS.DROP_RDF_MODEL('A0_C##CS329E_UTEID')}"
SQL on connOracleEE_native "drop table A0_C##CS329E_UTEID_DATA;"
SQL on connOracleEE_native "DROP SEQUENCE A0_C##CS329E_UTEID_SQNC;"
SQL on connOracleEE_native "DROP SEQUENCE A0_C##CS329E_UTEID_GUID_SQNC;"

connOracleEE = connectTo 'jdbc:oracle:thin:@sayonara.microlab.cs.utexas.edu:1521:orcl' 'C##cs329e_UTEid' 'orcl_UTEid' 'rdf_mode' 'A0' nodebug
conn_native = connectTo 'jdbc:oracle:thin:@sayonara.microlab.cs.utexas.edu:1521:orcl' 'C##cs329e_UTEid' 'orcl_UTEid' 'native_mode' 'A0' nodebug
connOracleRDFNoSQL = connectTo 'OracleNoSQL' 'kvstore' 'localhost:5000' 'rdf_mode' 'A0' 
connOracleRDFNoSQL = connectTo 'OracleNoSQL' 'kvstore' 'localhost:5000' 'rdf_mode' 'A0' nodebug
global_conn = connectTo 'jdbc:oracle:thin:@sayonara.microlab.cs.utexas.edu:1521:orcl' 'C##cs329e_UTEid' 'orcl_UTEid' 'rdf_mode' 'A1' nodebug

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

# A heterogeneous set of inserts is used for testing Oracle EE
Neo4j on connOracleEE "CREATE (:emp { EMPNO : 7369, ENAME : 'SMITH', JOB : 'CLERK', MGR : 7902, HIREDATE : '17-DEC-80', SAL : 800, COMM : 0, DEPTNO : 20})"
Neo4j on connOracleEE "CREATE (:emp { EMPNO : 7499, ENAME : 'ALLEN', JOB : 'SALESMAN', MGR : 7698, HIREDATE : '20-FEB-81', SAL : 1600, COMM : 300, DEPTNO : 30})"
Neo4j on connOracleEE "CREATE (:emp { EMPNO : 7521, ENAME : 'WARD', JOB : 'SALESMAN', MGR : 7698, HIREDATE : '22-FEB-81', SAL : 1250, COMM : 500, DEPTNO : 30})"
Neo4j on connOracleEE "CREATE (:emp { EMPNO : 7566, ENAME : 'JONES', JOB : 'MANAGER', MGR : 7839, HIREDATE : '02-APR-81', SAL : 2975, COMM : 0, DEPTNO : 20})"
Neo4j on connOracleEE "CREATE (:emp { EMPNO : 7654, ENAME : 'MARTIN', JOB : 'SALESMAN', MGR : 7698, HIREDATE : '28-SEP-81', SAL : 1250, COMM : 1400, DEPTNO : 30})"
Neo4j on connOracleEE "CREATE (:emp { EMPNO : 7698, ENAME : 'BLAKE', JOB : 'MANAGER', MGR : 7839, HIREDATE : '01-MAY-81', SAL : 2850, COMM : 0, DEPTNO : 30})"
Neo4j on connOracleEE "CREATE (:emp { EMPNO : 7782, ENAME : 'CLARK', JOB : 'MANAGER', MGR : 7839, HIREDATE : '09-JUN-81', SAL : 2450, COMM : 0, DEPTNO : 10})"
Neo4j on connOracleEE "CREATE (:emp { EMPNO : 7788, ENAME : 'SCOTT', JOB : 'ANALYST', MGR : 7566, HIREDATE : '09-DEC-82', SAL : 3000, COMM : 0, DEPTNO : 20})"
Neo4j on connOracleEE "CREATE (:emp { EMPNO : 7839, ENAME : 'KING', JOB : 'PRESIDENT', MGR : 0, HIREDATE : '17-NOV-81', SAL : 5000, COMM : 0, DEPTNO : 10})"
Neo4j on connOracleEE "CREATE (:emp { EMPNO : 7844, ENAME : 'TURNER', JOB : 'SALESMAN', MGR : 7698, HIREDATE : '08-SEP-81', SAL : 1500, COMM : 0, DEPTNO : 30})"
Neo4j on connOracleEE "CREATE (:emp { EMPNO : 7876, ENAME : 'ADAMS', JOB : 'CLERK', MGR : 7788, HIREDATE : '12-JAN-83', SAL : 1100, COMM : 0, DEPTNO : 20})"
Neo4j on connOracleEE "CREATE (:emp { EMPNO : 7900, ENAME : 'JAMES', JOB : 'CLERK', MGR : 7698, HIREDATE : '03-DEC-81', SAL : 950, COMM : 0, DEPTNO : 30})"
Neo4j on connOracleEE "CREATE (:emp { EMPNO : 7902, ENAME : 'FORD', JOB : 'ANALYST', MGR : 7566, HIREDATE : '03-DEC-81', SAL : 3000, COMM : 0, DEPTNO : 20})"
Neo4j on connOracleEE "CREATE (:emp { EMPNO : 7934, ENAME : 'MILLER', JOB : 'CLERK', MGR : 7782, HIREDATE : '23-JAN-82', SAL : 1300, COMM : 0, DEPTNO : 50})"

SQL on connOracleEE "INSERT INTO DEPT (DEPTNO, DNAME, LOC) VALUES (10, 'ACCOUNTING', 'NEW YORK');"
SQL on connOracleEE "INSERT INTO DEPT (DEPTNO, DNAME, LOC) VALUES (20, 'RESEARCH',   'DALLAS');"
SQL on connOracleEE "INSERT INTO DEPT (DEPTNO, DNAME, LOC) VALUES (30, 'SALES',      'CHICAGO');"
SQL on connOracleEE "INSERT INTO DEPT (DEPTNO, DNAME, LOC) VALUES (40, 'OPERATIONS', 'BOSTON');"

Neo4j on connOracleEE "MATCH (a:emp),(b:dept) WHERE a.deptno = 10 AND b.deptno = 10 CREATE (a)<-[:employees]-(b)"
Neo4j on connOracleEE "MATCH (a:emp),(b:dept) WHERE a.deptno = 20 AND b.deptno = 20 CREATE (a)<-[:employees]-(b)"
Neo4j on connOracleEE "MATCH (a:emp),(b:dept) WHERE a.deptno = 30 AND b.deptno = 30 CREATE (a)<-[:employees]-(b)"
Neo4j on connOracleEE "MATCH (a:emp),(b:dept) WHERE a.deptno = 40 AND b.deptno = 40 CREATE (a)<-[:employees]-(b)"

Neo4j on connOracleEE "MATCH (a:emp),(b:dept) WHERE a.deptno = 10 AND b.deptno = 10 CREATE (a)-[:dept]->(b)"
Neo4j on connOracleEE "MATCH (a:emp),(b:dept) WHERE a.deptno = 20 AND b.deptno = 20 CREATE (a)-[:dept]->(b)"
Neo4j on connOracleEE "MATCH (a:emp),(b:dept) WHERE a.deptno = 30 AND b.deptno = 30 CREATE (a)-[:dept]->(b)"
Neo4j on connOracleEE "MATCH (a:emp),(b:dept) WHERE a.deptno = 40 AND b.deptno = 40 CREATE (a)-[:dept]->(b)"

print "Finished loading the EE Database"

SQL on conn_native "truncate table emp"
SQL on conn_native "INSERT INTO EMP (EMPNO, ENAME, JOB, MGR, HIREDATE, SAL, COMM, DEPTNO) VALUES (7369, 'SMITH',  'CLERK',     7902, TO_DATE('17-DEC-1980', 'DD-MON-YYYY'),  800, NULL, 20);"
SQL on conn_native "INSERT INTO EMP (EMPNO, ENAME, JOB, MGR, HIREDATE, SAL, COMM, DEPTNO) VALUES (7499, 'ALLEN',  'SALESMAN',  7698, TO_DATE('20-FEB-1981', 'DD-MON-YYYY'), 1600,  300, 30);"
SQL on conn_native "INSERT INTO EMP (EMPNO, ENAME, JOB, MGR, HIREDATE, SAL, COMM, DEPTNO) VALUES (7521, 'WARD',   'SALESMAN',  7698, TO_DATE('22-FEB-1981', 'DD-MON-YYYY'), 1250,  500, 30);"
SQL on conn_native "INSERT INTO EMP (EMPNO, ENAME, JOB, MGR, HIREDATE, SAL, COMM, DEPTNO) VALUES (7566, 'JONES',  'MANAGER',   7839, TO_DATE('2-APR-1981', 'DD-MON-YYYY'),  2975, NULL, 20);"
SQL on conn_native "INSERT INTO EMP (EMPNO, ENAME, JOB, MGR, HIREDATE, SAL, COMM, DEPTNO) VALUES (7654, 'MARTIN', 'SALESMAN',  7698, TO_DATE('28-SEP-1981', 'DD-MON-YYYY'), 1250, 1400, 30);"
SQL on conn_native "INSERT INTO EMP (EMPNO, ENAME, JOB, MGR, HIREDATE, SAL, COMM, DEPTNO) VALUES (7698, 'BLAKE',  'MANAGER',   7839, TO_DATE('1-MAY-1981', 'DD-MON-YYYY'),  2850, NULL, 30);"
SQL on conn_native "INSERT INTO EMP (EMPNO, ENAME, JOB, MGR, HIREDATE, SAL, COMM, DEPTNO) VALUES (7782, 'CLARK',  'MANAGER',   7839, TO_DATE('9-JUN-1981', 'DD-MON-YYYY'),  2450, NULL, 10);"
SQL on conn_native "INSERT INTO EMP (EMPNO, ENAME, JOB, MGR, HIREDATE, SAL, COMM, DEPTNO) VALUES (7788, 'SCOTT',  'ANALYST',   7566, TO_DATE('09-DEC-1982', 'DD-MON-YYYY'), 3000, NULL, 20);"
SQL on conn_native "INSERT INTO EMP (EMPNO, ENAME, JOB, MGR, HIREDATE, SAL, COMM, DEPTNO) VALUES (7839, 'KING',   'PRESIDENT', NULL, TO_DATE('17-NOV-1981', 'DD-MON-YYYY'), 5000, NULL, 10);"
SQL on conn_native "INSERT INTO EMP (EMPNO, ENAME, JOB, MGR, HIREDATE, SAL, COMM, DEPTNO) VALUES (7844, 'TURNER', 'SALESMAN',  7698, TO_DATE('8-SEP-1981', 'DD-MON-YYYY'),  1500, NULL, 30);"
SQL on conn_native "INSERT INTO EMP (EMPNO, ENAME, JOB, MGR, HIREDATE, SAL, COMM, DEPTNO) VALUES (7876, 'ADAMS',  'CLERK',     7788, TO_DATE('12-JAN-1983', 'DD-MON-YYYY'), 1100, NULL, 20);"
SQL on conn_native "INSERT INTO EMP (EMPNO, ENAME, JOB, MGR, HIREDATE, SAL, COMM, DEPTNO) VALUES (7900, 'JAMES',  'CLERK',     7698, TO_DATE('3-DEC-1981', 'DD-MON-YYYY'),   950, NULL, 30);"
SQL on conn_native "INSERT INTO EMP (EMPNO, ENAME, JOB, MGR, HIREDATE, SAL, COMM, DEPTNO) VALUES (7902, 'FORD',   'ANALYST',   7566, TO_DATE('3-DEC-1981', 'DD-MON-YYYY'),  3000, NULL, 20);"
SQL on conn_native "INSERT INTO EMP (EMPNO, ENAME, JOB, MGR, HIREDATE, SAL, COMM, DEPTNO) VALUES (7934, 'MILLER', 'CLERK',     7782, TO_DATE('23-JAN-1982', 'DD-MON-YYYY'), 1300, NULL, 50);"

SQL on conn_native "truncate table dept"
SQL on conn_native "INSERT INTO DEPT (DEPTNO, DNAME, LOC) VALUES (10, 'ACCOUNTING', 'NEW YORK');"
SQL on conn_native "INSERT INTO DEPT (DEPTNO, DNAME, LOC) VALUES (20, 'RESEARCH',   'DALLAS');"
SQL on conn_native "INSERT INTO DEPT (DEPTNO, DNAME, LOC) VALUES (30, 'SALES',      'CHICAGO');"
SQL on conn_native "INSERT INTO DEPT (DEPTNO, DNAME, LOC) VALUES (40, 'OPERATIONS', 'BOSTON');"

print "Finished loading the Native Database"

# Start testing of Oracle EE
class EESQLTestCase(unittest.TestCase):
    def runTest(self):
        results = SQL on connOracleEE "select ename from emp"
        assert sorted(results) == [('ADAMS',), ('ALLEN',), ('BLAKE',), ('CLARK',), ('FORD',), ('JAMES',), ('JONES',), ('KING',), ('MARTIN',), ('MILLER',), ('SCOTT',), ('SMITH',), ('TURNER',), ('WARD',), ('ename',)], 'SQL query failed'

class EESIMTestCase_1(unittest.TestCase):
    def runTest(self):
        results = SIM on connOracleEE "From emp Retrieve ename"
        assert sorted(results) == [('ADAMS',), ('ALLEN',), ('BLAKE',), ('CLARK',), ('ENAME',), ('FORD',), ('JAMES',), ('JONES',), ('KING',), ('MARTIN',), ('MILLER',), ('SCOTT',), ('SMITH',), ('TURNER',), ('WARD',)], 'SIM query failed'

class EESIMTestCase_2(unittest.TestCase):
    def runTest(self):
        results = SIM on connOracleEE "FROM EMP RETRIEVE DNAME OF dept, ENAME"
        assert sorted(results) == [('ADAMS', 'RESEARCH'), ('ALLEN', 'SALES'), ('BLAKE', 'SALES'), ('CLARK', 'ACCOUNTING'), ('ENAME', 'X0_1'), ('FORD', 'RESEARCH'), ('JAMES', 'SALES'), ('JONES', 'RESEARCH'), ('KING', 'ACCOUNTING'), ('MARTIN', 'SALES'), ('MILLER', 'null'), ('SCOTT', 'RESEARCH'), ('SMITH', 'RESEARCH'), ('TURNER', 'SALES'), ('WARD', 'SALES')], 'SIM query failed'

class EENeo4jTestCase(unittest.TestCase):
    def runTest(self):
        results = Neo4j on connOracleEE "MATCH(a:emp)-[:dept]->(b:dept) RETURN b.dname, a.ename"
        assert sorted(results) == [('ADAMS', 'RESEARCH'), ('ALLEN', 'SALES'), ('BLAKE', 'SALES'), ('CLARK', 'ACCOUNTING'), ('ENAME', 'X0_1'), ('FORD', 'RESEARCH'), ('JAMES', 'SALES'), ('JONES', 'RESEARCH'), ('KING', 'ACCOUNTING'), ('MARTIN', 'SALES'), ('MILLER', 'null'), ('SCOTT', 'RESEARCH'), ('SMITH', 'RESEARCH'), ('TURNER', 'SALES'), ('WARD', 'SALES')], 'Neo4j query failed'

# Start testing of native_mode
class SQLTestCase(unittest.TestCase):
    def runTest(self):
        results = SQL on conn_native "select ename, dname from emp e join dept d on(e.deptno = d.deptno)"
        assert sorted(results) == [('ADAMS', 'RESEARCH'), ('ALLEN', 'SALES'), ('BLAKE', 'SALES'), ('CLARK', 'ACCOUNTING'), ('ENAME', 'DNAME'), ('FORD', 'RESEARCH'), ('JAMES', 'SALES'), ('JONES', 'RESEARCH'), ('KING', 'ACCOUNTING'), ('MARTIN', 'SALES'), ('SCOTT', 'RESEARCH'), ('SMITH', 'RESEARCH'), ('TURNER', 'SALES'), ('WARD', 'SALES')], 'SQL query failed'

# Start testing of Oracle RDF NoSQL
class OracleNoSQLTestCase_Neo4J_select(unittest.TestCase):
    def runTest(self):
        results = Neo4j on connOracleRDFNoSQL "MATCH(a:emp) RETURN a.ename"
        assert sorted(results) == [('ADAMS',), ('ALLEN',), ('BLAKE',), ('CLARK',), ('FORD',), ('JAMES',), ('JONES',), ('KING',), ('MARTIN',), ('MILLER',), ('SCOTT',), ('SMITH',), ('TURNER',), ('WARD',), ('ename',)], 'OracleNoSQLTestCase_Neo4J_select query failed'

class OracleNoSQLTestCase_Neo4J_join(unittest.TestCase):
    def runTest(self):
        results = Neo4j on connOracleRDFNoSQL "MATCH(a:emp)-[:dept]->(b:dept) RETURN b.dname, a.ename"
        assert sorted(results) == [('ADAMS', 'RESEARCH'), ('ALLEN', 'SALES'), ('BLAKE', 'SALES'), ('CLARK', 'ACCOUNTING'), ('FORD', 'RESEARCH'), ('JAMES', 'SALES'), ('JONES', 'RESEARCH'), ('KING', 'ACCOUNTING'), ('MARTIN', 'SALES'), ('MILLER',), ('SCOTT', 'RESEARCH'), ('SMITH', 'RESEARCH'), ('TURNER', 'SALES'), ('WARD', 'SALES'), ('ename', 'x0_1')], 'OracleNoSQLTestCase_Neo4J_join query failed'

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
        results = SIM on connOracleRDFNoSQL "FROM emp RETRIEVE dname OF dept, ename;"
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

class OOReLTestCase(unittest.TestCase):
    def runTest(self):
        persist on global_conn class TEST(object):
            A = 0
            B = 0
            def __init__(self):
                self.A = ""
                self.B = 0

        item = TEST()
        item.A = "samplestring"
        item.B = 540
        relInsert on global_conn item
        relCommit on global_conn

        results = SQL on global_conn "select * from TEST"

        # SQL on global_conn """ DELETE * FROM TEST"""
        # results = SQL on global_conn """ SELECT TEST.A, TEST.B FROM TEST """

        assert False, 'OOReL needs work'

if __name__ == "__main__":
    unittest.main()
