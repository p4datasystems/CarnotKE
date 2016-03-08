/**
 * Define a grammar called Cypher
 */
grammar Cypher;

clauses 
	: match (where)? returnClause #read
	| create 					  #writenode
	| edgeMatch where edgeCreate  #writeedge
	;

match : 'MATCH' relationshipChain;

where : 'WHERE' boolExpression;

returnClause : 'RETURN' returnList;

create : 'CREATE' (node | relationshipChain);

edgeMatch : 'MATCH' node ',' node; 

edgeCreate 
	: 'CREATE' forwardNode node #forwardCreate
	| 'CREATE' node backwardNode #backCreate
	;

relationshipChain 
	:   forwardNode* node 	#forwardChain
	|	node backwardNode*	#backwardChain
	;

forwardNode : node forwardEdge;	

backwardNode : backwardEdge node;

node 
	: '(' ID ')'		#identifierNode
	| '(' ID ':' ID ')' #labelNode
	| '(' ID? label* attrList? ')' #genericNode 
	;
	
label
	: ':' ID
	;
	
attrList
	: '{' ( (assign ',')* assign )? '}'
	;

assign
	: ID ':' value
	;
forwardEdge 
	: '-'+ '[' ID? ':' ID']' '-'+ '>' 	#fLabelEdge
	| '-'+ ('[' ID ']')? '-'+ '>'		#fAllEdge
	;
	
backwardEdge
	: '<' '-'+ '[' ID? ':'ID']' '-'+	#bLabelEdge
	| '<' '-'+ ('[' ID? ']')? '-'+		#bAllEdge
	;

expression : property comparison value;

boolExpression : expression (('AND' | 'OR') expression)*;

property : ID ('.' ID)?;

comparison 
	: '=' 
	| '<>' 
	| '<' 
	| '>' 
	| '<=' 
	| '>='
	;

value 
	:	STRING
	| 	NUMBER
	| 	'true' | 'TRUE'
	| 	'false' | 'FALSE'
	|	'null'
	;
		
returnList:  (property ',')* property;

ID : [a-zA-Z_] [a-zA-Z0-9\-_]*;

STRING : '\'' .*? '\'';

NUMBER 
	:	'-'? INT '.' INT EXP?
	|	'-'? INT EXP
	|	'-'? INT
	;

fragment INT : 	'0' | [1-9][0-9]*;
fragment EXP :	[Ee] [+\-]? INT ;

WS : [ \t\r\n]+ -> skip ;
