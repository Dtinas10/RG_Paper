package rgv4.frontend

import caos.frontend.Configurator.*
import caos.frontend.{Configurator, Documentation}
import caos.view.*
import rgv4.backend.*
import rgv4.syntax.Program.RxGr
import rgv4.syntax.Program.System
import rgv4.syntax.{Program, Show}
import rgv4.syntax.Parser 
import rgv4.frontend.Examples.*

/** Object used to configure which analysis appear in the browser */
object CaosConfig extends Configurator[System]:
  val name = "Animator of Multi-Action Switch Reactive Graphs"
  override val languageName: String = "Input program"

  /** Parser, converting a string into a System in rgv3 */
  // val parser = str => Parser.pp(Parser.rg,str).getOrElse(???)
  val parser = rgv4.syntax.Parser.parseProgram

  /** Examples of programs that the user can choose from. The first is the default one. */
  val examples = List(
    "Gabbay Example" -> Examples.gabbayExample -> "Figure 7.4 in Dov M Gabbay, Cognitive Technologies Reactive Kripke Semantics",
    // "Gabbay Example2" -> Example_GabbayExample2-> "Figure 7.9 of Dov M Gabbay, Cognitive Technologies Reactive Kripke Semantics",
    "Counter" ->  Examples.counter-> "Run 3 times only the action *act*",
    "Feature Model"->  Examples.featureModel -> "Fig 1 in Maxime Cordy et al. Model Checking Adaptive Software with Featured Transition Systems",
    "Vending Machine"->  Examples.vendingMachine -> "We have 1$ only to spend in the vending machine and we need to decide the best option between cofee, chocolate and apple.",
    //"Vending Machine 2"->  Examples.vendingMachine2 -> "We have 1$ only to spend in the vending machine and we need to decide the best option between cofee, chocolate and apple.",
    "Inconsistency" -> Examples.inconsistency -> "Example of Reactive Graph with an inconsistency.",
    "Example" -> Examples.exampleOfReport -> "Example of Report",
    "Ex1" -> Examples.ex1,
    "Bissim" -> Examples.bissimulation,
    )

  /** Description of the widgets that appear in the dashboard. */
  val widgets = List(
    "View pretty data" -> view[System](x => Show.toMermaid(x.main,""), Code("haskell")).moveTo(1),
    "Dead Locks" -> view[System](x => Program.find(Set(x.main),Set(),0).toString, Code("haskell")).moveTo(1),
    "Dead Locks 2" -> view[System](Program.findDeadlockPP(_), Text).moveTo(1),
    "Dead Locks 3" -> view[System](Program.findDeadlockTracePP(_), Text).moveTo(1),
    "Inconsistency" -> view[System](x => Program.findInconsitency(Set(x.main),Set(),0).toString, Code("haskell")).moveTo(1),
    // "My tests" -> view(x => x.toString, Text),
    // "Mermaid" -> view(x => x.toMermaid, Mermaid),
    "Global structure view" -> view(x =>Show.toMermaid(x.main,"GSV"), Mermaid),
    "Local structure view" -> view(x => Show.toMermaid(x.main.getLevel0,"LSV"), Mermaid),
    "Run semantics" -> steps(e=>e, Semantics, x => Show.toMermaid(x.main,"RS"), _.toString, Mermaid),
    "Run semantics2" -> steps(e=>e, Warnings, x => Show.toMermaid(x.main,"RS"), _.toString, Mermaid),
    // "Run semanticstext" -> steps(e=>e, Semantics, x => Show.toMermaid(x.main,"RS"), _.toString, Text),
    "Run semantics with local structure" -> steps(e=>e, Semantics, x => Show.toMermaid_twoGraphs(x.main,"RSLS"), _.toString, Mermaid),
    "Build LTS" -> lts(x=>x, Semantics, x=>x.main.init, _.toString),
    // "Build LTS (explore)" -> ltsExplore(e=>e, Semantics, x=>x.main.init, _.toString),
    "Two Reactive Graphs" -> view(x =>Show.toMermaid_twoGraphs_Bissi(x,"TG"), Mermaid),
    // "Build LTS" -> lts(x=>x, Semantics, x=>x.init, _.toString),
    // "Check" -> check(x=>Seq(x.toString)),
    // "Build LTS2" -> lts(x=>x, Semantics, x=>x.active.toString, _.toString),
    //  "Build LTS" -> lts((e:System)=>e, Semantics, Show.justTerm, _.toString).expand,
    //  "Build LTS (explore)" -> ltsExplore(e=>e, Semantics, x=>Show(x.main), _.toString),
    "Find strong bisimulation (given a program \"A ~ B\")" ->
      compareStrongBisim(Semantics, Semantics,
        (e: System) => System(e.main, None),
        (e: System) => System(e.toCompare.getOrElse(RxGr(Map.empty, Map.empty, " ", Set.empty)), None),
        Show.justTerm, Show.justTerm, _.toString),
  )

  //// Documentation below

  override val footer: String =
    """Simple animator of Multi Action Reactive Graphs, meant to exemplify the
      | CAOS libraries, used to generate this website.""".stripMargin 
      // Source code available online:
      // | <a target="_blank" href="https://github.com/arcalab/CAOS">
      // | https://github.com/arcalab/CAOS</a> (CAOS).""".stripMargin

  private val sosRules: String =    
    """ """.stripMargin

  override val documentation: Documentation = List(
    languageName -> "More information on the syntax of Reactive Graph" ->
    """|A program <code>RG</code> is a Reactive Graph with a syntax that follows the following template:
       |<pre>
       |init = Initial State;
       |l0 = {
       |    State from  --> State to by action, weigth, 
       |    };
       |ln = {
       |    (HE from, HE to, weigth, active, function),
       |    }
       |
       |</pre>
       |
       |where:
       |</p><code>init</code> is the initial state; </p>
       |</p><code>l0</code> is a set of level 0 edges (E); use <code>--></code> to represent an enabled edge and <code>-.-></code> a disable edge; </p>
       |</p><code>ln</code> is a set of hyper edges (GE); these can start and end in either E or another HE. 
       | An HE is defined recursively, i.e., both the "from" and the "to" fields can be another HE, or a simpler E in the base case;</p>
       |</p><code>action</code> is a string that labels an E; it can have only letters in lower or upper case,  digits, and the symbols <code>_</code>, <code><</code>, <code>></code>, <code>.</code>, <code>-</code>, <code>€</code>, and <code>$</code>; </p>
       |</p><code>weight</code> is a number; can be float or an integer; </p>
       |</p><code>funtion</code> is either <code>ON</code> or <code>OFF</code>; representing whether the HE enables or disables the target edge, respectively.</p>
       """.stripMargin,
    //"Build LTS" -> "More information on the operational rules used here" -> sosRules,
    //"Build LTS (explore)" -> "More information on the operational rules used here" -> sosRules,
    //"Run semantics" -> "More information on the operational rules used here" -> sosRules,
    "Find strong bisimulation (given a program \"A ~ B\")" -> "More information on this widget" ->
     ("<p>When the input consists of the comparison of 2 programs separated by <code>~</code>, this widget " +
       "searches for a (strong) bisimulation between these 2 programs, providing either a " +
       "concrete bisimulation or an explanation of where it failed.</p>" +
       "<p>When only a program is provided, it compares it against the empty process <code>0</code>.</p>"),
  )


