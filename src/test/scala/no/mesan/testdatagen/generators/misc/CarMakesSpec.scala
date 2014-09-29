package no.mesan.testdatagen.generators.misc

import no.mesan.testdatagen.ExtendedGenerator
import org.junit.runner.RunWith
import org.scalatest.FlatSpec
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class CarMakesSpec extends FlatSpec  {

  "CarMakes" should "not support from/to" in {
    val l: ExtendedGenerator[String] = CarMakes()
    intercept[UnsupportedOperationException] {
      l.from("a").get(1)
    }
    intercept[UnsupportedOperationException] {
      l.to("<").get(1)
    }
  }

  it should "support filtering" in {
    val gen = CarMakes().filter(s => s startsWith "A")
    val res = gen.get(100)
    assert(res.forall(s => List("Alfa Romeo", "Aston Martin", "Atlas", "Audi", "Austin",
     "Autobianchi") contains s))
  }
}