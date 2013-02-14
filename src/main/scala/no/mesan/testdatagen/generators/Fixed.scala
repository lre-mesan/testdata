package no.mesan.testdatagen.generators

import no.mesan.testdatagen.Generator

/**
 * Generate the same value all time....
 * Special methods: from/to/unique -- not supported
 * Default limits: n/a
 */
object Fixed {
  def apply[T](v: T): Generator[T] = new FromList().fromList(List(v))
}