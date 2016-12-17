package example

import org.scalajs.dom

import scala.scalajs.js

object ScalaJSExample extends js.JSApp {
  def main(): Unit = {
    dom.document.getElementById("scalajsShoutOut").textContent = "it works scala js"
  }
}
