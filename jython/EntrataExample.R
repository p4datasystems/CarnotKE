require("jsonlite")
require("RCurl")
df <- data.frame(fromJSON(getURL(URLencode(gsub("\n", " ", 'localhost:5011/rest/native/?query=
"select firstname, lastname, residenttype, customer_id, ldrank, ldsopetype, lease_id, organizationname 
from LeaseApplication l 
join 
Tenant t on(l.Customer_ID = t.Customer_ID)"
')),httpheader=c(DB='OracleNoSQL', USER='C##cs329e_UTEid', PASS='orcl_UTEid', MODE='rdf_mode', MODEL='A0', returnDimensions = 'False', returnFor = 'JSON'), verbose = TRUE), ))
df

save(df, file="/Users/pcannata/Downloads/R2Tableau.RData")

df <- data.frame(fromJSON(getURL(URLencode('localhost:5011/rest/native/?query="select firstname, lastname, residenttype, customer_id, ldrank, ldsopetype, lease_id, organizationname from LeaseApplication l join Tenant t on(l.Customer_ID = t.Customer_ID)"'),httpheader=c(DB='OracleNoSQL', USER='C##cs329e_UTEid', PASS='orcl_UTEid', MODE='rdf_mode', MODEL='A0', returnDimensions = 'False', returnFor = 'JSON'), verbose = TRUE), ))
