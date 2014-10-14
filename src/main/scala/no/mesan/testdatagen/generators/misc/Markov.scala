package no.mesan.testdatagen.generators.misc

import no.mesan.testdatagen.utils.IO
import no.mesan.testdatagen.{GeneratorImpl, RandomElem}

import scala.annotation.tailrec

/**
 * Simple Markov Chain generator.
 * Generates "gibberish" based on one or more input files.
 * A default input file (parameterless apply method) is provided containing text from
 * http://www.regjeringen.no/nb/dep/fad/dok/regpubl/stmeld/2008-2009/stmeld-nr-19-2008-2009-,
 * any file(s) may be read and concatenated to provide a basis.
 * The get methods return a string containing N words randomly selected from
 * the input.
 */
class Markov extends GeneratorImpl[String] with RandomElem {

  type WordMap= Map[String, List[String]]

  // Mapping from a word to possible successors
  var words: WordMap= Map[String, List[String]]()

  protected[misc] def build(wordList: List[String]): WordMap= {
    @tailrec def build(map: WordMap, list: List[String]): WordMap =
      list match {
        case Nil => map
        case first :: Nil =>
          val occ= map.getOrElse(first, List[String]())
          map + (first->occ)
        case first :: next :: _ =>
          val occ= map.getOrElse(first, List[String]())
          build(map + (first -> (next :: occ)), list.tail)
      }
    build(words, wordList)
  }

  private def readFiles(files: List[String]): List[String] =
    for {file <- files
         line <- IO.sourceLines(IO.fileAsStream(file))
         word <- line.split("\\s+")}
        yield word

  def buildFrom(files: List[String]): this.type = {
    words= build(readFiles(files))
    this
  }

  def buildFromList(wordList: List[String]): this.type = {
    words= build(wordList)
    this
  }

  def getStream: Stream[String] = {
    require(words.size > 0, "must load words")
    @tailrec def selectNext(from: List[String]): String= from match {
      case Nil => selectNext(words.keys.toList)
      case _ => randomFrom(from)
    }
    def getNext(word:String): Stream[String] = {
      val w= selectNext(words(word))
      w #:: getNext(w)
    }
    getNext(selectNext(List()))
  }

  override def formatOne[S>:String](v: S): String = s"$v"

  def mkString(n: Int): String =  get(n).mkString(" ")
}

object Markov {
  /** A list of files to read. */
  val inputFiles= List("markov.txt")

  def apply(): Markov = apply(inputFiles)
  def apply(fileList: List[String]): Markov = new Markov().buildFrom(fileList)

}