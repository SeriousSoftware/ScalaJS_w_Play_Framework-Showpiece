package example
package test

import scala.scalajs.js.Dynamic.{global => g}
//import scala.scalajs.test.JasmineTest
import utest._

object ScalaJSExampleTest extends TestSuite {
  import ExampleJS._

  def tests = TestSuite {
    'ScalaJSExample {
      assert(square(0) == 0)
      assert(square(4) == 16)
      assert(square(-5) == 25)
    }
  }
}
