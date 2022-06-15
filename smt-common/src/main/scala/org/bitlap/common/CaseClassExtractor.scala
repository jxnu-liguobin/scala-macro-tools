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

package org.bitlap.common

import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import scala.reflect.macros.whitebox
import scala.reflect.ClassTag
import scala.reflect.runtime.{ universe => ru }
import scala.reflect.runtime.universe._
import scala.util.{ Failure, Success }

/** @author
 *    梦境迷离
 *  @version 1.0,6/8/22
 */
object CaseClassExtractor {

  /** Using the characteristics of the product type to get the field value should force the conversion externally
   *  (safely).
   */
  def ofValue[T <: Product](t: T, field: CaseClassField): Option[Any] = macro macroImpl[T]

  def macroImpl[T: c.WeakTypeTag](
    c: whitebox.Context
  )(t: c.Expr[T], field: c.Expr[CaseClassField]): c.Expr[Option[Any]] = {
    import c.universe._
    // scalafmt: { maxColumn = 400 }
    val tree =
      q"""
       if ($t == null) None else {
          val _field = $field
          _field.${TermName(CaseClassField.fieldNamesTermName)}.find(kv => kv._2 == _field.${TermName(CaseClassField.stringifyTermName)})
          .map(kv => $t.productElement(kv._1))       
       }
     """
    exprPrintTree[Option[Any]](c)(tree)

  }

  def exprPrintTree[Field: c.WeakTypeTag](c: whitebox.Context)(resTree: c.Tree): c.Expr[Field] = {
    c.info(
      c.enclosingPosition,
      s"\n###### Time: ${ZonedDateTime.now().format(DateTimeFormatter.ISO_ZONED_DATE_TIME)} Expanded macro start ######\n" + resTree
        .toString() + "\n###### Expanded macro end ######\n",
      force = false
    )
    c.Expr[Field](resTree)
  }

  /** Using scala reflect to get the field value (safely).
   */
  @deprecated
  def reflectValue[T: ru.TypeTag](obj: T, field: CaseClassField)(implicit
    classTag: ClassTag[T]
  ): Option[field.Field] = {
    val mirror = ru.runtimeMirror(getClass.getClassLoader)
    val fieldOption = scala.util.Try(
      getMethods[T]
        .filter(_.name.toTermName.decodedName.toString == field.stringify)
        .map(m => mirror.reflect(obj).reflectField(m).get)
        .headOption
        .map(_.asInstanceOf[field.Field])
    )
    fieldOption match {
      case Success(value)     => value
      case Failure(exception) => exception.printStackTrace(); None
    }
  }

  def getMethods[T: ru.TypeTag]: List[ru.MethodSymbol] = typeOf[T].members.collect {
    case m: MethodSymbol if m.isCaseAccessor => m
  }.toList
}