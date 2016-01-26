import json

connOracleRDFNoSQL = connectTo 'OracleNoSQL' 'C##cs329e_UTEid' 'orcl_UTEid' 'rdf_mode' 'A0' 
connOracleRDFNoSQL = connectTo 'OracleNoSQL' 'C##cs329e_UTEid' 'orcl_UTEid' 'rdf_mode' 'A0' nodebug
#json_input = JAPI on connOracleRDFNoSQL " "
#d = json.loads(json_input[0][0])
with open('EntrataExampleJSON.json') as data_file: 
    long = False
#with open('entrata_leases_01222016.json') as data_file:
    #long = True
    d = json.load(data_file)

def processManyToMany(connection, firstListName, secondForeignKeyName, topDict, topDictName, debug): # E.g., topDictName is LeaseApplication
    for topDictKey, topDictValue in topDict.iteritems(): # E.g., topDictKey: Tenant, topDictValue: list
        if isinstance(topDictValue, dict):
            processManyToMany(connection, firstListName, secondForeignKeyName, topDictValue, topDictKey, debug)
        if isinstance(topDictValue, list): # I.e., Tenant is a list
            for i, itemFromTopDictValue in enumerate(topDictValue): # Look at each item in the list.
                if isinstance(itemFromTopDictValue, dict):
                    if topDictKey.upper() == firstListName.upper(): # If the lists name == firstListName argument, e.g., 'Tenant'
                        if isinstance(itemFromTopDictValue, dict):  # Only operate on items that are dicts.
                            assignmentList = ''
                            # Find the first many-to-many association table foreign key data.
                            attr = itemFromTopDictValue.get('Identification').get('@attributes').get('IDType').replace(" ", "_")
                            value = itemFromTopDictValue.get('Identification').get('IDValue')
                            value = quoteIfString(value)
                            assignmentList += attr + " := " + value
                            firstTableForeignKey = attr
                            firstTableForeignKeyValue = value
                            for k, v in itemFromTopDictValue.iteritems():
                                # print k
                                if k == 'Identification':
                                    # Get other attributes that are in "Identification".
                                    for attr, value in v.iteritems():
                                        if attr != 'IDValue' and attr != '@attributes':
                                            attr = attr.replace(" ", "_")
                                            value = quoteIfString(value)
                                            assignmentList += ", " + attr + " := " + value
                                        # Get other attributes that are in "@attributes".
                                        elif attr == '@attributes':
                                            for a1, v1 in value.iteritems():
                                                if a1 != 'IDType':
                                                    attr = a1.replace(" ", "_")
                                                    value = quoteIfString(v1)
                                                    assignmentList += ", " + attr + " := " + value   

                                # Find the second many-to-many association table foreign key data.
                                elif k == secondForeignKeyName and isinstance(v.get('Identification'), list):
                                    attr = v.get('Identification')[0].get('@attributes').get('IDType').replace(" ", "_")
                                    value = v.get('Identification')[0].get('IDValue')
                                    value = quoteIfString(value)
                                    assignmentList += ", " + attr + " := " + value
                                # Find non-foreign key data on the association table.
                                elif k == '@attributes':
                                    for attr, value in v.iteritems():
                                        attr = attr.replace(" ", "_")
                                        value = quoteIfString(value)
                                        assignmentList += ", " + attr + " := " + value
                                else :
                                    # Get the data for the non-association table in the many-to-many relationship, e.g., data for the Tenant table
                                    # and process one-to-many relationships if appropriate.
                                    insertStmtOrig = insertStmt = "SIM on " + str(connection) + ' """INSERT ' + topDictKey + " (" + firstTableForeignKey + " := " + firstTableForeignKeyValue
                                    if isinstance(v, dict):
                                        # print 'Processing data for', k
                                        sep = ", "
                                        attribute = ""
                                        for a2, v2 in v.iteritems(): 
                                            attribute = ""
                                            if isinstance(v2, dict): 
                                                processOneToMany(connection, v, k, firstTableForeignKey, firstTableForeignKeyValue, debug) 
                                                attribute = ""
                                                break
                                            elif isinstance(v2, list): 
                                                processOneToMany(connection, v, k, firstTableForeignKey, firstTableForeignKeyValue, debug) 
                                                attribute = "" 
                                                break
                                            else: 
                                                v2 = quoteIfString(v2)
                                                attribute += sep + a2 + " := " + str(v2)
                                            if attribute != "": 
                                                insertStmt += attribute
                                        if attribute != "": 
                                            insertStmt += ');"""'
                                            if debug:
                                                print insertStmt
                                            # Insert non-association table data.
                                            eval(insertStmt) # Execute a SIM INSERT for the table in the many-to-many relationship.
                                    elif isinstance(v, list):
                                        processOneToMany(connection, v, k, firstTableForeignKey, firstTableForeignKeyValue, debug) 

                            # Insert association table data.
                            insert = "SIM on " + str(connection) + ' """INSERT ' + topDictName + " (" + assignmentList + ');"""'
                            if debug:
                                print insert
                            eval(insert)
                    else: # If the lists name != firstListName argument, e.g., 'LA_Lease'
                        if isinstance(itemFromTopDictValue, dict):  # Only operate on items that are dicts.
                            assignmentList = ''
                            attr = itemFromTopDictValue.get('Identification').get('@attributes').get('IDType').replace(" ", "_")
                            value = itemFromTopDictValue.get('Identification').get('IDValue')
                            value = quoteIfString(value)
                            assignmentList += attr + " := " + value
                            secondTablePrimaryKey = attr
                            secondTablePrimaryKeyValue = value
                            for k, v in itemFromTopDictValue.iteritems():
                                if k == 'Identification':
                                    # Get other attributes that are in "Identification".
                                    for attr, value in v.iteritems():
                                        if attr != 'IDValue' and attr != '@attributes':
                                            attr = attr.replace(" ", "_")
                                            value = quoteIfString(value)
                                            assignmentList += ", " + attr + " := " + value
                                        # Get other attributes that are in "@attributes".
                                        elif attr == '@attributes':
                                            for a1, v1 in value.iteritems():
                                                if a1 != 'IDType':
                                                    attr = a1.replace(" ", "_")
                                                    value = quoteIfString(v1)
                                                    assignmentList += ", " + attr + " := " + value 
                                else :
                                    # Get the data for the non-association table in the many-to-many relationship, e.g., data for the La_Lease table.
                                    if isinstance(v, dict):
                                        # print 'Processing data for', k
                                        attribute = ""
                                        for a2, v2 in v.iteritems():
                                            if isinstance(v2, dict): 
                                                processOneToMany(connection, v, k, secondTablePrimaryKey, secondTablePrimaryKeyValue, debug) 
                                                #processOneToMany(connection, v2, a1, debug, insertStmtOrig)
                                                attribute = ""
                                                break
                                            elif isinstance(v2, list): 
                                                processOneToMany(connection, v, k, secondTablePrimaryKey, secondTablePrimaryKeyValue, debug)
                                                attribute = ""
                                                break
                                            else: 
                                                v2 = quoteIfString(v2)
                                                attribute += ", " + a2 + " := " + v2
                                        if attribute != '':
                                            assignmentList += attribute
                                    elif isinstance(v, list):
                                        processOneToMany(connection, v, k, secondTablePrimaryKey, secondTablePrimaryKeyValue, debug)
                            # Insert association table data.
                            insert = "SIM on " + str(connection) + ' """INSERT ' + topDictKey + " (" + assignmentList + ');"""'
                            if debug:
                                print insert
                            eval(insert)

def quoteIfString(value):
    if isinstance(value, float) or isinstance(value, int):
        return str(value) # If the value is a number leave it alone.
    else:
        value = '"' + str(value) + '"'
        return value      # If the value is a string, put double quotes around it.

def processOneToMany(connection, dictOrList, dictOrListName, primeryKey, primeryKeyValue, debug):
    # process a simple list e.g., Phone or Status
    if isinstance(dictOrList, list):
        insert = "SIM on " + str(connection) + ' """INSERT ' + dictOrListName + " (" + primeryKey + " := " + primeryKeyValue
        process = True
        for i, listItem in enumerate(dictOrList):
            # Test for simple list.
            if isinstance(listItem, dict) and len(listItem) == 2 and '@attributes' in listItem and isinstance(listItem.get('@attributes'), dict) and len(listItem.get('@attributes')) == 1:
                for a2, v2 in listItem.iteritems():
                    if a2 == '@attributes': pass
                    elif a2 != '@attributes' and isinstance(v2, dict) or isinstance(v2, dict): process = False
                    else: 
                        insert += ", " + listItem.get('@attributes').values()[0] + " := " + quoteIfString(v2)
            elif isinstance(listItem, dict) and len(listItem) == 1:
                insert += ", " + listItem.keys()[0] + " := " + quoteIfString(listItem.values()[0])
            else: 
                process = False
                break
        if process:
            insert += ');"""'
            if debug:
                print insert
            eval(insert)
            return

    print "Did not process ", dictOrListName

processManyToMany('connOracleRDFNoSQL', 'Tenant', 'LeaseID', d, "", False)

#import time
#time.sleep(5)

if not long:
    query = 'SPARQL on connOracleRDFNoSQL "select ?g ?x ?y ?z where { GRAPH c:SCHEMA { ?x ?y ?z } }"'
    print
    print query
    results =  eval(query)
    for r in results:
        print r

    query = 'SQL on connOracleRDFNoSQL "select * from LeaseApplication"'
    print
    print query
    print eval(query)

    query = 'SQL on connOracleRDFNoSQL "select * from Tenant"'
    print
    print query
    print eval(query)

    query = 'SQL on connOracleRDFNoSQL "select * from LA_Lease"'
    print
    print query
    print eval(query)

    query = 'SQL on connOracleRDFNoSQL "select * from Phone"'
    print
    print query
    results =  eval(query)
    for r in results:
        print r

    query = 'SQL on connOracleRDFNoSQL "select * from Status"'
    print
    print query
    results =  eval(query)
    for r in results:
        print r

    query = 'SQL on connOracleRDFNoSQL "select firstname, lastname, residenttype from LeaseApplication l join Tenant t on(l.Customer_ID = t.Customer_ID)"'
    print
    print query
    print eval(query)

    query = 'SQL on connOracleRDFNoSQL "select firstname, lastname, residenttype from LeaseApplication l join Tenant t on(l.Customer_ID = t.Customer_ID) join LA_Lease x on(l.Lease_ID = x.Lease_ID)"'
    print
    print query
    print eval(query)

