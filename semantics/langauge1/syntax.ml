(* The abstract syntax of the language *)

type loc = string;

datatype oper = Plus | GTEQ;

datatype expr = Integer of int
              | Boolean of bool
              | Op of expr * oper * expr
              | If of expr * expr * expr
              | Assign of loc * expr
              | Deref of loc
              | Skip
              | Seq of expr * expr
              | While of expr * expr;