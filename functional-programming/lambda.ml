(* ----- CHURCH BOOLEANS -----
  Church booleans are the Church encoding of the Boolean values true and false. In short, Boolean
  logic may be considered as a choice, such as in flow control. The Church encoding of true and 
  false are functions of two parameters: true chooses the first parameter, false chooses the 
  second parameter.

  This definition allows preficates to act directly as if-clauses:
  	<predicate> bool <then-clause> <else-clause> *)

fun TRUE  x y = x;
fun FALSE x y = y;

(* ----- LOGICAL OPERATORS -----
  The above definitions of true and false naturally extend to a set of logic operators, which
  act on one or two Church boolean values and return the appropriate Church boolean. *)

fun IF   b1    x y = b1 x y;
fun OR   b1 b2 x y = b1 x (b2 x y);
fun XOR  b1 b2 x y = b1 (b2 y x) (b2 x y);
fun AND  b1 b2 x y = b1 (b2 x y) y;
fun NAND b1 b2 x y = b1 (b2 y x) x;
fun NOT  b1    x y = b1 y x;

(* ----- CHURCH NUMERALS -----
  Church numerals are an abstraction of natural numbers using lambda notation. Intuitively, the 
  Church encoding of an natural number n is a function that maps any function to its n-fold 
  composition, i.e:

  	0 f x = x
  	1 f x = f(x)
  	2 f x = f(f(x)) etc.  *)

fun zero     f x = x;         (*Do not apply successor function*)
fun succ   n f x = f(n f x);  (*Apply successor function to base n+1 many times*)

(* ----- ARITHMETIC OPERATORS -----
  Arithmetic operations on numbers may be encoded as functions which act on Church numerals. The 
  hyperoperations from n=0 to n=3 can be simply implemented using curried functions in ML:  *)

fun succ   n f x =   f(n f x); (*Apply successor function to base n many times*)
fun add  m n f x = m f(n f x); (*Take successor of n, m many times*)
fun mult m n f x = m (n f) x;  (*Take nth successor of base, m many times*)
fun pow  m n     = n m;

(* ----- PREDECESSOR FUNCTION -----
  The predecessor funtion is the inverse of the successor function. It is *)
fun pre n f x = (fn (a,b) => a) (n (fn (a,b) => (b,f b)) (x,x));
fun sub m n f x = n pre m f x;

(*testing*)
fun integer l = l (fn x => x+1) 0; (*Cast arbitrary function to integer*)
fun one       f x = f x;
fun two       f x = f (f x);
fun three     f x = (succ two) f x; (*This works!*)
fun six       f x = (add (add two two) two) f x; (*2 + 2 + 2*)
fun sixfour   f x = (pow (mult two one) six) f x;

integer (pre (pre (pre sixfour)));
