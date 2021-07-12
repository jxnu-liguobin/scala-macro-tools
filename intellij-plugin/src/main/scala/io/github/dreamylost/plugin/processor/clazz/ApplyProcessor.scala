package io.github.dreamylost.plugin.processor.clazz

import io.github.dreamylost.plugin.processor.ProcessType.ProcessType
import io.github.dreamylost.plugin.processor.{ AbsProcessor, ProcessType }
import org.jetbrains.plugins.scala.lang.psi.api.toplevel.typedef.{ ScClass, ScObject, ScTypeDefinition }

/**
 * Desc: Processor for annotation apply
 *
 * Mail: chk19940609@gmail.com
 * Created by IceMimosa
 * Date: 2021/7/8
 */
class ApplyProcessor extends AbsProcessor {

  override def needCompanion: Boolean = true

  override def process(source: ScTypeDefinition, typ: ProcessType): Seq[String] = {
    typ match {
      case ProcessType.Method =>
        source match {
          case obj: ScObject =>
            val clazz = obj.fakeCompanionClassOrCompanionClass.asInstanceOf[ScClass]
            val nameAndTypes = getConstructorParameters(clazz, withSecond = false)
              .map(o => s"${o._1}: ${o._2}")
              .mkString(", ")
            Seq(s"def apply($nameAndTypes): ${clazz.getName} = ???")
          case _ => Nil
        }
      case _ => Nil
    }
  }
}