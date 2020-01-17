grammar CalculatorWithPow;

@header {
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Collections;
}

s returns [List<String> results]
  : resList = equationList[new HashMap<String, Integer>(), new ArrayList<String>()] { Collections.reverse($resList.ret); $results = $resList.ret; }
  ;

equationList[Map<String, Integer> variables, List<String> results] returns [List<String> ret]
  : eq = equation[variables] SEMICOLON equationList[variables, results]*   { results.add($eq.result); $ret = $results; }
  ;

equation[Map<String, Integer> variables] returns [String result]
  : VAR EQ expr[variables]                                        { variables.put($VAR.text, $expr.val); $result = $VAR.text + " = " + $expr.val; }
  ;

expr[Map<String, Integer> variables] returns [int val]
  : term[variables] exprP[variables, $term.val]                   { $val = $exprP.val; }
  ;

exprP[Map<String, Integer> variables, int i] returns [int val]
  :                                                               { $val = $i; }
  | PLUS term[variables] e = exprP[variables, i + $term.val]      { $val = $e.val; }
  | MINUS term[variables] e = exprP[variables, i - $term.val]     { $val = $e.val; }
  ;

term[Map<String, Integer> variables] returns [int val]
  : fact[variables] termP[variables, $fact.val]                   { $val = $termP.val; }
  ;

termP[Map<String, Integer> variables, int i] returns [int val]
  :                                                               { $val = $i; }
  | TIMES fact[variables] e = termP[variables, i * $fact.val]     { $val = $e.val; }
  | DIV fact[variables] e = termP[variables, i / $fact.val]       { $val = $e.val; }
  ;

fact[Map<String, Integer> variables] returns [int val]
  : pow[variables] POW fact[variables]               { $val = (int)Math.pow($pow.val, $fact.val); }
  | pow[variables]                                                { $val = $pow.val; }
  ;

pow[Map<String, Integer> variables] returns [int val]
  : MINUS atom[variables]                                         { $val = -$atom.val; }
  | atom[variables]                                               { $val = $atom.val; }
  ;

atom[Map<String, Integer> variables] returns[int val]
  : LPAREN expr[variables] RPAREN                                 { $val = $expr.val; }
  | NUMBER                                                        { $val = Integer.parseInt($NUMBER.text); }
  | VAR                                                           { $val = variables.get($VAR.text); }
  ;

LPAREN      : '(' ;
RPAREN      : ')' ;
PLUS        : '+' ;
MINUS       : '-' ;
TIMES       : '*' ;
DIV         : '/' ;
EQ          : '=' ;
SEMICOLON   : ';' ;
POW         : '^' ;

NUMBER      : [0]|[1-9]+[0-9]* ;
VAR         : [a-z] ;
WHITESPACE  : [ \t\r\n]+ -> skip ;
