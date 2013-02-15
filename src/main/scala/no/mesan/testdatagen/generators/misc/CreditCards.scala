package no.mesan.testdatagen.generators.misc

import no.mesan.testdatagen.Generator
import no.mesan.testdatagen.aggreg.{FieldConcatenator, WeightedGenerator}
import no.mesan.testdatagen.generators.{Fixed, FromList, Strings}
import no.mesan.testdatagen.GeneratorImpl
import no.mesan.testdatagen.SingleGenerator
import scala.annotation.tailrec
import no.mesan.testdatagen.generators.Ints

/**
 * Generate credit card numbers.
 */
class CreditCards (prefixes: List[Long]) extends GeneratorImpl[Long] {

  protected var isUnique= false
  /** Generate unique, random values. */
  def unique: this.type= { isUnique=true; this }

  private def digits(n:Long) = n.toString.map { _.toString.toInt }
  private def sum(s: Seq[Int]) = s.foldLeft(0)(_+_)
  private def flip(n:Int) = if (n==2) 1 else 2
  private def forAll(accum: Int, factor:Int, d:List[Int]): Int = d match {
     case Nil => accum
     case _ => forAll(accum + sum(digits(factor*d.head)), flip(factor), d.tail)
  }
  private def checkDigit(cardNo: Long)= {
    val chk= forAll(0, 2, digits(cardNo/10L).reverse.toList)%10
    if (chk==0) 0 else 10-chk
  }
  // private def isValid(cardNo: Long)= checkDigit(cardNo) == cardNo%10

  override def get(n:Int) = {
    require(n>=0, "cannot get negative count")
    val pfxGen= FromList(prefixes)
    val ints= Ints() from(0) to (9)
    @tailrec
    def getRandomly(soFar: List[Long]): List[Long]=
    if (soFar.length>=n) soFar
    else {
      val pfx: Long= pfxGen.get(1)(0)
      val allInts= ints.get(16-(pfx.toString.length)) // up to 15 more digits
      val sample= allInts.foldLeft(pfx)(10L*_ + _)
      val nxt= (sample/10L)*10L + checkDigit(sample)
      if (filterAll(nxt) && (!isUnique || !(soFar contains nxt)))
        getRandomly(nxt::soFar)
      else getRandomly(soFar)
    }
    getRandomly(Nil)
  }
}

object CreditCards {
  private val masterCard= (51L to 55L).toList
  private val visa= (40L to 49L).toList
  def apply(): CreditCards = new CreditCards(masterCard ::: visa)
  def visas : CreditCards = new CreditCards(visa)
  def masterCards: CreditCards= new CreditCards(masterCard)
}