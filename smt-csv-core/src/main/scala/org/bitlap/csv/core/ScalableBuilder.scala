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

package org.bitlap.csv.core

import org.bitlap.csv.core.macros.DeriveScalableBuilder
import java.io.InputStream

/**
 * Builder to create a custom Csv Decoder.
 *
 * @author 梦境迷离
 * @version 1.0,2022/4/30
 */
class ScalableBuilder[T] {

  /**
   * Convert any Scala types to this CSV column string.
   *
   * @param scalaField The field in scala case class.
   * @param value      This function specifies how you want to convert this CSV column to a scala type.
   * @tparam SF The field type, generally, it is not necessary to specify, but it is safer if specify.
   * @return
   */
  def setField[SF](scalaField: T => SF, value: String => SF): ScalableBuilder[T] =
    macro DeriveScalableBuilder.setFieldImpl[T, SF]

  /**
   * Create a custom builder for converting this CSV line to scala values.
   *
   * @param line            One CSV line.
   * @param columnSeparator The separator for CSV column value.
   * @return
   */
  def convert(line: String, columnSeparator: Char): Option[T] = macro DeriveScalableBuilder.convertOneImpl[T]

  /**
   * Make columnSeparator assign to `,` as default value.
   */
  def convert(line: String): Option[T] = macro DeriveScalableBuilder.convertOneDefaultImpl[T]

  /**
   * Convert all CSV lines to the sequence of Scala case class.
   *
   * @param lines           All CSV lines.
   * @param columnSeparator The separator for CSV column value.
   * @return
   */
  def convert(lines: List[String], columnSeparator: Char): List[Option[T]] = macro DeriveScalableBuilder.convertImpl[T]

  /**
   * Make columnSeparator assign to `,` as default value.
   */
  def convert(lines: List[String]): List[Option[T]] = macro DeriveScalableBuilder.convertDefaultImpl[T]

  /**
   * Read all CSV lines of the file and convert them to the sequence of Scala case class.
   *
   * @param file    InputStream of the CSV file.
   * @param charset String charset of the CSV file content.
   * @return
   */
  def convertFrom(file: InputStream, charset: String): List[Option[T]] = macro DeriveScalableBuilder.convertFromFileImpl[T]

}

object ScalableBuilder {

  def apply[T <: Product]: ScalableBuilder[T] = macro DeriveScalableBuilder.applyImpl[T]

}
