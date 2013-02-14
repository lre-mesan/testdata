package no.mesan.testdatagen.generators.norway

import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner

import no.mesan.testdatagen.Printer

@RunWith(classOf[JUnitRunner])
class KommunerSuite extends FunSuite with Printer {

  print(false) {
    println(Kommuner().get(120))
    println(Kommuner.kommunenr().get(120))
    println(Kommuner.kommunenavn().get(120))
  }

  test("negative get") {
    intercept[IllegalArgumentException] {
      Kommuner(false).get(-1)
    }
    intercept[IllegalArgumentException] {
      Kommuner(false).getStrings(-1)
    }
  }

  test("count") {
    assert(Kommuner(true).get(30).size === 30)
  }

  test("contents") {
    val res = Kommuner().sequential.get(300)
    for (s<-res) assert(s.matches("^[0-9][0-9][0-9][0-9] .+"))
  }

  test("kommunenr") {
    val res = Kommuner.kommunenr().get(300)
    for (s<-res) assert(s.matches("^[0-9][0-9][0-9][0-9]$"))
  }

}
