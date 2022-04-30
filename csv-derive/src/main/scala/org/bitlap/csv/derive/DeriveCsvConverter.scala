/*
 * Copyright (c) 2022 bitlap
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.bitlap.csv.derive

import org.bitlap.csv.core.CsvConverter

import scala.reflect.macros.blackbox
import org.bitlap.csv.core.AbstractMacroProcessor

/**
 *
 * @author 梦境迷离
 * @version 1.0,2022/4/29
 */
object DeriveCsvConverter {

  def gen[CC]: CsvConverter[CC] = macro Macro.macroImpl[CC]

  def gen[CC](columnSeparator: Char): CsvConverter[CC] = macro Macro.macroImplWithColumnSeparator[CC]

  class Macro(override val c: blackbox.Context) extends AbstractMacroProcessor(c) {

    def macroImplWithColumnSeparator[CC: c.WeakTypeTag](columnSeparator: c.Expr[Char]): c.Expr[CC] = {
      import c.universe._
      val clazzName = c.weakTypeOf[CC].typeSymbol.name
      val typeName = TypeName(clazzName.decodedName.toString)
      val tree =
        q"""
        new CsvConverter[$typeName] {
            override def from(line: String): Option[$typeName] = org.bitlap.csv.core.DeriveToCaseClass[$typeName](line, $columnSeparator)
            override def to(t: $typeName): String = org.bitlap.csv.core.DeriveToString[$typeName](t, $columnSeparator)
        }
       """
      printTree[CC](c)(force = true, tree).asInstanceOf[c.Expr[CC]]
    }

    def macroImpl[CC: c.WeakTypeTag]: c.Expr[CC] = {
      import c.universe._
      val columnSeparator = ','
      macroImplWithColumnSeparator[CC](c.Expr[Char](q"$columnSeparator"))
    }
  }
}