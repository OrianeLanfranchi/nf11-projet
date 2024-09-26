grammar Logo ; 

@header {		
  package logoparsing;
}

FLOAT : [0-9][0-9]*('.'[0-9]+)? ;
WS : [ \t\r\n]+ -> skip ;
COMMENT1 : '//' .*? [\r\n]+ -> skip;
COMMENT2 : '/*' .*? '*/' -> skip;
VARIABLE : [a-zA-Z]+;

programme :
 liste_instructions  
;

liste_instructions :   
 (instruction)+    
;

instruction :
   'av' expr # av
 | 'td' expr # td
 | 'tg' expr # tg
 | 'lc' # lc
 | 'bc' # bc
 | 're' expr # re
 | 'fpos' expr expr # fpos
 | 'fcc' VARIABLE # fcc
 | 'fcap' expr # fcap
 | 'repete' expr '[' liste_instructions ']' #repete
 | 'store' # store
 | 'move' # move
 | 'donne' '"' VARIABLE expr #donne
 | 'si' booleen '[' liste_instructions ']' #si
 | 'si' booleen '[' liste_instructions ']' '[' liste_instructions ']' #sisinon
 | 'tantque' booleen '[' liste_instructions ']' #tantque
; 

booleen :
	expr '<' expr #infstrict
|	expr '<=' expr #infequal
| 	expr '>' expr #supstrict
|	expr '>=' expr #supequal
|	expr '==' expr #equals
;


expr :
  expr '+' term  # addition 
 |  expr '-' term  # difference
 | term # terme
 | 'hasard' '(' expr ')' #hasard
;

term :
   term '*' fact  # multiplication
 |  term '/' fact  # fraction 
 | fact # facteur
 ;
 
 fact :
 	FLOAT         # float
 |  'loop' #loop
 | '(' expr ')' #parenthese
 | 'cos' '(' expr ')' # cos
 | 'sin' '(' expr ')' #sin
 |  VARIABLE # var
 ;
