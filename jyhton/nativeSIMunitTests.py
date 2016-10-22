import unittest

connOracleNoSQL = connectTo 'OracleNoSQL' 'WDB' 'localhost:5010' 'native_mode' 'A0'

print "Connections are opened, start loading Databases"

# sim_person
SIM on connOracleNoSQL 'CLASS sim_person ( PERSON_ID:INTEGER, REQUIRED ; NAME :STRING ; SSNUM :INTEGER ; GENDER :STRING ; BIRTH_DATE :STRING ; ADDRESS :STRING ; CITY :STRING ; STATE :STRING ; ZIP :INTEGER ; );'

# sim_emp--->sim_person
SIM on connOracleNoSQL 'SUBCLASS sim_emp OF sim_person ( EMP_ID:INTEGER, REQUIRED ; HIRE_DATE :STRING ; SALARY :INTEGER ; STATUS :STRING ; );'

# sim_project_emp--->sim_emp--->sim_person
SIM on connOracleNoSQL 'SUBCLASS sim_project_emp OF sim_emp ( TITLE :STRING ; RATING :STRING ; projects :sim_project, MV(DISTINCT), INVERSE IS employees ; department :sim_dept, INVERSE IS employees ; );'

# sim_manager--->sim_emp--->sim_person
SIM on connOracleNoSQL 'SUBCLASS sim_manager OF sim_emp ( TITLE :STRING ; BONUS :INTEGER ; department :sim_dept, INVERSE IS manager ; );'

# sim_dept
# RELATIONSHIPS: sim_dept 1-1 sim_manager,  sim_dept 1-M sim_project_emp,   sim_dept 1-M sim_project
SIM on connOracleNoSQL 'CLASS sim_dept ( DEPT_ID:INTEGER, REQUIRED ; NAME :STRING ; LOCATION :STRING ; employees :sim_project_emp, MV(DISTINCT), INVERSE IS department ; projects :sim_project, MV(DISTINCT), INVERSE IS department ; manager :sim_manager, INVERSE IS department ; );'

# sim_project
SIM on connOracleNoSQL 'CLASS sim_project ( PROJECT_ID:INTEGER, REQUIRED ; NAME :STRING ; employees :sim_project_emp, MV(DISTINCT), INVERSE IS projects ; department :sim_dept, INVERSE IS projects ; );'

# (Schemaless Inserts) Animal
SIM on connOracleNoSQL 'INSERT animal ( Species := "Tiger", type:= "Mammalia", MeatEater := True);'
SIM on connOracleNoSQL 'INSERT animal ( Species := "Jiraffe", type := "Mammalia", MeatEater := False);'
SIM on connOracleNoSQL 'INSERT animal.pet WHERE Species = "Tiger" ( Name := "Greg", Age := 10, Address := "3843 Maplewood Dr.");'

# 6 instances of sim_project_emp
SIM on connOracleNoSQL 'INSERT sim_project_emp (PERSON_ID := 13 , NAME := "AARON" , SSNUM := 100 , GENDER := "MALE" , BIRTH_DATE := "01/01/87" , ADDRESS := "123 NOTHING LN" , CITY := "NEW YORK" , STATE := "NEW YORK" , ZIP := 888 , EMP_ID := 100100 , HIRE_DATE := "01/01/07" , SALARY := 87000 , STATUS := "EMPLOYED" , TITLE := "MARKETER"  , RATING := "OKAY"); '
SIM on connOracleNoSQL 'INSERT sim_project_emp (PERSON_ID := 2 , NAME := "ILYA" , SSNUM := 200 , GENDER := "MALE" , BIRTH_DATE := "01/02/87" , ADDRESS := "123 NOTHING LN" , CITY := "NEW YORK" , STATE := "NEW YORK" , ZIP := 888 , EMP_ID := 200200 , HIRE_DATE := "01/02/07" , SALARY := 67000 , STATUS := "EMPLOYED" , TITLE := "MARKETER"  , RATING := "SUCKY"); '
SIM on connOracleNoSQL 'INSERT sim_project_emp (PERSON_ID := 3 , NAME := "TY" , SSNUM := 300 , GENDER := "FEMALE" , BIRTH_DATE := "01/03/87" , ADDRESS := "123 NOTHING LN" , CITY := "AUSTIN" , STATE := "TEXAS" , ZIP := 888 , EMP_ID := 300300 , HIRE_DATE := "01/03/07" , SALARY := 92000 , STATUS := "EMPLOYED" , TITLE := "ENGINEER"  , RATING := "AWESOME"); '
SIM on connOracleNoSQL 'INSERT sim_project_emp (PERSON_ID := 4 , NAME := "PETER" , SSNUM := 400 , GENDER := "FEMALE" , BIRTH_DATE := "01/04/87" , ADDRESS := "123 NOTHING LN" , CITY := "AUSTIN" , STATE := "TEXAS" , ZIP := 888 , EMP_ID := 400400 , HIRE_DATE := "01/04/07" , SALARY := 112000 , STATUS := "EMPLOYED" , TITLE := "ENGINEER"  , RATING := "AVERAGE"); '
SIM on connOracleNoSQL 'INSERT sim_project_emp (PERSON_ID := 5 , NAME := "DERP" , SSNUM := 500 , GENDER := "MALE" , BIRTH_DATE := "01/05/87" , ADDRESS := "123 NOTHING LN" , CITY := "AUSTIN" , STATE := "TEXAS" , ZIP := 888 , EMP_ID := 500500 , HIRE_DATE := "01/05/07" , SALARY := 85000 , STATUS := "EMPLOYED" , TITLE := "ENGINEER"  , RATING := "EXCEPTIONAL"); '
SIM on connOracleNoSQL 'INSERT sim_project_emp (PERSON_ID := 6 , NAME := "BOB" , SSNUM := 600 , GENDER := "MALE" , BIRTH_DATE := "01/06/87" , ADDRESS := "123 NOTHING LN" , CITY := "AUSTIN" , STATE := "TEXAS" , ZIP := 888 , EMP_ID := 600600 , HIRE_DATE := "01/06/07" , SALARY := 187000 , STATUS := "EMPLOYED" , TITLE := "ENGINEER"  , RATING := "SAVANT"); '

# 2 instances of sim_manager
SIM on connOracleNoSQL 'INSERT sim_manager (PERSON_ID := 7, NAME := "MICHAEL", SSNUM := 700, GENDER := "MALE", BIRTH_DATE := "01/07/87", ADDRESS := "123 NOTHING LN", CITY := "AUSTIN", STATE := "TEXAS", ZIP := 888, EMP_ID := 700700, HIRE_DATE := "01/07/07", SALARY := 147000, STATUS := "EMPLOYED", TITLE := "SUPERVISOR INTERN IN TRAINING" , BONUS := 8423); '
SIM on connOracleNoSQL 'INSERT sim_manager (PERSON_ID := 8, NAME := "JOHNSON", SSNUM := 800, GENDER := "FEMALE", BIRTH_DATE := "01/08/87", ADDRESS := "123 NOTHING LN", CITY := "AUSTIN", STATE := "TEXAS", ZIP := 888, EMP_ID := 800800, HIRE_DATE := "01/08/07", SALARY := 187000, STATUS := "EMPLOYED", TITLE := "ASSOCIATE ASSISTANT SUPERVISOR" , BONUS := 5600); '

# 2 instances of sim_dept, each with a different manager
SIM on connOracleNoSQL 'INSERT sim_dept (DEPT_ID := 69 , NAME := "ENGINEERING", LOCATION := "AUSTIN"); '
SIM on connOracleNoSQL 'INSERT sim_dept (DEPT_ID := 10 , NAME := "MARKETING", LOCATION := "NEW YORK"); '
SIM on connOracleNoSQL 'MODIFY LIMIT = ALL sim_manager (department := sim_dept WITH (DEPT_ID = 69)) WHERE EMP_ID = 800800;'
SIM on connOracleNoSQL 'MODIFY LIMIT = ALL sim_manager (department := sim_dept WITH (DEPT_ID = 10)) WHERE EMP_ID = 700700;'

# 3 instances of sim_project, 2 in one department and 1 in the other department
SIM on connOracleNoSQL 'INSERT sim_project (PROJECT_ID := 1234567, NAME := "SUPER SECRET RESEARCH"); '
SIM on connOracleNoSQL 'INSERT sim_project (PROJECT_ID := 420420, NAME := "DANK PROJECT"); '
SIM on connOracleNoSQL 'INSERT sim_project (PROJECT_ID := 7654321, NAME := "BORING MARKETING CRAP"); '
SIM on connOracleNoSQL 'MODIFY LIMIT = ALL sim_project (department := sim_dept WITH (DEPT_ID = 69)) WHERE PROJECT_ID = 1234567 OR PROJECT_ID = 420420;'
SIM on connOracleNoSQL 'MODIFY LIMIT = ALL sim_project (department := sim_dept WITH (DEPT_ID = 10)) WHERE PROJECT_ID = 7654321;'

# Assign 2 project employees uniquely to each project
SIM on connOracleNoSQL 'MODIFY LIMIT = ALL sim_project_emp (projects := sim_project WITH (PROJECT_ID = 1234567)) WHERE EMP_ID = 300300 OR EMP_ID = 500500;'
SIM on connOracleNoSQL 'MODIFY LIMIT = ALL sim_project_emp (projects := sim_project WITH (PROJECT_ID = 420420)) WHERE EMP_ID = 400400 OR EMP_ID = 600600;'
SIM on connOracleNoSQL 'MODIFY LIMIT = ALL sim_project_emp (projects := sim_project WITH (PROJECT_ID = 7654321)) WHERE EMP_ID = 100100 OR EMP_ID = 200200;'

# Assign employees to their departments, starting with engineering
SIM on connOracleNoSQL 'MODIFY LIMIT = ALL sim_project_emp (department := sim_dept WITH (DEPT_ID = 69)) WHERE PERSON_ID = 3 OR PERSON_ID = 4 OR PERSON_ID = 5 OR PERSON_ID = 6;'
SIM on connOracleNoSQL 'MODIFY LIMIT = ALL sim_project_emp (department := sim_dept WITH (DEPT_ID = 10)) WHERE PERSON_ID = 1 OR PERSON_ID = 2;'


print "Beginning to query the native SIM database;"
# 1. Show all instances of the SIM_person class
# print(SIM on connOracleNoSQL 'from sim_person retrieve *;')
print()
# 2. Show all instances of the SIM_emp class
print(SIM on connOracleNoSQL 'from sim_project_emp retrieve *;')
print()
# 3. Show all instances of the SIM_project_emp class along with their respective department name and project name(s).
print(SIM on connOracleNoSQL 'from sim_project_emp retrieve *, name OF department, name OF projects;')
print()
# 4. Show all instances of the SIM_manager class along with their respective department names
print(SIM on connOracleNoSQL 'from sim_manager retrieve *, name OF department;')
print()
# 5. Show all instances of the SIM_project class along with their respective project employee and department name(s).
print(SIM on connOracleNoSQL 'from sim_project retrieve *, name OF employees, name OF department;')
print()
# 6. Show all instances of the SIM_dept class along with their respective project employee, project, and manager name(s).
print(SIM on connOracleNoSQL 'from sim_dept retrieve *, name OF employees, name OF projects, name OF manager;')
print()



# SIM on connOracleNoSQL "CLEAR DATABASE"
# SIM on connOracleNoSQL "STOP DATABASE"
# SIM on connOracleNoSQL "STOP DATABASE"





if __name__ == "__main__":
    unittest.main()
