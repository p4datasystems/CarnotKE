import unittest

"""
conn0 = connectTo 'jdbc:oracle:thin:@sayonara.microlab.cs.utexas.edu:1521:orcl' 'C##cs329e_UTEid' 'orcl_UTEid' 'native_mode'
SQL on conn0 "{call SEM_APIS.DROP_RDF_MODEL('A0_C##CS329E_UTEID')}"
SQL on conn0 "drop table A0_C##CS329E_UTEID_DATA;"
SQL on conn0 "DROP SEQUENCE A0_C##CS329E_UTEID_SQNC;"
SQL on conn0 "DROP SEQUENCE A0_C##CS329E_UTEID_GUID_SQNC;"

conn = connectTo 'jdbc:oracle:thin:@sayonara.microlab.cs.utexas.edu:1521:orcl' 'C##cs329e_UTEid' 'orcl_UTEid' 'rdf_mode' 'A0' 

Neo4j on conn "CREATE (:emp { EMPNO : 7369, ENAME : 'SMITH', JOB : 'CLERK', MGR : 7902, HIREDATE : '17-DEC-80', SAL : 800, COMM : 0, DEPTNO : 20})"
Neo4j on conn "CREATE (:emp { EMPNO : 7499, ENAME : 'ALLEN', JOB : 'SALESMAN', MGR : 7698, HIREDATE : '20-FEB-81', SAL : 1600, COMM : 300, DEPTNO : 30})"
Neo4j on conn "CREATE (:emp { EMPNO : 7521, ENAME : 'WARD', JOB : 'SALESMAN', MGR : 7698, HIREDATE : '22-FEB-81', SAL : 1250, COMM : 500, DEPTNO : 30})"
Neo4j on conn "CREATE (:emp { EMPNO : 7566, ENAME : 'JONES', JOB : 'MANAGER', MGR : 7839, HIREDATE : '02-APR-81', SAL : 2975, COMM : 0, DEPTNO : 20})"
Neo4j on conn "CREATE (:emp { EMPNO : 7654, ENAME : 'MARTIN', JOB : 'SALESMAN', MGR : 7698, HIREDATE : '28-SEP-81', SAL : 1250, COMM : 1400, DEPTNO : 30})"
Neo4j on conn "CREATE (:emp { EMPNO : 7698, ENAME : 'BLAKE', JOB : 'MANAGER', MGR : 7839, HIREDATE : '01-MAY-81', SAL : 2850, COMM : 0, DEPTNO : 30})"
Neo4j on conn "CREATE (:emp { EMPNO : 7782, ENAME : 'CLARK', JOB : 'MANAGER', MGR : 7839, HIREDATE : '09-JUN-81', SAL : 2450, COMM : 0, DEPTNO : 10})"
Neo4j on conn "CREATE (:emp { EMPNO : 7788, ENAME : 'SCOTT', JOB : 'ANALYST', MGR : 7566, HIREDATE : '09-DEC-82', SAL : 3000, COMM : 0, DEPTNO : 20})"
Neo4j on conn "CREATE (:emp { EMPNO : 7839, ENAME : 'KING', JOB : 'PRESIDENT', MGR : 0, HIREDATE : '17-NOV-81', SAL : 5000, COMM : 0, DEPTNO : 10})"
Neo4j on conn "CREATE (:emp { EMPNO : 7844, ENAME : 'TURNER', JOB : 'SALESMAN', MGR : 7698, HIREDATE : '08-SEP-81', SAL : 1500, COMM : 0, DEPTNO : 30})"
Neo4j on conn "CREATE (:emp { EMPNO : 7876, ENAME : 'ADAMS', JOB : 'CLERK', MGR : 7788, HIREDATE : '12-JAN-83', SAL : 1100, COMM : 0, DEPTNO : 20})"
Neo4j on conn "CREATE (:emp { EMPNO : 7900, ENAME : 'JAMES', JOB : 'CLERK', MGR : 7698, HIREDATE : '03-DEC-81', SAL : 950, COMM : 0, DEPTNO : 30})"
Neo4j on conn "CREATE (:emp { EMPNO : 7902, ENAME : 'FORD', JOB : 'ANALYST', MGR : 7566, HIREDATE : '03-DEC-81', SAL : 3000, COMM : 0, DEPTNO : 20})"
Neo4j on conn "CREATE (:emp { EMPNO : 7934, ENAME : 'MILLER', JOB : 'CLERK', MGR : 7782, HIREDATE : '23-JAN-82', SAL : 1300, COMM : 0, DEPTNO : 50})"

Neo4j on conn "CREATE (:dept { DEPTNO : 10, DNAME : 'ACCOUNTING', LOC : 'NEW YORK' })"
Neo4j on conn "CREATE (:dept { DEPTNO : 20, DNAME : 'RESEARCH', LOC : 'DALLAS' })"
Neo4j on conn "CREATE (:dept { DEPTNO : 30, DNAME : 'SALES', LOC : 'CHICAGO' })"
Neo4j on conn "CREATE (:dept { DEPTNO : 40, DNAME : 'OPERATIONS', LOC : 'BOSTON' })"

Neo4j on conn "MATCH (a:emp),(b:dept) WHERE a.deptno = 10 AND b.deptno = 10 CREATE (a)<-[:employees]-(b)"
Neo4j on conn "MATCH (a:emp),(b:dept) WHERE a.deptno = 20 AND b.deptno = 20 CREATE (a)<-[:employees]-(b)"
Neo4j on conn "MATCH (a:emp),(b:dept) WHERE a.deptno = 30 AND b.deptno = 30 CREATE (a)<-[:employees]-(b)"
Neo4j on conn "MATCH (a:emp),(b:dept) WHERE a.deptno = 40 AND b.deptno = 40 CREATE (a)<-[:employees]-(b)"

Neo4j on conn "MATCH(a:emp)<-[:employees]-(b:dept) WHERE b.deptno = 20 RETURN b, a.ename, a.job, a.mgr, a.deptno, a.hiredate"

Neo4j on conn "MATCH (a:emp),(b:dept) WHERE a.deptno = 10 AND b.deptno = 10 CREATE (a)-[:dept]->(b)"
Neo4j on conn "MATCH (a:emp),(b:dept) WHERE a.deptno = 20 AND b.deptno = 20 CREATE (a)-[:dept]->(b)"
Neo4j on conn "MATCH (a:emp),(b:dept) WHERE a.deptno = 30 AND b.deptno = 30 CREATE (a)-[:dept]->(b)"
Neo4j on conn "MATCH (a:emp),(b:dept) WHERE a.deptno = 40 AND b.deptno = 40 CREATE (a)-[:dept]->(b)"
"""

conn1 = connectTo 'jdbc:oracle:thin:@sayonara.microlab.cs.utexas.edu:1521:orcl' 'C##cs329e_UTEid' 'orcl_UTEid' 'rdf_mode' 'A0' nodebug

class SIMTestCase(unittest.TestCase):
    def runTest(self):
        results = SIM on conn1 "From emp Retrieve ename"
        assert sorted(results) == [('ADAMS',), ('ALLEN',), ('BLAKE',), ('CLARK',), ('ENAME',), ('FORD',), ('JAMES',), ('JONES',), ('KING',), ('MARTIN',), ('MILLER',), ('SCOTT',), ('SMITH',), ('TURNER',), ('WARD',)], 'SIM query failed'

class Neo4jTestCase(unittest.TestCase):
    def runTest(self):
        results = Neo4j on conn1 "MATCH(a:emp)-[:dept]->(b:dept) RETURN b.dname, a.ename"
        assert sorted(results) == [('ADAMS', 'RESEARCH'), ('ALLEN', 'SALES'), ('BLAKE', 'SALES'), ('CLARK', 'ACCOUNTING'), ('ENAME', 'X0_1'), ('FORD', 'RESEARCH'), ('JAMES', 'SALES'), ('JONES', 'RESEARCH'), ('KING', 'ACCOUNTING'), ('MARTIN', 'SALES'), ('MILLER', 'null'), ('SCOTT', 'RESEARCH'), ('SMITH', 'RESEARCH'), ('TURNER', 'SALES'), ('WARD', 'SALES')], 'Neo4j query failed'

conn2 = connectTo 'OracleNoSQL' 'C##cs329e_UTEid' 'orcl_UTEid' 'rdf_mode' 'A0'

class OracleNoSQLTestCase(unittest.TestCase):
    def runTest(self):
        results = Neo4j on conn2 "MATCH(a:emp)-[:dept]->(b:dept) RETURN b.dname, a.ename"
        assert results == (('NoSQL',),), 'Oracle NoSQL query failed'

if __name__ == "__main__":
    unittest.main()
