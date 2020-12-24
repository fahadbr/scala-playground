package example

import org.scalatest.matchers.should.Matchers
import org.scalatest.funspec.AnyFunSpec

class HelloSpec extends AnyFunSpec with Matchers {
  describe("Calcite") {
    it("should print stuff") {
      Example.run()
    }
  }
}
