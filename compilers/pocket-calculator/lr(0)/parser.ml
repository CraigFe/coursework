PolyML.use "lexer.ml";

datatype nonterminal = Expression | Sum | Difference | Product | Cosine | Factorial;
datatype symbol = N of nonterminal | T of tokenName;
datatype production = Prod of nonterminal * symbol list;
type grammar = production list;

datatype action = Shift of int
                | Reduce of int
                | Accept
                | Error of int * tokenName;

datatype ('a, 'b) tree = Br of 'a * ('a, 'b) tree list
                 | Lf of 'b;

fun member v (x::xs) = (v=x) orelse member v xs
  | member v [] = false;

fun parse s = let

  exception ParseError of string;
  exception InternalError of string;

  val g = [
    Prod(Expression, [N(Sum)]),
    Prod(Sum       , [N(Sum), T(Plus), N(Difference)]),
    Prod(Sum       , [N(Difference)]),
    Prod(Difference, [N(Difference), T(Minus), N(Product)]),
    Prod(Difference, [N(Product)]),
    Prod(Product   , [N(Cosine), T(Multiply), N(Product)]),
    Prod(Product   , [N(Cosine)]),
    Prod(Cosine    , [T(Cos), N(Cosine)]),
    Prod(Cosine    , [N(Factorial)]),
    Prod(Factorial , [N(Factorial), T(Pling)]),
    Prod(Factorial , [T(Float)])
  ];

  fun nonterminalToString Expression = "Expression"
    | nonterminalToString Sum = "Sum"
    | nonterminalToString Difference = "Difference"
    | nonterminalToString Product = "Product"
    | nonterminalToString Cosine = "Cosine"
    | nonterminalToString Factorial = "Factorial"

  fun tokenNameToString End = "$"
    | tokenNameToString Plus = "+"
    | tokenNameToString Minus = "-"
    | tokenNameToString Multiply = "*"
    | tokenNameToString Pling = "!"
    | tokenNameToString Cos = "cos"
    | tokenNameToString Float = "<float>";

  fun symbolToString (T(t)) = tokenNameToString t
    | symbolToString (N(n)) = nonterminalToString n;

  fun symbolsToString (s::ss) = (symbolToString s) ^ (symbolsToString ss)
    | symbolsToString [] = "";

  fun prodToString (Prod(n,ss)) = "{" ^ (nonterminalToString n) ^ " -> " ^ (symbolsToString ss) ^ "}";
  fun prodHead (Prod(nonterminal, _)) = nonterminal;

  fun actionError (state, tokenName) = raise ParseError("There is no action on token '" ^ (tokenNameToString tokenName) ^ "' from state " ^ (Int.toString state));
  fun gotoError (state, nonterminal) = raise ParseError("There is no goto on terminal '" ^ (nonterminalToString nonterminal) ^ "' from state " ^ (Int.toString state));

  (* Function to return the set of terminals which may follow an input non-terminal in
     any sentential form in the grammar *)
  fun follow Expression = [End]
    | follow Sum        = [End, Plus]
    | follow Difference = [End, Plus, Minus] 
    | follow Product    = [End, Plus, Minus, Multiply]
    | follow Cosine     = [End, Plus, Minus, Multiply]
    | follow Factorial  = [End, Plus, Minus, Multiply, Pling];

  (* Checks that the next token to read is in the follow set of the head of the given production before reducing *)
  fun checkedReduce (s, t) prod = if (member t o follow o prodHead o List.nth) (g, prod) then Reduce(prod) else Error(s, t);

  (* The action function; takes as arguments a state and a terminal (or the end marker)
     and returns an action, which is one of Shift(state), Reduce(production), Accept, 
     Error(message) *)
  fun action (0, Cos)       = Shift(8)
    | action (0, Float)     = Shift(12)
    | action (0, t)         = Error(0, t)
    | action (1, Plus)      = Shift(2)
    | action (1, End)       = Accept
    | action (1, t)         = checkedReduce (1, t) 0
    | action (2, Cos)       = Shift(8)
    | action (2, Float)     = Shift(12)
    | action (2, t)         = Error(2, t)
    | action (3, Minus)     = Shift(4)
    | action (3, t)         = checkedReduce (3, t) 1
    | action (4, Cos)       = Shift(8)
    | action (4, Float)     = Shift(12)
    | action (4, t)         = Error(4, t)
    | action (5, t)         = checkedReduce (5, t) 3
    | action (6, Cos)       = Shift(8)
    | action (6, Float)     = Shift(12)
    | action (6, t)         = Error(6, t)
    | action (7, t)         = checkedReduce (7, t) 5
    | action (8, Cos)       = Shift(8)
    | action (8, Float)     = Shift(12)
    | action (8, t)         = Error(8, t)
    | action (9, t)         = checkedReduce (9, t) 7
    | action (10, Pling)    = Shift(11)
    | action (10, t)        = checkedReduce (10, t) 8
    | action (11, t)        = checkedReduce (11, t) 9
    | action (12, t)        = checkedReduce (12, t) 10
    | action (13, Multiply) = Shift(6)
    | action (13, t)        = checkedReduce (13, t) 6
    | action (14, t)        = checkedReduce (14, t) 4
    | action (15, Minus)    = Shift(4)
    | action (15, t)        = checkedReduce (15, t) 2

    | action (n, t) = raise InternalError("Invalid state " ^ (Int.toString n) ^ " reached on terminal '" ^ (tokenNameToString t) ^ "'");

  (* The goto function; takes as arguments a state and a nonterminal and returns a state
     given by the transition of the LR(0) automaton *)
  fun goto (0, Sum)        = 1
    | goto (0, Difference) = 15
    | goto (0, Product)    = 14
    | goto (0, Cosine)     = 13
    | goto (0, Factorial)  = 10
    | goto (2, Difference) = 3
    | goto (2, Product)    = 14
    | goto (2, Cosine)     = 13
    | goto (2, Factorial)  = 10
    | goto (4, Product)    = 5
    | goto (4, Cosine)     = 13
    | goto (4, Factorial)  = 10
    | goto (6, Product)    = 7
    | goto (6, Factorial)  = 10
    | goto (6, Cosine)     = 13
    | goto (8, Cosine)     = 9
    | goto (8, Factorial)  = 10

    | goto (s, n) = gotoError (s, n)

  (* Push a new token to the forest *)
  fun push t forest = (Lf(t))::forest; 

  (* Return a new forest after a reduction *)
  fun grow (P as Prod(head, symbols)) forest = let

    val toDrop = List.length symbols;

    fun takeN acc 0 _ = acc
      | takeN acc n (x::xs) = takeN (x::acc) (n-1) xs
      | takeN acc n [] = raise InternalError("Attempting to take " ^ (Int.toString n) ^ " too many symbols from the forest during reduction using " ^ (prodToString P));

  in
    (Br(head, takeN [] toDrop forest))::(List.drop (forest, toDrop))
  end;

  (* Return the new state of the stack after reducing by a production *)
  fun reduce (Prod(head, symbols)) states = let
    val base = List.drop (states, List.length symbols)
  in 
      (goto ((List.hd base), head))::base
  end;

  (* Implmentation of the LR(0) parsing algorithm *)
  fun lr forest (states as s::ss) (C as Cons(Token(t, attrs), tf)) = ( case action (s, t) of
      (Shift(next)) => lr (push (Token(t, attrs)) forest) (next :: states) (tf ())
    | (Reduce(prod)) => lr (grow (List.nth (g, prod)) forest) (reduce (List.nth (g, prod)) states) C
    | Accept => List.hd forest
    | (Error(state, tokenName)) => raise ParseError("There is no action on token '" ^ (tokenNameToString tokenName) ^ "' from state " ^ (Int.toString state)) )

  | lr forest states EOS = (PolyML.print(forest); PolyML.print(states); raise InternalError("End of string reached"))
  | lr forest [] stream = (PolyML.print(forest); raise InternalError("States empty before the end of string"))
in
  lr [] [0] s
end;