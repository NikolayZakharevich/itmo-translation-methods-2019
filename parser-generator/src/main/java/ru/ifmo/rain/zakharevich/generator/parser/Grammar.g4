grammar Grammar;

metaGrammar
  : GRAMMAR name=UPPERCASE_START_TOKEN SEMICOLON
    (HEADER header=BRACES_BLOCK)?
    (FIELDS fields=BRACES_BLOCK)?
    (productionRules+=productionRule | lexemRules+=lexemRule)+
    EOF
  ;

productionRule
  : name=LOWERCASE_START_TOKEN
    (args=BRACKETS_BLOCK)?
    (RETURNS ret=BRACKETS_BLOCK)?
    COLON productionOptions SEMICOLON
  ;

productionOptions
  : options+=productionOption (OR options+=productionOption)*
  ;

productionOption
  : (wrappers+=productionTokenWrapper)*
    (code = BRACES_BLOCK)?
  ;

productionTokenWrapper
  : (label=LOWERCASE_START_TOKEN ASSIGN)?
    token=productionToken
    (args=BRACKETS_BLOCK)?
  ;

productionToken
  : lexemRuleName=UPPERCASE_START_TOKEN
  | parserRuleName=LOWERCASE_START_TOKEN (args=BRACKETS_BLOCK)?
  | string=SINGLE_QUOTE_STRING
  | LPAREN alternative=productionOptions RPAREN
  ;


lexemRule
  : name=UPPERCASE_START_TOKEN COLON lexemOptions (skip = SKIP_ARROW?) SEMICOLON
  ;

lexemOptions
  : options+=lexemOption (OR options+=lexemOption)*
  ;

lexemOption
  : (wrappers+=lexemTokenWrapper)*
  ;

lexemTokenWrapper
  : token=lexemToken (repeat=QUANTIFIER)?
  | code=BRACES_BLOCK
  ;

lexemToken
  : lexemRuleName=UPPERCASE_START_TOKEN
  | string=SINGLE_QUOTE_STRING
  | charset=BRACKETS_BLOCK
  ;


BRACES_BLOCK: LBRACE (~['"{}] | SINGLE_QUOTE_STRING | DOUBLE_QUOTE_STRING | BRACES_BLOCK)* RBRACE {
  String text = getText();
  setText(text.substring(1, text.length() - 1));
};

BRACKETS_BLOCK: LBRACKET (~[\\\]] | [\\] . )* RBRACKET {
  String text = getText();
  setText(text.substring(1, text.length() - 1));
};

GRAMMAR               : 'grammar' ;
FIELDS                : 'fields'  ;
HEADER                : 'header'  ;
RETURNS               : 'returns' ;
SKIP_ARROW            : '-> skip' ;

UPPERCASE_START_TOKEN : UPPERCASE_LETTER CHAR*;
LOWERCASE_START_TOKEN : LOWERCASE_LETTER CHAR*;

LPAREN                : '('       ;
RPAREN                : ')'       ;
COLON                 : ':'       ;
SEMICOLON             : ';'       ;
OR                    : '|'       ;
LBRACKET              : '['       ;
RBRACKET              : ']'       ;
LBRACE                : '{'       ;
RBRACE                : '}'       ;
ASSIGN                : '='       ;

SINGLE_QUOTE_STRING   : '\'' (~[\\'] | [\\] . )+ '\'' { String text = getText();  setText(text.substring(1, text.length() - 1)); } ;
DOUBLE_QUOTE_STRING   : '"' (~[\\"] | [\\] . )* '"'   { String text = getText(); setText(text.substring(1, text.length() - 1)); } ;
QUANTIFIER            : [?*+] ;

BLOCK_COMMENT         : '/*' .*? '*/' -> skip ;
LINE_COMMENT          : '//' ~[\r\n]* -> skip ;
NEW_LINE              : ('\r\n' | '\r' | '\n') -> skip ;
WHITESPACE            : [ \t]+ -> skip ;


fragment CHAR             : DIGIT | LETTER | UNDERSCORE ;
fragment LETTER           : UPPERCASE_LETTER | LOWERCASE_LETTER ;
fragment DIGIT            : [0-9] ;
fragment LOWERCASE_LETTER : [a-z] ;
fragment UPPERCASE_LETTER : [A-Z] ;
fragment UNDERSCORE       : [_]   ;

