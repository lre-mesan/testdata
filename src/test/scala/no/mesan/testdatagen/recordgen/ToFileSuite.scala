package no.mesan.testdatagen.recordgen

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

import org.scalatest.FunSuite
import no.mesan.testdatagen.generators.{Dates, FromList, Ints}
import no.mesan.testdatagen.generators.norway.{Fnr, NorskeNavn}

import no.mesan.testdatagen.Printer

@RunWith(classOf[JUnitRunner])
class ToFileSuite extends FunSuite with Printer {
  val dates = Dates().from(y = 1950).to(y = 2012).get(1000)

  trait Setup {
    val idGen = Ints().from(1).sequential
    val nameGen = NorskeNavn()
    val fnrGen = Fnr(FromList(dates).sequential)
    val recordGen = ToCsv(true).
      add("id", idGen).
      add("name", nameGen).
      add("fnr", fnrGen)
    val fName= "target/test.txt"
    val gen= recordGen.toFile(fName)
  }

  test("negative get") {
    intercept[IllegalArgumentException] {
      new Setup {
        gen.get(-1)
      }
    }
    intercept[IllegalArgumentException] {
      new Setup {
        gen.getStrings(-1)
      }
    }
  }

  test("needs one generator") {
    intercept[IllegalArgumentException] {
      ToCsv(true).toFile("test.txt").get(1)
    }
  }

  print(false) {
    new Setup {
      gen.get(100)
      recordGen.appendToFile(fName).get(100)
    }
  }
}
