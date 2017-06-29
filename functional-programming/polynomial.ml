(*This code concerns the representation of multivariable polynomials using ML. Once such
  representation is to represent each term in the polynomial as a pair of a coefficient
  and an ordered list of integers, representing the exponents of a series of variables:

    e.g. (3,[1,2,0,4]) represents 3*w*x^2*z^4

  Given this 'term' concept, a polynomial is simply a list of terms 
    *)

datatype term = Term of int * int list;

(*Number of variables * List of terms in lexicographic order*)
datatype poly = Poly of int * term list
              | Null;

exception MismatchedVariables;
exception EmptyArgument;

local
    (*Returns ~1 if a < b, 0 if a = b, 1 if a > b
     Throws error if the terms have different numbers of variables*)
    fun compPowers ([],[]) = 0
      | compPowers ([],_) = raise MismatchedVariables
      | compPowers (_,[]) = raise MismatchedVariables
      | compPowers (x::xs,y::ys) = 
             if (x > y) then 1
        else if (x < y) then ~1
                        else compPowers(xs,ys);

    (*Takes two lists of terms and combines them into one list*)
    fun zipterms ([],[])  = []
      | zipterms (t1s,[]) = t1s
      | zipterms ([],t2s) = t2s
      | zipterms (Term(a,pows1)::t1s,Term(b,pows2)::t2s) = case compPowers(pows1,pows2) of
          1 => Term(a,pows1)   :: zipterms (t1s,Term(b,pows2)::t2s)
        | ~1=> Term(b,pows2)   :: zipterms (Term(a,pows1)::t1s,t2s)
        | 0 => Term(a+b,pows1) :: zipterms (t1s,t2s)
        | _ => raise MismatchedVariables;

in
    (*Adds two polynomials together, assuming the terms of each are lexicographically ordered*)
    fun add (Null, Null) = Null
      | add (Poly(nvar1,terms1),Null)              = Poly(nvar1,terms1) (*Null cases*)
      | add (Null              ,Poly(nvar2,terms2)) = Poly(nvar2,terms2)
      | add (Poly(nvar1,terms1),Poly(nvar2,terms2)) =
        if (nvar1 <> nvar2) then raise MismatchedVariables
        else Poly(nvar1,zipterms(terms1,terms2));

    (*Checks whether two polynomials are equivalent*)
    fun polyEquals (Null, Null) = true
    | polyEquals (_,Null) = false
    | polyEquals (Null,_) = false
    | polyEquals (Poly(_,[]),Poly(_,[])) = true
    | polyEquals (Poly(nvar1,Term(c1,pows1)::t1s),Poly(nvar2, Term(c2,pows2)::t2s)) = 
        (nvar1 = nvar2)                                     (*The number of variables must be the same*)
        andalso (compPowers(pows1,pows2) = 0)               (*Powers of the terms must be equal*)
        andalso (c1 = c2)                                   (*Coefficient of the terms must be equal*)
        andalso polyEquals(Poly(nvar1,t1s),Poly(nvar2,t2s)) (*Further terms must be equal*)
   
    | polyEquals (_,_) = raise MismatchedVariables;

end;


val a = Poly(1,[Term(3,[2]),Term(6,[1]),Term(2,[0])]);
val b = a;
val c = Poly(1,[Term(2,[2]),Term(9,[1]),Term(3,[0])]);

polyEquals (a,b);
polyEquals (a,c);

add (a,b);