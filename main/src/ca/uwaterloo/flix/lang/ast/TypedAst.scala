package ca.uwaterloo.flix.lang.ast

sealed trait TypedAst

object TypedAst {

  // TODO

  sealed trait Literal extends TypedAst {
    def tpe: TypedAst.Type
  }

  object Literal {

    case object Unit extends TypedAst.Literal {
      final val tpe = TypedAst.Type.Unit
    }

    case class Bool(literal: scala.Boolean) extends TypedAst.Literal {
      final val tpe = TypedAst.Type.Bool
    }

    case class Int(literal: scala.Int) extends TypedAst.Literal {
      final val tpe = TypedAst.Type.Int
    }

    case class Str(literal: java.lang.String) extends TypedAst.Literal {
      final val tpe = TypedAst.Type.Str
    }

    case class Tag(name: ParsedAst.QName, ident: ParsedAst.Ident, literal: TypedAst.Literal, tpe: TypedAst.Type) extends TypedAst.Literal

    case class Tuple(elms: Seq[TypedAst.Literal], tpe: TypedAst.Type) extends TypedAst.Literal

  }

  sealed trait Expression extends TypedAst {
    def tpe: Type
  }

  object Expression {

    case class IfThenElse(e1: TypedAst.Expression, e2: TypedAst.Expression, e3: TypedAst.Expression, tpe: TypedAst.Type) extends TypedAst.Expression

    case class Let(ident: ParsedAst.Ident, value: TypedAst.Expression, body: TypedAst.Expression, tpe: TypedAst.Type) extends TypedAst.Expression

  }

  sealed trait Pattern extends TypedAst

  object Pattern {

    case class Wildcard(location: SourceLocation) extends TypedAst.Pattern

    case class Var(ident: ParsedAst.Ident) extends TypedAst.Pattern

    case class Lit(literal: ResolvedAst.Literal) extends TypedAst.Pattern

    case class Tag(name: ResolvedAst.RName, ident: ParsedAst.Ident, p: TypedAst.Pattern) extends TypedAst.Pattern

    case class Tuple(elms: Seq[TypedAst.Pattern]) extends TypedAst.Pattern

  }

  sealed trait Type extends TypedAst

  object Type {

    case object Unit extends TypedAst.Type

    case object Bool extends TypedAst.Type

    case object Int extends TypedAst.Type

    case object Str extends TypedAst.Type

    case class Tag(ident: ParsedAst.Ident, tpe: TypedAst.Type) extends TypedAst.Type

    case class Enum(variants: Map[String, TypedAst.Type.Tag]) extends TypedAst.Type

    case class Tuple(elms: Seq[TypedAst.Type]) extends TypedAst.Type

    case class Parametric(name: ResolvedAst.RName, elms: Seq[TypedAst.Type]) extends TypedAst.Type


  }


}
