(*Church numerals are an abstraction of natural numbers using lambda notation. It is possible to
  simply implement the hyperoperations from n=0 to n=3 using curried functions in ML, as 
  illustrated in the following code.

  Intuitively, the Church encoding of an natural number n is a function that maps any function to
  its n-fold composition, i.e:
  	0 f x = x
  	1 f x = f(x)
  	2 f x = f(f(x)) etc.*)

(*-----BASIS-----*)
fun zero     f x = x;
fun succ   n f x = f(n f x);       (*Apply successor function to base n+1 many times*)

(*-----PREDECESSOR------*)
fun pre n f x = (fn (a,b) => a) (n (fn (a,b) => (b,f b)) (x,x));

(*-----OPERATION DEFINITIONS-----*)
fun succ   n f x =   f(n f x); (*Apply successor function to base n many times*)
fun add  m n f x = m f(n f x); (*Take successor of n, m many times*)
fun mult m n f x = m (n f) x;  (*Take nth successor of base, m many times*)
fun pow  m n = n m;

fun sub m n f x = n pre m f x;

(*testing*)
fun integer l = l (fn x => x+1) 0; (*Cast arbitrary function to integer*)
fun one       f x = f x;
fun two       f x = f (f x);
fun three     f x = (succ two) f x; (*This works!*)
fun six       f x = (add (add two two) two) f x; (*2 + 2 + 2*)
fun sixfour   f x = (pow (mult two one) six) f x;

integer (pre (pre (pre sixfour)));
