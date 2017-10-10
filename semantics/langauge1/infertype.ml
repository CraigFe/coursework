PolyML.use "interpreter.ml";
PolyML.use "types.ml";

fun infertype gamma exp = let

  fun infer (Integer n) = SOME int
    | infer (Boolean b) = SOME bool
    | infer (Op (e1, opr, e2)) = (
        case (infer e1, opr, infer e2) of
            (SOME int, GTEQ, SOME int) => SOME bool
          | (SOME int, PLus, SOME int) => SOME int
          | _ => NONE
      )

    | infer (If (e1, e2, e3)) = (
        case (infer e1, infer e2, infer e3) of
            (SOME bool, SOME t2, SOME t3) => if t2 = t3 then SOME t2 else NONE
          | _ => NONE
      )

    | infer (Deref l) = (
        case lookup (gamma, l) of
            SOME IntRef => SOME int
          | NONE => NONE
      )

    | infer (Assign (l, e)) = (
        case (lookup (gamma, l), infer e) of
            (SOME IntRef, SOME int) => SOME unit
          | _ => NONE
      )

    | infer (Seq (e1, e2)) = (
        case (infer e1, infer e2) of
            (SOME unit, SOME t2) => SOME t2
          | _ => NONE
      )

    | infer (While (e1, e2)) = (
        case (infer e1, infer e2) of
            (SOME bool, SOME unit) => SOME unit
          | _ => NONE
      )

    | infer Skip = SOME unit

in infer exp end;