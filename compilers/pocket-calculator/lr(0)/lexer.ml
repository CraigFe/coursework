datatype 'a stream = Cons of 'a * (unit -> 'a stream)
                   | EOS;

datatype attribute = RealAttr of real; 
datatype tokenName = Plus | Minus | Multiply | Cos | Pling | Float | End;
datatype token = Token of tokenName * attribute list;

fun lex str = let
  exception LexError of string;

  fun listToStream (x::xs) = Cons(x, fn () => listToStream xs)
    | listToStream [] = EOS; 

  fun append v EOS = Cons(v, fn () => EOS)
    | append v (Cons(x, xf)) = Cons(x, fn () => append v (xf()));

  fun stringToStream s = listToStream (String.explode s);
  val s = stringToStream str
  fun inRange (l, u) v = (v >= l andalso v <= u);
  fun digitToInt c = Char.ord c - 48;
  fun isNonZeroDigit c = inRange (1, 9) (digitToInt c);
  fun isDigit c = isNonZeroDigit c orelse c = #"0";
  fun isFloatPrefix c = isDigit c orelse c = #"+" orelse c = #"-";

  fun length (Cons(x,xf)) = 1 + length (xf())
    | length EOS = 0;

  fun err action (Cons(c,xf)) = raise LexError("Error in lexing " ^ action ^ ". Unexpected character '" ^ Char.toString c ^ "' at position " ^ Int.toString (length s - length (xf())) ^ ".")
    | err action EOS          = raise LexError("Unexpected end of input while lexing " ^ action ^ ".")

  fun cosAccept s = let
    fun cosErr s = err "a cosine operator" s
    fun c1 (Cons(#"c", xf)) = c2 (xf())
      | c1 s = cosErr s

    and c2 (Cons(#"o", xf)) = c3 (xf())
      | c2 s = cosErr s

    and c3 (Cons(#"s", xf)) = Cons(Token(Cos, []), fn () => tokenAccept (xf()))
      | c3 s = cosErr s
  in c1 s end

  and floatAccept s = let

    val states = ["the first character of a float",  "the first character of a decimal", "", "", "the first character after a decimal point", 
    "" , "the first character of the exponent", "the first character after a sign in the exponent"]

    val chars = ref [];
    fun prepend c = chars := c :: !chars;
    val float = fn () => case (Real.fromString o implode o List.rev) (!chars) of
        SOME f => Token(Float, [RealAttr f])
      | NONE => raise LexError("Could not convert string '" ^ (implode (List.rev (!chars))) ^ "' to real value")

    fun floatErr n C = err (List.nth (states, n) ^ " (state " ^ Int.toString n ^ ")") C

    fun f0 (Cons(#"+", xf)) = (prepend #"+"; f1 (xf()))
      | f0 (Cons(#"-", xf)) = (prepend #"-"; f1 (xf()))
      | f0 (Cons(#"0", xf)) = (prepend #"0"; f3 (xf()))
      | f0 (C as Cons(c, xf)) = if (isNonZeroDigit c) then (prepend c; f2 (xf())) else floatErr 0 C
      | f0 EOS = floatErr 0 EOS

    and f1 (Cons(#".", xf)) = (prepend #"."; f4 (xf()))
      | f1 (Cons(#"0", xf)) = (prepend #"0"; f3 (xf()))
      | f1 (C as Cons(c, xf)) = if (isNonZeroDigit c) then (prepend c; f2 (xf())) else floatErr 1 C
      | f1 EOS = floatErr 1 EOS

      (* Accepting state *)
    and f2 (Cons(#".", xf)) = (prepend #"."; f4 (xf()))
      | f2 (Cons(#"e", xf)) = (prepend #"e"; f6 (xf()))
      | f2 (C as Cons(c, xf)) = if (isDigit c) then (prepend c; f2 (xf()))
          else Cons(float (), fn () => pmAccept C)
      | f2 EOS = Cons(float (), fn () => EOS)

      (* Accepting state *)
    and f3 (Cons(#".", xf)) = (prepend #"."; f4 (xf()))
      | f3 (Cons(#"e", xf)) = (prepend #"e"; f6 (xf()))
      | f3 (C as Cons(c, xf)) = Cons(float (), fn () => pmAccept C)
      | f3 EOS = Cons(float (), fn () => EOS)

    and f4 (C as Cons(c, xf)) = if (isDigit c) then (prepend c; f5 (xf())) else floatErr 4 C
      | f4 EOS = floatErr 4 EOS

      (* Accepting state *)
    and f5 (Cons(#"e", xf)) = (prepend #"e"; f6 (xf()))
      | f5 (C as Cons(c, xf)) = if (isDigit c) then (prepend c; f5 (xf()))
          else Cons(float (), fn () => pmAccept C)
      | f5 EOS = Cons(float (), fn () => EOS)

    and f6 (Cons(#"+", xf)) = (prepend #"+"; f7 (xf()))
      | f6 (Cons(#"-", xf)) = (prepend #"-"; f7 (xf()))
      | f6 (Cons(#"0", xf)) = (prepend #"0"; f8 (xf()))
      | f6 (C as Cons(c, xf)) = if (isNonZeroDigit c) then (prepend c; f9 (xf())) else floatErr 6 C
      | f6 EOS = floatErr 6 EOS

    and f7 (Cons(#"0", xf)) = (prepend #"0"; f8 (xf()))
      | f7 (C as Cons(c, xf)) = if (isNonZeroDigit c) then (prepend c; f9 (xf())) else floatErr 7 C
      | f7 EOS = floatErr 7 EOS

      (* Accepting state *)
    and f8 (C as Cons(_, _)) = Cons(float (), fn () => pmAccept C)
      | f8 EOS = Cons(float (), fn () => EOS)

    and f9 (C as Cons(c,xf)) = if (isDigit c) then (prepend c; f9 (xf()))
            else Cons(float (), fn () => pmAccept C)
      | f9 EOS = Cons(float (), fn () => EOS)

  in f0 s end

  and pmAccept (Cons(#"+", xf)) = Cons(Token(Plus, []), fn () => tokenAccept (xf()))
    | pmAccept (Cons(#"-", xf)) = Cons(Token(Minus, []), fn () => tokenAccept (xf()))
    | pmAccept s = tokenAccept s

  and tokenAccept (Cons(#"*", xf)) = Cons(Token(Multiply, []), fn () => tokenAccept (xf()))
    | tokenAccept (Cons(#"!", xf)) = Cons(Token(Pling, []), fn () => pmAccept (xf()))
    | tokenAccept (C as Cons(#"c", xf)) = cosAccept C
    | tokenAccept (F as Cons(c, xf)) = if (isFloatPrefix c) then floatAccept F else raise LexError("Invalid character at the start of a token")
    | tokenAccept EOS = EOS;

in
  append (Token(End, [])) (tokenAccept s)
end;