package net.kalars.testgen.recordgen

import org.junit.runner.RunWith
import net.kalars.testgen.FunSuite
import org.scalatest.junit.JUnitRunner

import net.kalars.testgen.aggreg.SomeNulls
import net.kalars.testgen.generators.{Booleans, Chars, Dates, Doubles, Ints, ListGenerator, Strings}
import net.kalars.testgen.generators.misc.{MailGenerator, NameGenerator, UrlGenerator}
import net.kalars.testgen.generators.norway.{FnrGenerator, KjennemerkeGenerator}

@RunWith(classOf[JUnitRunner])
class SqlGeneratorSuite extends FunSuite {
  val dates = Dates().from(y = 1950).to(y = 2012).get(1000)

  trait Setup {
    val idGen = Ints().from(1).sequential
    val codeGen = Strings().chars('A' to 'Z').length(4)
    val nameGen = NameGenerator(2)
    val bornGen = SomeNulls(4, ListGenerator(dates).sequential.formatWith(Dates.dateFormatter("yyyy-MM-dd")))
    val fnrGen = FnrGenerator(ListGenerator(dates).sequential)
    val boolGen = Booleans()
    val scoreGen = SomeNulls(2, Doubles().from(0).to(10000))
    val urlGen = SomeNulls(3, UrlGenerator())
    val mailGen = SomeNulls(5, MailGenerator())
    var kjmGen = SomeNulls(6, KjennemerkeGenerator())
    val recordGen = SqlGenerator("User").
      add("id", idGen).
      addQuoted("userId", codeGen).
      addQuoted("ssn", fnrGen).
      add("born", bornGen).
      addQuoted("name", nameGen).
      addQuoted("mail", mailGen).
      addQuoted("homePage", urlGen).
      add("active", boolGen).
      add("score", scoreGen).
      addQuoted("car", kjmGen)
  }

  print {
    new Setup {
      println(recordGen.get(4).mkString("\n"))
    }
  }

  test("needs one generator") {
    intercept[IllegalArgumentException] {
      SqlGenerator("tab", "go").get(1)
    }
  }

  test("negative get") {
    intercept[IllegalArgumentException] {
      new Setup {
        recordGen.get(-1)
      }
    }
    intercept[IllegalArgumentException] {
      new Setup {
        recordGen.getStrings(-1)
      }
    }
  }

  test("count") {
    new Setup {
      assert(recordGen.get(30).size === 30)
    }
  }

  test("contents") {
    new Setup {
      val res=recordGen.get(30).mkString("\n")
      assert(res.matches("(?s)^(insert into .* values .*)+"))
    }
  }

  test("quoting") {
    new Setup {
      var fnuttGen = Chars("'")
      val res = SqlGenerator("tbl", "").addQuoted("fnutt", fnuttGen).getStrings(1)(0)
      val exp= "insert into tbl (fnutt) values ('\\'')"
      assert(res===exp,res +"=" + exp)
    }
  }
}
