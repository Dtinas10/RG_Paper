package rgv4.frontend

import caos.frontend.Site.initSite
import rgv4.syntax.Program
import rgv4.syntax.Program.RxGr

/** Main function called by ScalaJS' compiled javascript when loading. */
object Main {
  def main(args: Array[String]):Unit =
    initSite[RxGr](CaosConfig)
}