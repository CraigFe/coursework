(* An interpreter for the semantics of L1 *)

PolyML.use "syntax.ml";

fun isValue (Integer _) = true
  | isValue (Boolean _) = true
  | isValue (Skip) = true
  | isValue _ = false;

type store = (loc * int) list;

fun lookup ([]            , l') = NONE
  | lookup ((l, v)::store, l') = if l = l' then SOME v else lookup (store, l');

fun update (store, (new as (l', v'))) = let

  fun helper _ [] = NONE
    | helper acc (S as ((l, v)::rest)) = 
        if l = l' then SOME (acc @ (new::rest))
                  else helper ((l, v)::acc) rest;

in
  helper [] store
end;

fun reduce (Integer n, s) = NONE
  | reduce (Boolean b, s) = NONE
  | reduce (Op (e1, opr, e2), s) = (

    case (e1, opr, e2) of
        (Integer n1, Plus, Integer n2) => SOME(Integer (n1 + n2), s)  (* op+  *)
      | (Integer n1, GTEQ, Integer n2) => SOME(Boolean (n1 >= n2), s) (* op>= *)
      | (e1, opr, e2) => if isValue e1 

      then case reduce (e2, s) of
            SOME(e2', s') => SOME(Op (e1,opr,e2'), s') (* op2 *)
          | NONE => NONE

      else case reduce (e1, s) of
            SOME(e1', s') => SOME(Op (e1',opr,e2), s') (* op1 *)
          | NONE => NONE
    )

  | reduce (If (e1, e2, e3), s) = (

    case e1 of
        Boolean true =>  SOME (e2, s)                 (* if1 *)
      | Boolean false => SOME (e3, s)                 (* if2 *)
      | _ => case reduce (e1, s) of
          SOME(e1', s') => SOME(If (e1', e2, e3), s') (* if3 *)
        | NONE => NONE
    )

  | reduce (Deref l, s) = (

    case lookup (s, l) of
        SOME v => SOME(Integer v, s) (* deref *)
      | NONE => NONE
    )

  | reduce (Assign (l, e), s) = (

    case e of
        Integer v' => (
          case update (s, (l, v')) of
              SOME s' => SOME(Skip, s') (* assign1 *)
            | NONE => NONE
        )
      | _ => (
          case reduce (e, s) of
              SOME (e', s') => SOME(Assign (l, e'), s') (* assign2 *)
            | NONE => NONE
        )
    )

  | reduce (While (e1, e2), s) = SOME (If ())