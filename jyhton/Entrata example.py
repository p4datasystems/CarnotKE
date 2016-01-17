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
        assignments = ''
        if isinstance(v, dict):
            processManyToMany(connection, v, k, debug, assignments)
        if isinstance(v, list):
            lists += sep + k
            sep = "; "
            count += 1
            for i, val in enumerate(v):
                if isinstance(val, dict):
                    if 'Identification' in val:
                        if '@attributes' in val.get('Identification'):
                            if val.get('Identification').get('@attributes').get('IDType'):
                                assignment += sep1 + val.get('Identification').get('@attributes').get('IDType').replace(" ", "_") + " := " + str(val.get('Identification').get('IDValue'))
                                sep1 = ", "
                                assignments += assignment
                                processManyToMany(connection, val, "?", debug, assignment)
                        else :
                            processManyToMany(connection, val, "?", debug, "")
    if debug:
        if count > 0:
            print "Dictionary has ", count, "list(s):", name, ":", lists, "assignments are:", assignments
    if count == 2 and ", " in assignments:
        insert = "SIM on " + str(connection) + ' """INSERT ' + name + " (" + assignments + ');"""'
        print insert
        eval(insert)
    return count

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

processManyToMany('connOracleRDFNoSQL', d, "?", False, "")

query = 'SQL on connOracleRDFNoSQL "select * from leaseapplication"'
print
print query
print eval(query)