/*
 * Copyright (c) 2022 org.bitlap
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

package org.bitlap.tools.cacheable

import scala.annotation.{ compileTimeOnly, StaticAnnotation }
import org.bitlap.tools.cacheable.macros.CacheableMacro

/**
 * A distributed cache for zio.
 *
 * @author 梦境迷离
 * @param verbose Whether to enable detailed log.
 * @since 2022/3/18
 * @version 1.0
 */
@compileTimeOnly("enable macro to expand macro annotations")
final class cacheable(verbose: Boolean = false) extends StaticAnnotation {

  def macroTransform(annottees: Any*): Any = macro CacheableMacro.CacheableProcessor.impl
}
