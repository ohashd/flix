/*
 * Copyright 2015-2016 Magnus Madsen
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ca.uwaterloo.flix.language

import java.nio.file.Path

import ca.uwaterloo.flix.util.ConsoleCtx
import org.parboiled2.ParserInput

package object ast {

  // TODO: Move into Ast.

  /**
    * A common super-type for sources.
    */
  sealed trait SourceInput {
    def format: String = this match {
      case SourceInput.TxtFile(p) => p.getFileName.toString
      case SourceInput.ZipFile(p) => p.getFileName.toString
      case SourceInput.Str(_) => "???"
    }
  }

  object SourceInput {

    /**
      * An source that is backed by a regular string.
      */
    case class Str(str: String) extends SourceInput

    /**
      * An source that is backed by a regular file.
      */
    case class TxtFile(path: Path) extends SourceInput

    /**
      * An source that is backed by a zip file.
      */
    case class ZipFile(path: Path) extends SourceInput

  }

  object SourcePosition {
    /**
      * Represents an unknown source position.
      */
    val Unknown: SourcePosition = SourcePosition(SourceInput.Str(""), 0, 0, None)
  }

  /**
    * A class that represent a physical source position inside a source input.
    *
    * @param line  the line number.
    * @param col   the column number.
    * @param input the parser input.
    */
  case class SourcePosition(source: SourceInput, line: Int, col: Int, input: Option[ParserInput])

  /**
    * Companion object for the [[SourceLocation]] class.
    */
  object SourceLocation {
    /**
      * Represents an unknown source location.
      */
    val Unknown: SourceLocation = mk(SourcePosition.Unknown, SourcePosition.Unknown)

    def mk(b: SourcePosition, e: SourcePosition): SourceLocation = {
      assert(b.source == e.source)
      val lineAt = b.input match {
        case None => (i: Int) => ""
        case Some(input) => (i: Int) => input.getLine(i)
      }
      SourceLocation(b.source, b.line, b.col, e.line, e.col, lineAt)
    }

    implicit object Order extends Ordering[SourceLocation] {

      import scala.math.Ordered.orderingToOrdered

      def compare(x: SourceLocation, y: SourceLocation): Int =
        (x.source.format, x.beginLine, x.beginCol) compare(y.source.format, y.beginLine, y.beginCol)
    }

  }

  /**
    * A class that represents the physical source location of some parsed syntactic entity.
    *
    * @param source    the source input.
    * @param beginLine the line number where the entity begins.
    * @param beginCol  the column number where the entity begins.
    * @param endLine   the line number where the entity ends.
    * @param endCol    the column number where the entity ends.
    * @param lineAt    a closure which returns the text at the given line offset.
    */
  case class SourceLocation(source: SourceInput, beginLine: Int, beginCol: Int, endLine: Int, endCol: Int, lineAt: Int => String) {

    /**
      * Returns a formatted string representation of `this` source location.
      */
    def format: String = s"${source.format}:$beginLine:$beginCol"

    /**
      * Returns this line of code with the source location underlined.
      */
    def highlight(implicit consoleCtx: ConsoleCtx): String = if (beginLine == endLine) underline else leftline

    /**
      * Highlights this source location with red arrows under the text.
      */
    private def underline(implicit consoleCtx: ConsoleCtx): String = {
      val lineNo = beginLine.toString + "|"
      val line1 = lineNo + lineAt(beginLine) + "\n"
      val line2 = " " * (beginCol + lineNo.length - 1) + consoleCtx.red("^" * (endCol - beginCol))
      line1 + line2
    }

    /**
      * Highlights this source location with red arrows left of the text.
      */
    private def leftline(implicit consoleCtx: ConsoleCtx): String = {
      val sb = new StringBuilder()
      for (lineNo <- beginLine to endLine) {
        val currentLine = lineAt(lineNo)
        sb.
          append(lineNo).append("|").
          append(consoleCtx.red(">")).
          append(currentLine).
          append("\n")
      }
      sb.toString()
    }

  }

  object Time {
    val Default: Time = Time(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0)
  }

  /**
    * A class that tracks the amount of time spent in each phase of the compiler.
    */
  case class Time(parser: Long,
                  weeder: Long,
                  resolver: Long,
                  typer: Long,
                  propertyGen: Long,
                  verifier: Long,
                  lambdaLift: Long,
                  simplifier: Long,
                  varNumbering: Long,
                  codeGen: Long,
                  solver: Long)

}
