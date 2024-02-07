package rgv4.syntax

import rgv4.syntax.Program.Edge.{HyperEdge, SimpleEdge}
import scala.annotation.tailrec
import scala.scalajs.js.`new`
import cats.instances.boolean

object Program:
  type State = String
  type Action = String
  type Weight = Double

  /** An edge can be simple (from state to state) or hyper (from a simlpe edge to any edge. */
  enum Edge:
    case SimpleEdge(from: State,
                    to: State,
                    action: Action,
                    weight: Weight = 0.0)
    case HyperEdge(from: Edge,
                   to: Edge,
                  //  action: Action,
                   weight: Weight = 0.0,
                   activate: Boolean)
    def pretty:String = this match
      case e:SimpleEdge =>
        s"${e.from}-${e.action}->${e.to}"
      case e:HyperEdge =>
        s"[${e.from.pretty}]->[${e.to.pretty}]"

    /** Returns the action label of any edge */
    def act: Action = this match
      case s:SimpleEdge => s.action
      case h:HyperEdge  => ""//h.action

  /** A reactive graph Rx has edges, an initial state, and a set of active edges. */
  case class RxGr(se:Map[State,Set[SimpleEdge]],// simple edges from st to outgoing edges
                  he:Map[Edge,Set[Edge]],       // hyperedges, from any edge to another edge (initially was from simple edge to any adge)
                  init: State,                  // initial state
                  active: Set[Edge]):           // set of active edges

    def toString2: String = s"$init${active.map(e=>s"\n${e.pretty}").mkString}"
    override def toString: String = s"$init${""}" 
    /** Auxiliary function to collect all states */
    def states:Set[State] = se.keySet + init
    /** Auxiliary function to collect all edges from a given simple edge */
    def getHe(e: Edge): Set[Edge] = he.getOrElse(e, Set())
    /** Auxiliary function to collect all edges from a given state */
    def getSe(st:State): Set[SimpleEdge] = se.getOrElse(st,Set())
    /** Returns the set of edges that exist from the initial state */
    def nextEdg: Set[SimpleEdge] = getSe(init)
    /** Returns a new RxGr with SimpleEdges only*/
    def getLevel0: RxGr =  
      val newactive = for case SimpleEdge(f,t,a,w) <- active yield SimpleEdge(f, t, a, w) 
      new RxGr(se,Map.empty,init,newactive)
      
    def empty: RxGr = RxGr(Map.empty, Map.empty,"", Set.empty)
    def isEmpty: Boolean = this == this.empty 

  /**
   * Evolves a reactive graph rxGr by performing a simple edge
   * @param rxGr the starting reactive graph
   * @param se the simple edge to be performed
   * @return None if `se` is not active or an inconsistency is found,
   *         or a new graph by updating the init state and the active edges,
   *         together with all action labels involved.
   */
  def step(rxGr: RxGr, se:SimpleEdge): Option[(RxGr,Set[Action])] =
    // stop if the edge is not active
    if !rxGr.active(se) then return None
    // collect edges involved
    val edges =
      collectEdges(rxGr,rxGr.getHe(se),Set(se))
    // collect actions
    val actions = edges.map(_.act)
    // update active edges
    val active = updateActive(rxGr,edges)
    active.map(a => (RxGr(rxGr.se,rxGr.he,se.to,a),actions))

  /** Calculates all edges that are triggered, avoiding loops. */
  @tailrec
  private def collectEdges(gr: RxGr,
                           missing: Set[Edge],
                           done: Set[Edge]): Set[Edge] =
    missing.headOption match
      case Some(e) =>
        if gr.active(e) && !done(e)
        then collectEdges(gr,missing ++ gr.getHe(e) - e,done+e)
        else collectEdges(gr,missing-e,done)
      case None => done

  /** Given a set of edges that are triggered, calculates a new set of
   * active edges, returning None if some edge is both activated and deactivated. */
  private def updateActive(gr: RxGr, es: Set[Edge]): Option[Set[Edge]] =
    val toActivate   = for case HyperEdge(_,to,_,true)  <-es yield to
    val toDeactivate = for case HyperEdge(_,to,_,false) <-es yield to
    if toActivate.intersect(toDeactivate).nonEmpty
    then None
    else Some(gr.active--toDeactivate++toActivate)

  def find(miss: Set[RxGr], know: Set[RxGr]): Boolean =
    if know.size > 100 then return false
    if miss.isEmpty then return false
    var st: RxGr = miss.head
    var newmiss = miss.tail
    val nextgr =  (for (i <- st.nextEdg) yield step(st,i)).flatten 
    if nextgr.isEmpty then return true
    for (i <- nextgr){
      if know.contains(i._1) then find(newmiss,know + st)
      else newmiss = newmiss + i._1 
    }
    find(newmiss,know + st)


  def find2(gr: RxGr, numit: Int, maxit: Int = 100): Boolean =
    if numit > maxit then return false
    var f: Boolean = false
    if gr.nextEdg.isEmpty then return true
    val nextgr: Set[RxGr] =  for i <- gr.nextEdg yield step(gr,i) match{
        case None => gr.empty
        case Some(value) => value._1
      }
    for (i <- nextgr){
      f = find2(i,numit +1, maxit)
    }
    f


    // if gr.getSe(st).isEmpty then return false
    // for (i <- gr.getSe(st)){
    //   var newgr = step(gr,i)
    //   if know.contains(newgr.init) then find(newgr,miss - st, know)
    //   know = know + newgr.init
    // } 

  case class System(main:RxGr, toCompare:Option[RxGr]):
    def apply(newMain:RxGr) = System(newMain,toCompare)
    override def toString: String = s"${main.init}${""}" 

