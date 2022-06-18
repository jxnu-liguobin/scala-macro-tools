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

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

/** @author
 *    梦境迷离
 *  @version 1.0,6/15/22
 */
class TransformableTest extends AnyFlatSpec with Matchers {

  "TransformableTest simple case" should "ok for Transformable" in {

    case class A1(a: String, b: Int, cc: Long, d: Option[String])
    case class A2(a: String, b: Int, c: Int, d: Option[String])

    val a = A1("hello", 1, 2, None)
    val b: A2 = Transformable[A1, A2] // todo `fromField: Long` type Long cannot be ignored.
      .mapField[Long, Int](_.cc, _.c, (fromField: Long) => if (fromField > 0) fromField.toInt else 0)
      .instance
      .transform(a)

    b.toString shouldEqual "A2(hello,1,2,None)"

    case class B1(d: List[String])
    case class B2(d: Seq[String])

    val b1 = B1(List("hello"))
    // List => Seq  not need mapping field
    val b2: B2 = Transformable[B1, B2].instance.transform(b1)
    b2.toString shouldEqual "B2(List(hello))"
  }

  "TransformableTest simple case" should "ok for implicit Transformable" in {
    case class A1(a: String, b: Int, cc: Long, d: Option[String])
    case class A2(a: String, b: Int, c: Int, d: Option[String])
    val a = A1("hello", 1, 2, None)
    implicit val transformer = Transformable[A1, A2]
      .mapField(_.b, _.c)
      .mapField(_.a, _.a)
      .mapField[Option[String], Option[String]](_.d, _.d, (map: Option[String]) => map)
      .instance

    Transformer[A1, A2].transform(a).toString shouldEqual "A2(hello,1,1,None)"
  }

  "TransformableTest type not match" should "error if field type is incompatible" in {
    """
      |
      |    case class A1(a: String, b: Int, cc: Long, d: Option[String])
      |    case class A2(a: String, b: Int, c: Int, d: Option[String])
      |    val a = A1("hello", 1, 2, None)
      |    val b: A2 = Transformable[A1, A2]
      |      .mapField(_.cc, _.c)
      |      .instance
      |      .transform(a)
      |""".stripMargin shouldNot compile
  }

  "TransformableTest simple case for nest field" should "ok when field is case class" in {
    case class C1(j: Int)
    case class D1(c1: C1)
    case class C2(j: Int)
    case class D2(c2: C2)

    implicit val cTransformer: Transformer[C1, C2] = Transformable[C1, C2].instance
    implicit val dTransformer: Transformer[D1, D2] = Transformable[D1, D2].mapField(_.c1, _.c2).instance

    val d1     = D1(C1(1))
    val d2: D2 = Transformer[D1, D2].transform(d1)
    println(d2)
  }

  "TransformableTest more complex case for nest field" should "ok when field is list with case class" in {
    case class C1(j: Int)
    case class D1(c1: List[C1])
    case class C2(j: Int)
    case class D2(c2: List[C2])

    implicit val cTransformer: Transformer[C1, C2] = Transformable[C1, C2].instance
    implicit val dTransformer: Transformer[D1, D2] = Transformable[D1, D2].mapField(_.c1, _.c2).instance

    val d1     = D1(List(C1(1), C1(2)))
    val d2: D2 = Transformer[D1, D2].transform(d1)
    println(d2)
  }

  "TransformableTest more complex case for two-layer nest field" should "ok for implicit and non-implicit(mapField)" in {
    case class C1(j: Int)
    case class D1(c1: List[List[C1]])

    case class C2(j: Int)
    case class D2(c2: List[List[C2]]) // Nesting of the second layer

    object C1 {
      implicit val cTransformer: Transformer[C1, C2] = Transformable[C1, C2].instance
    }

    object D1 {
      implicit val dTransformer: Transformer[D1, D2] = Transformable[D1, D2]
        .mapField[List[List[C1]], List[List[C2]]](
          _.c1,
          _.c2,
          // implicit values of nested dependencies cannot be at the same level, so move it to companion their object
          (c1: List[List[C1]]) => c1.map(_.map(Transformer[C1, C2].transform(_)))
        )
        .instance
    }

    val d1     = D1(List(List(C1(1), C1(2))))
    val d2: D2 = Transformer[D1, D2].transform(d1)
    d2.toString shouldBe "D2(List(List(C2(1), C2(2))))"
  }

  "TransformableTest different type" should "compile ok if can use weak conformance" in {
    case class A1(a: String, b: Int, cc: Int, d: Option[String]) // weak conformance
    case class A2(a: String, b: Int, c: Long, d: Option[String])
    object A1 {
      implicit val aTransformer: Transformer[A1, A2] = Transformable[A1, A2].mapField(_.cc, _.c).instance
    }
    val a1 = A1("hello", 1, 2, None)
    val a2 = Transformer[A1, A2].transform(a1)
    a2.toString shouldBe "A2(hello,1,2,None)"

  }

  "TransformableTest type cannot match" should "compile failed if can't use weak conformance" in {
    """
      | case class A1(a: String, b: Int, cc: Long, d: Option[String]) // Can't to use weak conformance, must use `mapField(?,?,?)` method for it.
      |    case class A2(a: String, b: Int, c: Int, d: Option[String])
      |    object A1 {
      |      
      |      implicit val aTransformer: Transformer[A1, A2] = Transformable[A1, A2].mapField(_.cc,_.c).instance
      |    }
      |    val a1 = A1("hello", 1, 2, None)
      |    val a2 = Transformer[A1, A2].transform(a1)
      |    a2.toString shouldBe "A2(hello,1,2,None)"
      |""".stripMargin shouldNot compile
  }

  "TransformableTest more complex case to use implicit Transformer" should "compile ok" in {
    import org.bitlap.common.models.from._
    import org.bitlap.common.models.to._
    val fromRow =
      List(FRow(List("this is row data1", "this is row data2")))
    val fromRowSet      = FRowSet.apply(fromRow, 100000)
    val fromColumnDesc  = List(FColumnDesc("this is column name1"), FColumnDesc("this is column name2"))
    val fromTableSchema = FTableSchema(fromColumnDesc)
    val fromQueryResult = FQueryResult(tableSchema = fromTableSchema, rows = fromRowSet)

    val toRow =
      List(TRow(List("this is row data1", "this is row data2")))
    val toRowSet            = TRowSet(100000, toRow)
    val toColumnDesc        = List(TColumnDesc("this is column name1"), TColumnDesc("this is column name2"))
    val toTableSchema       = TTableSchema(toColumnDesc)
    val expectToQueryResult = TQueryResult(ttableSchema = toTableSchema, trows = toRowSet)

    val actualToQueryResult = Transformer[FQueryResult, TQueryResult].transform(fromQueryResult)

    actualToQueryResult shouldBe expectToQueryResult
  }

  "TransformableTest From have fewer fields than To" should "compile error" in {
    """
      |    case class B1(a: List[String])
      |    case class B2(a: List[String], b: Int)
      |    val b2 = Transformable[B1, B2].instance.transform(B1(List.empty))
      |    println(b2)
      |""".stripMargin shouldNot compile
  }

  "TransformableTest From have more fields than To" should "ok" in {
    case class B1(a: List[String], b: Int)
    case class B2(a: List[String])
    val b2 = Transformable[B1, B2].instance.transform(B1(List.empty, 1))
    println(b2)
  }
}
