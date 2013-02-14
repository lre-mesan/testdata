package no.mesan.testdatagen.generators.misc

import no.mesan.testdatagen.Generator
import no.mesan.testdatagen.aggreg.FieldConcatenator
import no.mesan.testdatagen.generators.Strings

/**
 * Generate name-like strings...
 * Default limits: name length from 3 to 18, first letter upper case
 */
object Names {
  private val pfxChars= "ABDEFGHIJKLMNOPRSTUVY"*12 +
                "CQWXZ"
  private val sfxChars= "abdefghijklmnoprstuvy" * 20 +
                "aeiouy" * 5
                "cqwxz" * 3 +
                "-'"
  def apply(n: Int): Generator[String] = {
    val gen= new FieldConcatenator()
    for ( i<- 0 to n-1) {
      if (i>0) gen.add(Strings().length(1).chars(" "))
      gen.add(Strings().length(1).chars(pfxChars))
      gen.add(Strings().lengthBetween(2, 19).chars(sfxChars))
    }
    gen
  }
}