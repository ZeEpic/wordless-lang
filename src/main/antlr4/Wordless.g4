grammar Wordless;

program: line* EOF;

line: statement | ifStatement | loop | function | ('\n' | '\r');

statement: (expression | returnStatement | CONTINUE | BREAK | variable | functionCall) ('\n' | '\r');

loop: (ID)? '<' (expression)? '>' (expression)? block;
CONTINUE: '...';
BREAK: '!!!';

ifStatement: '?' expression block (elseStatement)?;
elseStatement: ':' (ifStatement | block);

variable: ID '=' expression;

function: ID '(' (ID type (',' ID type)*)? ')' (type)? block;
returnStatement: '->' expression;
functionCall: ID '(' (expression (',' expression)*)? ')';

expression
    : constant                                                                # constantExpression
    | functionCall                                                            # functionCallExpression
    | ID                                                                      # identifierExpression
    | '(' expression ')'                                                      # parenExpression
    | '!' expression                                                          # notExpression
    | '-' expression                                                          # negExpression
    | '[' (expression (',' expression)*)? ']'                                 # listExpression
    | '(' (expression (',' expression)*)? ')'                                 # arrayExpression
    | '{' (expression ':' expression (',' expression ':' expression)*)? '}'   # dictExpression
    | expression POW expression                                               # powExpression
    | expression MULTIPLY expression                                          # multiplyExpression
    | expression ADD expression                                               # addExpression
    | expression COMPARE expression                                           # booleanCompareExpression
    | expression BOOL_OPERATOR expression                                     # booleanOperatorExpression
    ;

POW: '**';
MULTIPLY: '*' | '/' | '%';
ADD: '+' | '-';
COMPARE: '==' | '!=' | '<' | '>' | '<=' | '>=';
BOOL_OPERATOR: '&&' | '||';

constant: INT | FLOAT | STRING | BOOL;
INT: [0-9]+;
FLOAT: [0-9]+ '.' [0-9]+;
STRING: ('"' (.*?) '"') | ('\'' (.*?) '\''); // 'hello'  or "hello"
BOOL: 'true' | 'false';

block: '{' line* '}';

type: 'int' | 'float' | 'string' | 'bool' | 'list[' ID ']' | 'dict[' ID ',' ID ']' | 'array[' ID ']' | 'function[' (ID (',' ID)*)? ']' | ID;

COMMENT: '#' .*? '\n' -> skip;
WHITESPACE: [ \t]+ -> skip;
ID: [a-zA-Z_][a-zA-Z0-9_]*;