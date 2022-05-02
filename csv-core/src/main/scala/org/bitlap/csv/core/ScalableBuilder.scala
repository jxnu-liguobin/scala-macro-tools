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
   * Create a custom builder for converting this CSV line to scala values
   *
   * @param line            One CSV line.
   * @param columnSeparator The separator for CSV column value.
   * @return
   */
  def build(line: String, columnSeparator: Char): Scalable[T] = macro DeriveScalableBuilder.buildImpl[T]

  /**
   * Make columnSeparator assign to `,` as default value
   */
  def build(line: String): Scalable[T] = macro DeriveScalableBuilder.buildDefaultImpl[T]

}

object ScalableBuilder {

  def apply[T <: Product]: ScalableBuilder[T] = macro DeriveScalableBuilder.applyImpl[T]

}
