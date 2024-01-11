package rgv4.frontend

import caos.frontend.Configurator.*
import caos.frontend.{Configurator, Documentation}
import caos.view.*
import rgv4.backend.*
import rgv4.syntax.Program.RxGr
import rgv4.syntax.{Program, Show}
import rgv4.syntax.Parser 
import rgv4.frontend.Examples.*

/** Object used to configure which analysis appear in the browser */
object CaosConfig extends Configurator[RxGr]:
  val name = "Animator of Multi-Action Switch Reactive Graphs"
  override val languageName: String = "Input program"

  /** Parser, converting a string into a System in rgv3 */
  // val parser = str => Parser.pp(Parser.rg,str).getOrElse(???)
  val parser = rgv4.syntax.Parser.parseProgram

  /** Examples of programs that the user can choose from. The first is the default one. */
  val examples = List(
    "Example" -> Examples.exampleOfReport-> "Example of Report",
    "Ex1" -> Examples.ex1,
    "Gabbay Example" -> Examples.gabbayExample -> "Figure 7.4 of Dov M Gabbay, Cognitive Technologies Reactive Kripke Semantics",
    // "Gabbay Example2" -> Example_GabbayExample2-> "Figure 7.9 of Dov M Gabbay, Cognitive Technologies Reactive Kripke Semantics",
    "Counter" ->  Examples.counter-> "Run 3 times only the action *act*",
    "Future Model"->  Examples.futureModel -> "Fig 1 in Maxime Cordy et al. Model Checking Adaptive Software with Featured Transition Systems",
    "Multi Actions"->  Examples.multiActions -> "Vendig Machine",
  )

  /** Description of the widgets that appear in the dashboard. */
  val widgets = List(
    "View pretty data" -> view[RxGr](Show.toMermaid, Code("haskell")).moveTo(1),
    // "My tests" -> view(x => x.toString, Text),
    // "Mermaid" -> view(x => x.toMermaid, Mermaid),
    "View structure" -> view(Show.toMermaid, Mermaid),
    "Structure with level 0 only" -> view(x => Show.toMermaid(x.getLevel0), Mermaid),
    "Run semantics" -> steps(e=>e, Semantics, Show.toMermaid, _.toString, Mermaid),
    "Run semantics Level 0" -> steps(e=>e, Semantics, x => Show.toMermaid_twoGraphs(x), _.toString, Mermaid),
    "Build LTS" -> lts(x=>x, Semantics, x=>x.init, _.toString),
    // "Build LTS2" -> lts(x=>x, Semantics, x=>x.active.toString, _.toString),
    //  "Build LTS" -> lts((e:System)=>e, Semantics, Show.justTerm, _.toString).expand,
    //  "Build LTS (explore)" -> ltsExplore(e=>e, Semantics, x=>Show(x.main), _.toString),
    // "Find strong bisimulation (given a program \"A ~ B\")" ->
      // compareStrongBisim(Semantics, Semantics,
        // (e: System) => System(e.defs, e.main, None),
        // (e: System) => System(e.defs, e.toCompare.getOrElse(Program.Term.End), None),
        // Show.justTerm, Show.justTerm, _.toString),
  )

  //// Documentation below

  override val footer: String =
    """Simple animator of Reactive Graphs, meant to exemplify the
      | CAOS libraries, used to generate this website. Source code available online:
      | <a target="_blank" href="https://github.com/arcalab/CAOS">
      | https://github.com/arcalab/CAOS</a> (CAOS).""".stripMargin

  private val sosRules: String =    
    """ """.stripMargin

  override val documentation: Documentation = List(
    languageName -> "More information on the syntax of Reactive Graph" ->
    """A program <code>RG</code> in Reactive Graph is given by the following grammar:
       <pre>
       |  init = Initial State
       |  l0 = {
       |      (State from, State to, action, weigth, active) Simple Edge
       |      }
       |  ln = {
       |      (SE from, HE to, weigth, active, function) Hyper Edge
       |      }
       |
       |
       </pre>
       
       """.stripMargin,
    "Build LTS" -> "More information on the operational rules used here" -> sosRules,
    "Build LTS (explore)" -> "More information on the operational rules used here" -> sosRules,
    "Run semantics" -> "More information on the operational rules used here" -> sosRules,
    "Find strong bisimulation (given a program \"A ~ B\")" -> "More information on this widget" ->
     ("<p>When the input consists of the comparison of 2 programs separated by <code>~</code>, this widget " +
       "searches for a (strong) bisimulation between these 2 programs, providing either a " +
       "concrete bisimulation or an explanation of where it failed.</p>" +
       "<p>When only a program is provided, it compares it against the empty process <code>0</code>.</p>"),
  )


