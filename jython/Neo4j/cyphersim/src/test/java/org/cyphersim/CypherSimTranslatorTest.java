package org.cyphersim;


import org.cyphersim.CypherSimTranslator;

import junit.framework.TestCase;

public class CypherSimTranslatorTest extends TestCase {
	protected CypherSimTranslator t;
	
	protected void setUp() {
		t = new CypherSimTranslator();
	}
	
	protected void tearDown() {
		t = null;
	}
	
	/**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public CypherSimTranslatorTest( String testName ){
        super( testName );
    }

    
    public void testReturnAllNodesOfLabel() {
        assertEquals("MATCH (e:emp) RETURN e \ntranslates to\n" + 
        		"FROM EMP RETRIEVE *;",
        		t.translate("MATCH (e:emp) RETURN e"),
        		"FROM EMP RETRIEVE *;");
    }
    
    public void testReturnPropertyOfAllNodesOfLabel() {
    	assertEquals("MATCH (e:emp) RETURN e.ename \ntranslates to\n FROM EMP RETRIEVE ENAME;",
        		t.translate("MATCH (e:emp) RETURN e.ename"),
        		"FROM EMP RETRIEVE ENAME;");
    }
    
    public void testOneRelationshipChain() {
    	assertEquals("MATCH (e:emp)-[:DEPTNO]->(d:dept) RETURN e.ename, d.dname \ntranslates to\n FROM EMP RETRIEVE ENAME, DNAME OF DEPTNO;",
        		t.translate("MATCH (e:emp)-[:DEPTNO]->(d:dept) RETURN e.ename, d.dname"),
        		"FROM EMP RETRIEVE ENAME, DNAME OF DEPTNO;");
    }
    
    public void testTwoRelationshipChains() {
    	assertEquals("MATCH (e:emp)-[:DEPTNO]->(d:dept)-[:ORGNUM]->(o:org) RETURN e.empno, e.ename, e.sal, d.dname, o.oname \ntranslates to\n " +
    					"FROM EMP RETRIEVE EMPNO, ENAME, SAL, DNAME OF DEPTNO, ONAME OF ORGNUM OF DEPTNO;",
        		t.translate("MATCH (e:emp)-[:DEPTNO]->(d:dept)-[:ORGNUM]->(o:org) RETURN e.empno, e.ename, e.sal, d.dname, o.oname"),
        		"FROM EMP RETRIEVE EMPNO, ENAME, SAL, DNAME OF DEPTNO, ONAME OF ORGNUM OF DEPTNO;");
    }
    
    public void testTwoRelationshipChainsWithWhereClause() {
    	assertEquals("MATCH (e:emp)-[:DEPTNO]->(d:dept)-[:ORGNUM]->(o:org) WHERE e.empno = 111 RETURN e.empno, e.ename, e.sal, d.dname, o.oname \ntranslates to\n " +
    					"FROM EMP RETRIEVE EMPNO, ENAME, SAL, DNAME OF DEPTNO, ONAME OF ORGNUM AT DEPTNO WHERE EMPNO = 111;",
        		t.translate("MATCH (e:emp)-[:DEPTNO]->(d:dept)-[:ORGNUM]->(o:org) WHERE e.empno = 111 RETURN e.empno, e.ename, e.sal, d.dname, o.oname"),
        		"FROM EMP RETRIEVE EMPNO, ENAME, SAL, DNAME OF DEPTNO, ONAME OF ORGNUM OF DEPTNO WHERE EMPNO = 111;");
    }
    
    public void testOneBackwardsChain() {
    	assertEquals("MATCH (e:emp)<-[:EMPNO]-(d:dept) RETURN e.ename, d.dname \ntranslates to\n FROM DEPT RETRIEVE ENAME OF EMPNO, DNAME;",
        		t.translate("MATCH (e:emp)<-[:EMPNO]-(d:dept) RETURN e.ename, d.dname"),
        		"FROM DEPT RETRIEVE ENAME OF EMPNO, DNAME;");
    }
    
    public void testTwoBackwardsChains() {
    	assertEquals("MATCH (e:emp)<-[:EMPNO]-(d:dept)<-[:DEPTNO]-(o:org) RETURN e.eno, e.ename, e.sal, d.dname, o.oname \ntranslates to\n " +
    					"FROM ORG RETRIEVE ENO OF EMPNO OF DEPTNO, ENAME OF EMPNO OF DEPTNO, SAL OF EMPNO OF DEPTNO, DNAME OF DEPTNO, ONAME;",
        		t.translate("MATCH (e:emp)<-[:EMPNO]-(d:dept)<-[:DEPTNO]-(o:org) RETURN e.eno, e.ename, e.sal, d.dname, o.oname "),
        		"FROM ORG RETRIEVE ENO OF EMPNO OF DEPTNO, ENAME OF EMPNO OF DEPTNO, SAL OF EMPNO OF DEPTNO, DNAME OF DEPTNO, ONAME;");
    }
    
    public void testTwoBackwardsChainsWithWhereClause() {
    	assertEquals("MATCH (e:emp)<-[:EMPNO]-(d:dept)<-[:DEPTNO]-(o:org) WHERE e.eno = 111 RETURN e.eno, e.ename, e.sal, d.dname, o.oname \ntranslates to\n " +
				"FROM ORG RETRIEVE ENO OF EMPNO OF DEPTNO, ENAME OF EMPNO OF DEPTNO, SAL OF EMPNO OF DEPTNO, DNAME OF DEPTNO, ONAME WHERE ENO OF EMPNO OF DEPTNO = 111;",
		t.translate("MATCH (e:emp)<-[:EMPNO]-(d:dept)<-[:DEPTNO]-(o:org) WHERE e.eno = 111 RETURN e.eno, e.ename, e.sal, d.dname, o.oname "),
		"FROM ORG RETRIEVE ENO OF EMPNO OF DEPTNO, ENAME OF EMPNO OF DEPTNO, SAL OF EMPNO OF DEPTNO, DNAME OF DEPTNO, ONAME WHERE ENO OF EMPNO OF DEPTNO = 111;");
    }
    
    public void testPersonAllRows() {
        assertEquals("MATCH (n:Person) RETURN n \ntranslates to\n" + 
        		"FROM PERSON RETRIEVE *;",
        		t.translate("MATCH (n:Person) RETURN n"),
        		"FROM PERSON RETRIEVE *;");
    }
    
    public void testEmployeeSalary() {
        assertEquals("MATCH (n:Employee) RETURN n.salary\ntranslates to\n" + 
        		"FROM EMPLOYEE RETRIEVE SALARY;",
        		t.translate("MATCH (n:Employee) RETURN n.salary"),
        		"FROM EMPLOYEE RETRIEVE SALARY;");
    }
    
    public void testManagerPersonId() {
        assertEquals("MATCH (n:Manager) RETURN n.person_id\ntranslates to\n" + 
        		"FROM MANAGER RETRIEVE PERSON_ID;",
        		t.translate("MATCH (n:Manager) RETURN n.person_id"),
        		"FROM MANAGER RETRIEVE PERSON_ID;");
    }
    
    public void testPresidentAllRows() {
        assertEquals("MATCH (n:President) RETURN n\ntranslates to\n" + 
        		"FROM PRESIDENT RETRIEVE *;",
        		t.translate("MATCH (n:President) RETURN n"),
        		"FROM PRESIDENT RETRIEVE *;");
    }
    
    public void testPersonSevenName() {
        assertEquals("MATCH (n:Person) WHERE n.person_id = 7 RETURN n.first_name, n.last_name\ntranslates to\n" + 
        		"FROM PERSON RETRIEVE FIRST_NAME, LAST_NAME WHERE PERSON_ID = 7;",
        		t.translate("MATCH (n:Person) WHERE n.person_id = 7 RETURN n.first_name, n.last_name"),
        		"FROM PERSON RETRIEVE FIRST_NAME, LAST_NAME WHERE PERSON_ID = 7;");
    }
    
    public void testBillsSpouseAllRows() {
        assertEquals("MATCH (n:Person)-[:SPOUSE]->(m:Person) WHERE n.first_name = 'Bill' RETURN m" + 
        		"\ntranslates to\n" + 
        		"FROM PERSON RETRIEVE * OF SPOUSE WHERE FIRST_NAME = \"Bill\";",
        		t.translate("MATCH (n:Person)-[:SPOUSE]->(m:Person) WHERE n.first_name = 'Bill' RETURN m"),
        		"FROM PERSON RETRIEVE * OF SPOUSE WHERE FIRST_NAME = \"Bill\";");
    }
    
    public void testTwoRelChainTestSchemaOneProp() {
        assertEquals("MATCH (n:Project_Employee)-[:CURRENT_PROJECTS]->(cp:Current_Projects)-[:DEPT_ASSIGNED]->(d:Department) WHERE n.person_id = 3 RETURN d.dept_name" + 
        		"\ntranslates to\n" + 
        		"FROM PROJECT_EMPLOYEE RETRIEVE DEPT_NAME OF DEPT_ASSIGNED OF CURRENT_PROJECTS WHERE PERSON_ID = 3;",
        		t.translate("MATCH (n:Project_Employee)-[:CURRENT_PROJECTS]->(cp:Current_Projects)-[:DEPT_ASSIGNED]->(d:Department) WHERE n.person_id = 3 RETURN d.dept_name"),
        		"FROM PROJECT_EMPLOYEE RETRIEVE DEPT_NAME OF DEPT_ASSIGNED OF CURRENT_PROJECTS WHERE PERSON_ID = 3;");
    }
    
    public void testTwoRelChainTestSchemaTwoProp() {
        assertEquals("MATCH (n:Project_Employee)-[:CURRENT_PROJECTS]->(cp:Current_Projects)-[:DEPT_ASSIGNED]->(d:Department) WHERE n.person_id = 3 RETURN d.dept_name, d.dept_no" + 
        		"FROM PROJECT_EMPLOYEE RETRIEVE DEPT_NAME OF DEPT_ASSIGNED OF CURRENT_PROJECTS, DEPT_NO OF DEPT_ASSIGNED OF CURRENT_PROJECTS WHERE PERSON_ID = 3;",
        		t.translate("MATCH (n:Project_Employee)-[:CURRENT_PROJECTS]->(cp:Current_Projects)-[:DEPT_ASSIGNED]->(d:Department) WHERE n.person_id = 3 RETURN d.dept_name, d.dept_no"),
        		"FROM PROJECT_EMPLOYEE RETRIEVE DEPT_NAME OF DEPT_ASSIGNED OF CURRENT_PROJECTS, DEPT_NO OF DEPT_ASSIGNED OF CURRENT_PROJECTS WHERE PERSON_ID = 3;");
    }
    
    public void testOfInSIMWhereClause() {
        assertEquals("MATCH (d:Department)-[:PROJECT_AT]->(p:Project) WHERE p.project_title = 'Mission Impossible' RETURN d.dept_name\ntranslates to\n" + 
        		"FROM DEPARTMENT RETRIEVE DEPT_NAME WHERE PROJECT_TITLE OF PROJECT_AT = \"Mission Impossible\";",
        		t.translate("MATCH (d:Department)-[:PROJECT_AT]->(p:Project) WHERE p.project_title = 'Mission Impossible' RETURN d.dept_name"),
        		"FROM DEPARTMENT RETRIEVE DEPT_NAME WHERE PROJECT_TITLE OF PROJECT_AT = \"Mission Impossible\";");
    }
    /* Test to demonstrate a functionality to be implemented, must be commented out to build the jar
    public void testNoNodeLabels() {
        assertEquals("MATCH (n)-[:SPOUSE]->(m) WHERE n.first_name = 'Bill' RETURN m\ntranslates to\n" + 
        		"FROM PERSON RETRIEVE * OF SPOUSE WHERE FIRST_NAME = \"Bill\";",
        		t.translate("MATCH (n)-[:SPOUSE]->(m) WHERE n.first_name = 'Bill' RETURN m"),
        		"FROM PERSON RETRIEVE * OF SPOUSE WHERE FIRST_NAME = \"Bill\";");
    }
    */
    
    public void testCreatePersonOne() {
    	assertEquals("CREATE (:Person:Employee:Project_Employee:Manager:Interim_Manager { person_id : 1 , first_name :  'Bill' , last_name : 'Dawer' ,  home_address: '432 Hill Rd', zipcode : 78705, home_phone : 7891903 ,  us_citizen :  TRUE, employee_id: 101, salary: 70200, salary_exception : TRUE, bonus : 10000 })"
    			+ "should return"
    			+ "INSERT Interim_Manager ( person_id := 1 , first_name := \"Bill\" , last_name := \"Dawer\" , home_address := \"432 Hill Rd\" , zipcode := 78705 , home_phone := 7891903 , us_citizen := TRUE , employee_id := 101 , salary_exception := TRUE , bonus := 10000 );",
    			"INSERT Interim_Manager ( person_id := 1 , first_name := \"Bill\" , last_name := \"Dawer\" , home_address := \"432 Hill Rd\" , zipcode := 78705 , home_phone := 7891903 , us_citizen := TRUE , employee_id := 101 , salary := 70200 , salary_exception := TRUE , bonus := 10000 );",
    			t.translate("CREATE (:Person:Employee:Project_Employee:Manager:Interim_Manager { person_id : 1 , first_name :  'Bill' , last_name : 'Dawer' ,  home_address: '432 Hill Rd', zipcode : 78705, home_phone : 7891903 ,  us_citizen :  TRUE, employee_id: 101, salary: 70200, salary_exception : TRUE, bonus : 10000 })")
    			);
    }
    
    public void testCreatePersonTwo() {
    	assertEquals("CREATE (:Person:Employee:Project_Employee { person_id : 2 , first_name :  'Diane' , last_name : 'Wall' ,  home_address: '32 Cannon Dr', zipcode : 78705, home_phone : 7891903 ,  us_citizen :  TRUE, employee_id: 102,salary: 80210, salary_exception : FALSE})"
    			+ "should return"
    			+ "INSERT Project_Employee ( person_id := 2 , first_name := \"Diane\" , last_name := \"Wall\" , home_address := \"32 Cannon Dr\" , zipcode := 78705 , home_phone := 7891903 , us_citizen := TRUE , employee_id := 102 , salary := 80210 , salary_exception := FALSE );",
    			"INSERT Project_Employee ( person_id := 2 , first_name := \"Diane\" , last_name := \"Wall\" , home_address := \"32 Cannon Dr\" , zipcode := 78705 , home_phone := 7891903 , us_citizen := TRUE , employee_id := 102 , salary := 80210 , salary_exception := FALSE );",
    			t.translate("CREATE (:Person:Employee:Project_Employee { person_id : 2 , first_name :  'Diane' , last_name : 'Wall' ,  home_address: '32 Cannon Dr', zipcode : 78705, home_phone : 7891903 ,  us_citizen :  TRUE, employee_id: 102,salary: 80210, salary_exception : FALSE})")
    			);
    }
    
    public void testCreatePersonThree() {
    	assertEquals("CREATE (:Person:Employee:Project_Employee { person_id : 3 , first_name :  'Jennifer' , last_name : 'Brown' ,  home_address: '35 Palm Lane', zipcode : 73014, home_phone : 6541658 ,  us_citizen :  TRUE, employee_id: 103,salary: 80210 })"
    			+ "should return"
    			+ "INSERT Project_Employee ( person_id := 3 , first_name := \"Jennifer\" , last_name := \"Brown\" , home_address := \"35 Palm Lane\" , zipcode := 73014 , home_phone := 6541658 , us_citizen := TRUE , employee_id := 103 , salary := 80210 );",
    			"INSERT Project_Employee ( person_id := 3 , first_name := \"Jennifer\" , last_name := \"Brown\" , home_address := \"35 Palm Lane\" , zipcode := 73014 , home_phone := 6541658 , us_citizen := TRUE , employee_id := 103 , salary := 80210 );",
    			t.translate("CREATE (:Person:Employee:Project_Employee { person_id : 3 , first_name :  'Jennifer' , last_name : 'Brown' ,  home_address: '35 Palm Lane', zipcode : 73014, home_phone : 6541658 ,  us_citizen :  TRUE, employee_id: 103,salary: 80210 })")
    			);
    }
    
    public void testCreatePersonFour() {
    	assertEquals("CREATE (:Person:Previous_Employee { person_id : 4 , first_name :  'Alice' , last_name : 'Dawer' ,  home_address: '432 Hill Rd', zipcode : 78021, home_phone : 7891903 ,  us_citizen :  FALSE, salary: 50500 })"
    			+ "should return"
    			+ "INSERT Previous_Employee ( person_id := 4 , first_name := \"Alice\" , last_name := \"Dawer\" , home_address := \"432 Hill Rd\" , zipcode := 78021 , home_phone := 7891903 , us_citizen := FALSE , salary := 50500 );",
    			"INSERT Previous_Employee ( person_id := 4 , first_name := \"Alice\" , last_name := \"Dawer\" , home_address := \"432 Hill Rd\" , zipcode := 78021 , home_phone := 7891903 , us_citizen := FALSE , salary := 50500 );",
    			t.translate("CREATE (:Person:Previous_Employee { person_id : 4 , first_name :  'Alice' , last_name : 'Dawer' ,  home_address: '432 Hill Rd', zipcode : 78021, home_phone : 7891903 ,  us_citizen :  FALSE, salary: 50500 })")
    			);
    }
    
    public void testSpouseEVA() {
    	assertEquals("",
    			"MODIFY LIMIT = ALL Person (SPOUSE := Person WITH (first_name = \"Bill\" AND last_name = \"Dawer\")) WHERE first_name = \"Alice\" AND last_name = \"Dawer\";",
    			t.translate("MATCH (a:Person),(b:Person) WHERE a.first_name = 'Alice' AND a.last_name = 'Dawer' AND b.first_name = 'Bill' AND b.last_name = 'Dawer' CREATE (a)-[:SPOUSE]->(b)")
    			);
    }
    
    public void testSpouseEVABackwards() {
    	assertEquals("",
    			"MODIFY LIMIT = ALL Person (SPOUSE := Person WITH (first_name = \"Alice\" AND last_name = \"Dawer\")) WHERE first_name = \"Bill\" AND last_name = \"Dawer\";",
    			t.translate("MATCH (a:Person),(b:Person) WHERE a.first_name = 'Alice' AND a.last_name = 'Dawer' AND b.first_name = 'Bill' AND b.last_name = 'Dawer' CREATE (a)<-[:SPOUSE]-(b)")
    			);
    }
    
    public void testBillEmployeeManagerEVA() {
    	assertEquals("",
    			"MODIFY LIMIT = ALL Employee (employee_manager := Manager WITH (first_name = \"Bill\" AND last_name = \"Dawer\")) ".toUpperCase()
                                    + "WHERE employee_id = 102 OR employee_id = 106;".toUpperCase(),
    			t.translate("MATCH (e1:Employee),(m1:Manager) WHERE e1.employee_id = 102 OR e1.employee_id = 106 AND m1.first_name = 'Bill' AND m1.last_name = 'Dawer' CREATE (e1)-[:EMPLOYEE_MANAGER]->(m1)").toUpperCase()
    			);
    }
}
