(* The datatypes of L1 *)

datatype typeL1 = int
                | unit
                | bool

datatype typeLoc = IntRef

type typeEnv = (loc * typeLoc) list 