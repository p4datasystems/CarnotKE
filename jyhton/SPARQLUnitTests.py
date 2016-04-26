import unittest

connOracleRDFNoSQL = connectTo 'OracleNoSQL' 'C##cs329e_UTEid' 'orcl_UTEid' 'rdf_mode' 'A0' nodebug

results = SQL on connOracleRDFNoSQL "select ename from emp"

if "ALLEN" not in results:
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

    Neo4j on connOracleRDFNoSQL "MATCH (a:emp),(b:dept) WHERE a.deptno = 10 AND b.deptno = 10 CREATE (a)<-[:employees]-(b)"
    Neo4j on connOracleRDFNoSQL "MATCH (a:emp),(b:dept) WHERE a.deptno = 20 AND b.deptno = 20 CREATE (a)<-[:employees]-(b)"
    Neo4j on connOracleRDFNoSQL "MATCH (a:emp),(b:dept) WHERE a.deptno = 30 AND b.deptno = 30 CREATE (a)<-[:employees]-(b)"
    Neo4j on connOracleRDFNoSQL "MATCH (a:emp),(b:dept) WHERE a.deptno = 40 AND b.deptno = 40 CREATE (a)<-[:employees]-(b)"

    Neo4j on connOracleRDFNoSQL "MATCH (a:emp),(b:dept) WHERE a.deptno = 10 AND b.deptno = 10 CREATE (a)-[:dept]->(b)"
    Neo4j on connOracleRDFNoSQL "MATCH (a:emp),(b:dept) WHERE a.deptno = 20 AND b.deptno = 20 CREATE (a)-[:dept]->(b)"
    Neo4j on connOracleRDFNoSQL "MATCH (a:emp),(b:dept) WHERE a.deptno = 30 AND b.deptno = 30 CREATE (a)-[:dept]->(b)"
    Neo4j on connOracleRDFNoSQL "MATCH (a:emp),(b:dept) WHERE a.deptno = 40 AND b.deptno = 40 CREATE (a)-[:dept]->(b)"

results = SQL on connOracleRDFNoSQL "select deptno from dept"

if 20 not in results:

    SQL on connOracleRDFNoSQL "INSERT INTO DEPT (DEPTNO, DNAME, LOC) VALUES (20, 'RESEARCH',   'DALLAS');"


class OracleNoSQLTestCase_SQL_project (unittest.TestCase):

    def test_OracleNoSQLTestCase_SQL_1(self):
        results = SQL on connOracleRDFNoSQL "select sal, ename from (select * from emp) order by ename"
        self.assertEqual(sorted(results), [('ADAMS', 1100), ('ALLEN', 1600), ('BLAKE', 2850), ('CLARK', 2450), ('FORD', 3000), ('JAMES', 950), ('JONES', 2975), ('KING', 5000), ('MARTIN', 1250), ('MILLER', 1300), ('SCOTT', 3000), ('SMITH', 800), ('TURNER', 1500), ('WARD', 1250), ('ename', 'sal')])

    def test_OracleNoSQLTestCase_SQL_2(self):
        results = SQL on connOracleRDFNoSQL "select ename, sal from emp where empno=7876"
        self.assertEqual(sorted(results), [('ADAMS', 1100), ('ename', 'sal')])


    def test_OracleNoSQLTestCase_SQL_3(self):
        results = SQL on connOracleRDFNoSQL "select ename, sal from emp where 7876=(select empno from emp)"
        self.assertEqual(sorted(results), [('ADAMS', 1100), ('ename', 'sal')])

    def test_OracleNoSQLTestCase_SQL_4(self):
        results = SQL on connOracleRDFNoSQL "select ename, sal, deptno from emp e join (select deptno from dept d) f on (e.deptno = f.deptno)"
        self.assertEqual(sorted(results), [('ADAMS', 1100, 20), ('FORD', 3000, 20), ('JONES', 2975, 20), ('SCOTT', 3000, 20), ('SMITH', 800, 20), ('ename', 'sal', 'deptno')])

# ----
# main
# ----

if __name__ == "__main__" :
    suite = unittest.TestLoader().loadTestsFromTestCase(OracleNoSQLTestCase_SQL_project)
    unittest.TextTestRunner(verbosity=2).run(suite)
