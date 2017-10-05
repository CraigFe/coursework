PolyML.use "lexer.ml";

datatype nonterminal = Expression | Sum | Difference | Product | Cosine | Factorial;
datatype symbol = N of nonterminal | T of tokenName;
datatype production = Prod of nonterminal * symbol list;
type grammar = production list;

datatype action = Shift of int
                | Reduce of int
                | Accept
                | Error of string;

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

  (* The action function; takes as arguments a state and a terminal (or the end marker)
     and returns an action, which is one of Shift(state), Reduce(production), Accept, 
     Error(message) *)
  fun action (0, Cos) = Shift(8)
    | action (0, Float) = Shift(12)
    | action (0, t) = actionError (0, t)
    | action (1, Plus) = Shift(2)
    | action (1, End) = Accept
    | action (1, t) = if (member t (follow Expression)) then Reduce(0) else actionError (1, t)
    | action (2, Cos) = Shift(8)
    | action (2, Float) = Shift(12)
    | action (2, t) = actionError (2, t)
    | action (3, Minus) = Shift(4)
    | action (3, t) = if (member t (follow Sum)) then Reduce(1) else actionError (3, t)
    | action (4, Cos) = Shift(8)
    | action (4, Float) = Shift(12)
    | action (4, t) = actionError (4, t)
    | action (5, t) = if (member t (follow Difference)) then Reduce(3) else actionError (5, t)
    | action (6, Cos) = Shift(8)
    | action (6, Float) = Shift(12)
    | action (6, t) = actionError (6, t)
    | action (7, t) = if (member t (follow Product)) then Reduce(5) else actionError (7, t)
    | action (8, Cos) = Shift(8)
    | action (8, Float) = Shift(12)
    | action (8, t) = actionError (8, t)
    | action (9, t) = if (member t (follow Cosine)) then Reduce(7) else actionError (9, t)
    | action (10, Pling) = Shift(11)
    | action (10, t) = if (member t (follow Cosine)) then Reduce(8) else actionError (10, t)
    | action (11, t) = if (member t (follow Factorial)) then Reduce(9) else actionError (11, t)
    | action (12, t) = if (member t (follow Factorial)) then Reduce(10) else actionError (12, t)
    | action (13, Multiply) = Shift(6)
    | action (13, t) = if (member t (follow Product)) then Reduce(6) else actionError (13, t)
    | action (14, t) = if (member t (follow Difference)) then Reduce(4) else actionError (14, t)
    | action (15, Minus) = Shift(4)
    | action (15, t) = if (member t (follow Sum)) then Reduce(2) else actionError (15, t)
    | action (n, t) = raise InternalError("Invalid state " ^ (Int.toString n) ^ " reached on terminal '" ^ (tokenNameToString t) ^ "'");


  (* The goto function; takes as arguments a state and a nonterminal and returns a state
     given by the transition of the LR(0) automaton *)
  fun goto (0, Sum) = 1
    | goto (0, Difference) = 15
    | goto (0, Product) = 14
    | goto (0, Cosine) = 13
    | goto (0, Factorial) = 10

    | goto (2, Difference) = 3
    | goto (2, Product)    = 14
    | goto (2, Cosine)     = 13
    | goto (2, Factorial)  = 10

    | goto (4, Product) = 5
    | goto (4, Cosine) = 13
    | goto (4, Factorial) = 10

    | goto (6, Product) = 7
    | goto (6, Factorial) = 10
    | goto (6, Cosine) = 13

    | goto (8, Cosine) = 9
    | goto (8, Factorial) = 10

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
      (Shift(next)) => (PolyML.print("Shifting state " ^ (Int.toString next)); lr (push (Token(t, attrs)) forest) (next :: states) (tf ()))
    | (Reduce(prod)) => (PolyML.print("Reducing by production " ^ (prodToString (List.nth (g, prod)))); lr (grow (List.nth (g, prod)) forest) (reduce (List.nth (g, prod)) states) C)
    | Accept => List.hd forest
    | (Error(m)) => raise ParseError(m) )

  | lr forest states EOS = (PolyML.print(forest); PolyML.print(states); raise InternalError("End of string reached"))
  | lr forest [] stream = (PolyML.print(forest); raise InternalError("States empty before the end of string"))
in
  lr [] [0] s
end;