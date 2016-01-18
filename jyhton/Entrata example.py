import json

connOracleRDFNoSQL = connectTo 'OracleNoSQL' 'C##cs329e_UTEid' 'orcl_UTEid' 'rdf_mode' 'A0' nodebug
json_input = JAPI on connOracleRDFNoSQL " "
d = json.loads(json_input[0][0])

def processManyToMany(connection, d, name, debug, assignment):
    count = 0
    lists = ""
    sep = ""
    sep1 = ""
    for k, v in d.iteritems():
        assignment_list = ''
        if isinstance(v, dict):
            processManyToMany(connection, v, k, debug, assignment)
        if isinstance(v, list):
            lists += sep + k
            sep = "; "
            count += 1
            for i, val in enumerate(v):
                if isinstance(val, dict):
                    if 'Identification' in val and '@attributes' in val.get('Identification'):
                        attr = val.get('Identification').get('@attributes').get('IDType').replace(" ", "_")
                        value = val.get('Identification').get('IDValue')
                        try :
                            if isinstance(eval(str(value)), int): value = str(value)
                        except :
                            value = '"' + str(value) + '"'
                        if val.get('Identification').get('@attributes').get('IDType'):
                            assignment += sep1 + attr + " := " + value
                            sep1 = ", "
                            assignment_list += assignment
                        if '@attributes' in val:
                            for attr, value in val.get('@attributes').iteritems():
                                attr = attr.replace(" ", "_")
                                try :
                                    if isinstance(eval(str(value.replace(" ", "_"))), int): value = str(value)
                                except :
                                    value = '"' + str(value) + '"'
                                assignment += sep1 + attr + " := " + str(value)
                                assignment_list += assignment
                        processManyToMany(connection, val, "?", debug, assignment)
                    else :
                        processManyToMany(connection, val, "?", debug, "")
    if debug:
        if count > 0:
            print "Dictionary has ", count, "list(s):", name, ":", lists, "assignment_list is:", assignment_list
    if count == 2 and ", " in assignment_list:
        insert = "SIM on " + str(connection) + ' """INSERT ' + name + " (" + assignment_list + ');"""'
        print insert
        eval(insert)
    return count

processManyToMany('connOracleRDFNoSQL', d, "?", True, "")

query = 'SQL on connOracleRDFNoSQL "select * from leaseapplication"'
print
print query
print eval(query)

"""
def processList(d, name, insert):
    for k, v in d.iteritems():
        #print "!!!!!", type(v), k
        if isinstance(v, dict):
            if k == "@attributes" :
                pass
            else:
                processList(v, "", "")
        elif isinstance(v, list):
            list_name = k
            insert = "---INSERT " + k + " ( "
            for i, val in enumerate(v):
                if isinstance(val, dict):
                    processList(val, "", "")
                elif isinstance(val, list):
                    processList(val, "", "")
                else:
                    if isinstance(val, unicode):
                        insert += ' {0} := "{1}", '.format(1, 2)
                    else :
                        insert += ' {0} := {1}, '.format(i, val)
        else:
            if isinstance(v, unicode):
                insert += ' {0} := "{1}", '.format(k, v)
            else :
                insert += ' {0} := {1}, '.format(k, v)
    if insert != "":
        insert += " );"
        print
        print insert
"""