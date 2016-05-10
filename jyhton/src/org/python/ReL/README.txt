@Authored by Noel Negusse
### HOW TO UPDATE WDB LOGIC ###

+ Metadata Objects
	- Metadata objects, such as ClassDef, can be updated directly in
	  WDB/database/wdb/metadata/

+ Parser Files
	- The local parser files (here in ReL) only consist of the parser
	  objects generated from QueryParser.jjt 
	- In order to make any updates to the parser, you must first go to the
	  independent WDB folder, CarnotKE/jyhton/Neo4j/WDB-new/
	- Then navigate to src/parser/javacc/QueryParser.jjt and make any of
	  the desired changes to the grammar (which uses javaCC)
	- Next head back to the root of WDB-new and run ./build.sh (this will
	  run the xml file and in turn generate all of the necessary files to be moved)
	- Next head to (from WDB-new root) src/parser/generated/wdb/parser/, where
	  the generated parser java files will be located
	- These files can now be copied over to
	  CarnotKE/jython/src/org/python/ReL/WDB/parser/generated/wdb/parser/
	- Lastly, you must change the import/package paths of all generated parser files to:
		package org.python.ReL.WDB.parser.generated.wdb.parser;
		import org.python.ReL.WDB.database.wdb.metadata.*;
		import java.io.*;
		import java.util.ArrayList;

+ ProcessQuery method
	- The analagous method to processQuery in WDB's WDB.java is
	  processSIMNative inside ProcessLanguages.java
	- Any changes you wish to make to the WDB's "processQuery" will be done within the comments
	/********************** BEGIN WDB CODE DUMP **********************/
	and
	/********************** END WDB CODE DUMP **********************/
	which is found in ProcessLanguages.java	


### Example Test Cases for Schemaless Inserts in Carnot ###

# (Schemaless Inserts) Animal
SIM on connOracleNoSQL 'INSERT animal ( Species := "Tiger", type:= "Mammalia", MeatEater := True);'
SIM on connOracleNoSQL 'INSERT animal ( Species := "Jiraffe", type := "Mammalia", MeatEater := False);'
SIM on connOracleNoSQL 'INSERT animal.pet WHERE Species = "Tiger" ( Name := "Greg", Age := 10, Address := "3843 Maplewood Dr.");'
SIM on connOracleNoSQL 'INSERT animal.pet FROM animal WHERE Species = "Jiraffe" ( Name := "Spot", Age := 25, Address := "2893 San Gabriel Rd.");'

print "Testing schemaless inserts:"
# 7. Show all instances of the SIM_person class
SIM on connOracleNoSQL 'from animal retrieve *;'
# 8. Show all instances of the SIM_emp class
SIM on connOracleNoSQL 'from pet retrieve *;'
