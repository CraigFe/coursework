PolyML.use "parser.ml";

fun map _ []      = []
  | map f (x::xs) = f x :: map f xs;

fun foldr _ s [] = s
  | foldr f s (x::xs) = f(x, foldr f s xs);

fun factorial (n: real) = if (n > 0.0) then n * (factorial (n-1.0)) else 1.0;

(* Takes a syntax tree produced by the parser and flattens it to a list of tokens in postfix order *)
fun flatten (Br(_, [x1,x2,x3] ))             = foldr (op @) [] (map flatten [x1,x3,x2]) (* Diadic infix operators *)
  | flatten (Br(_, [Lf(Token(Cos, _)), x2])) = (flatten x2) @ [(Token(Cos, []))]        (* Monadic prefix operator *)
  | flatten (Br(_, xs))                      = foldr (op @) [] (map flatten xs)         (* Postfix operators *)
  | flatten (Lf(v)) = [v];

(* Takes a list of tokens in postfix order and calculates the real value of the expression *)
fun calculate ts = let
  exception CalculateError;

  (* Keeps a stack of real values for postfix calculation *)
  fun calc [] [y] = y
  | calc ((Token(Float, [RealAttr(v)]))::ts) ys                  = calc ts (v::ys)
  | calc ((Token(Cos, []))::ts)             (y::ys)              = calc ts ((Math.cos y)::ys)
  | calc ((Token(Pling, []))::ts)           (y::ys)              = calc ts ((factorial y)::ys)
  | calc ((Token(Plus, []))::ts)            ((y1: real)::y2::ys) = calc ts ((y1 + y2)::ys)
  | calc ((Token(Minus, []))::ts)           ((y1: real)::y2::ys) = calc ts ((y2 - y1)::ys)
  | calc ((Token(Multiply, []))::ts)        ((y1: real)::y2::ys) = calc ts ((y1 * y2)::ys)
  | calc _ _ = raise CalculateError;
in
  calc ts []
end;