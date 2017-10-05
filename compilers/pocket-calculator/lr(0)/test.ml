PolyML.use "calculator.ml";
PolyML.print_depth 10000;

val tree = parse o lex;
val compute = calculate o flatten o parse o lex;

(* ---------------------------------------------------------------
     INVALID INPUT
   --------------------------------------------------------------- *)

tree " ";  (* LexError "Invalid character at the start of a token" raised *)
tree "3."; (* LexError "Unexpected end of input while lexing the first character after a decimal point (state 4)." *)
tree "+";  (* LexError "Unexpected end of input while lexing the first character of a decimal (state 1)." *)

tree ""; (* ParseError "There is no action on token '$' from state 0" raised *)

(* ---------------------------------------------------------------
     VALID INPUT
   --------------------------------------------------------------- *)

tree "0";
tree "1+2";
tree "1+2+3";
tree "1-2-3";
tree "1+2-3";
tree "1*2+-3";
tree "cos3";
tree "coscos1!!";
tree "1+cos2-3*4!";

(* ---------------------------------------------------------------
     CALCULATIONS
   --------------------------------------------------------------- *)

compute "0";       (* 0.0 : zero represented correctly *)
compute "-0";      (* ~0.0 : distinguished negative 0 *)
compute "-2e-2";   (* ~0.02 : unary minus on mantissa and exponent *)
compute "1--2e-2"; (* 1.02 : unary and binary minus *)
compute "1++3e+2"; (* 301.0 : unary and binary plus *)
compute "coscos2"; (* 0.9146533259 : nested cosine application *)
compute "3!!";     (* 720.0 : nested factorial application *)

compute "2+7-7+2";   (* 4.0 : minus has a higher precedence than plus *)
compute "3*4-5+6*7"; (* 49.0 : multiply has a higher precedence than minus *)
compute "cos10*10";  (* ~8.390715291 : cos has a higher precedence than multiply *)
compute "cos0!";     (* 0.5403023059 : factorial has a higher precedence than cos *)

compute "1e309";       (* inf : upper limit of real representation *)
compute "1e309+1e309"; (* inf : valid arithmetic with infinities *)
compute "1e309-1e309"; (* nan : invalid arithmetic with infinities *)
compute "-1e309";      (* ~inf : distinguished negative infinity *)
compute "cos1e309";    (* nan : cosine of infinity *)

compute ""

