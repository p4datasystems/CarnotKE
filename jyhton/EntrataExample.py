import json

connOracleRDFNoSQL = connectTo 'OracleNoSQL' 'C##cs329e_UTEid' 'orcl_UTEid' 'rdf_mode' 'A0' nodebug
#json_input = JAPI on connOracleRDFNoSQL " "
#d = json.loads(json_input[0][0])
with open('EntrataExampleJSON.json') as data_file:    
    d = json.load(data_file)

def processManyToMany(connection, top_dict, top_dict_name, debug, assignment): # E.g., top_dict_name is LeaseApplication
    count = 0
    lists = ""
    sep = ""
    sep1 = ""
    for top_dict_key, top_dict_value in top_dict.iteritems(): # E.g., top_dict_key: Tenant, top_dict_value: list
        assignment_list = ''
        if isinstance(top_dict_value, dict):
            processManyToMany(connection, top_dict_value, top_dict_key, debug, assignment)
        if isinstance(top_dict_value, list): # I.e., Tenant is a list
            lists += sep + top_dict_key
            sep = "; "
            count += 1
            for i, item_from_top_dict_value in enumerate(top_dict_value):
                if isinstance(item_from_top_dict_value, dict):
                    # The next line is the signature of a many-to-many relationship.
                    if 'Identification' in item_from_top_dict_value and '@attributes' in item_from_top_dict_value.get('Identification'):
                        # Find the many-to-many association table foreign key data.
                        attr = item_from_top_dict_value.get('Identification').get('@attributes').get('IDType').replace(" ", "_")
                        value = item_from_top_dict_value.get('Identification').get('IDValue')
                        try :
                            if isinstance(eval(str(value)), int): value = str(value) # If the value is a number leave it alone
                        except :
                            value = '"' + str(value) + '"' # If the value is a string, put double quotes around it.
                        top_dict_key_foreignKey = attr         # This is the primary key name for one of the tables in the many-to-many relationship.
                        top_dict_key_foreignKeyValue = value   # This is the primary key value for one of the tables in the many-to-many relationship.

                        # Get other attr/value pairs for the association table in the many-to-many relationship.
                        if item_from_top_dict_value.get('Identification').get('@attributes').get('IDType'):
                            assignment += sep1 + attr + " := " + value
                            sep1 = ", "
                            assignment_list += assignment
                        if '@attributes' in item_from_top_dict_value:
                            for attr, value in item_from_top_dict_value.get('@attributes').iteritems():
                                attr = attr.replace(" ", "_")
                                try :
                                    if isinstance(eval(str(value.replace(" ", "_"))), int): value = str(value) # If the value is a number leave it alone
                                except :
                                    value = '"' + str(value) + '"' # If the value is a string, put double quotes around it.
                                assignment += sep1 + attr + " := " + str(value)
                                assignment_list += assignment

                        # Get the data for the non-association tables in the many-to-many relationship
                        insertStmtOrig = insertStmt = "SIM on " + str(connection) + ' """INSERT ' + top_dict_key + " (" + top_dict_key_foreignKey + " := " + top_dict_key_foreignKeyValue
                        sep = ", "
                        for a1, v1 in item_from_top_dict_value.iteritems():
                            if isinstance(v1, dict): 
                                attribute = ""
                                for a2, v2 in v1.iteritems():
                                    if isinstance(v2, dict): 
                                        processOneToMany(connection, v2, a1, debug, insertStmtOrig) 
                                        attribute = ""
                                        break
                                    elif isinstance(v2, list): 
                                        processOneToMany(connection, v2, a1, debug, insertStmtOrig) 
                                        attribute = "" 
                                        break
                                    else: 
                                        try :
                                            if isinstance(eval(str(v2.replace(" ", "_"))), int): v2 = str(value) # If the value is a number leave it alone
                                        except :
                                            v2 = '"' + str(v2) + '"' # If the value is a string, put double quotes around it.
                                        attribute += sep + a2 + " := " + str(v2)
                                if attribute != "": 
                                    insertStmt += attribute
                                    sep = ", "
                        if isinstance(v1, list): 
                            processOneToMany(connection, v1, a1, debug, insertStmtOrig)
                        insertStmt += ');"""'
                        print insertStmt
                        eval(insertStmt) # Execute a SIM INSERT for the table in the many-to-many relationship.

                        #processManyToMany(connection, item_from_top_dict_value, "?", debug, assignment) This causes false many-to-many data.
                    else :
                        pass #processManyToMany(connection, item_from_top_dict_value, "?", debug, "") # Not needed.
    if debug:
        if count > 0:
            print "Dictionary has ", count, "list(s):", top_dict_name, ":", lists, "assignment_list is:", assignment_list
    if count == 2 and ", " in assignment_list:
        insert = "SIM on " + str(connection) + ' """INSERT ' + top_dict_name + " (" + assignment_list + ');"""'
        print insert
        eval(insert)
    return count

def processOneToMany(connection, top_dict, top_dict_name, debug, assignment):
    print "In processOneToMany, name, assignment are: ", top_dict_name, ", ",assignment 

processManyToMany('connOracleRDFNoSQL', d, "?", False, "")

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

query = 'SQL on connOracleRDFNoSQL "select firstname, lastname, residenttype from LeaseApplication l join Tenant t on(l.Customer_ID = t.Customer_ID)"'
print
print query
print eval(query)


query = 'SQL on connOracleRDFNoSQL "select firstname, lastname, residenttype from LeaseApplication l join Tenant t on(l.Customer_ID = t.Customer_ID) join LA_Lease x on(l.Lease_ID = x.Lease_ID)"'
print
print query
print eval(query)
