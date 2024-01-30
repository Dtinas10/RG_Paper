package rgv4.syntax

import rgv4.syntax.Program.*
import rgv4.syntax.Program.RxGr.*
import rgv4.syntax.Program.Edge.*


/**
 * List of functions to produce textual representations of commands
 */
object Show:


  /** Put the reactive graph RxGr in Mermaid Code*/
  def toMermaid(g: RxGr,id:String): String = _toMermaid(g,"flowchart LR \n","",id)

  /** Put the RG in Mermaid Code + RG with level0 only in Mermaid Code*/ 
  def toMermaid_twoGraphs(g:RxGr,id:String): String = 
    _toMermaid(g, "flowchart LR \n subgraph Global View  \n direction LR \n", "",id)  
    + "\n end \n subgraph Local View \n direction LR \n" 
    + _toMermaid(g.getLevel0,"\n",".",id) + "\n end"
  
  /* put the RA in mermaid Code wich  received 3 arguments:
    g:RxGr -> is the RA
    sInital:String ->  is the string two begin a mermaid
    s2:String -> is the string to change name' nodes  for level0 only
    id:String -> is the id to identify nodes in widget*/
  private def _toMermaid(g: RxGr, sInitial: String, s2:String, id:String): String = {
    val colors: List[String] = List("gold", "red","blue","gray","orange","pink","green","purple") //miss and black
    // var mermaid = "```mermaid \nflowchart LR \n"
    var mermaid = sInitial //"flowchart LR \n"
    
    //Counter to put style in edges
    var numLinhas: Int = 0

    // Loops to draw edges level0
    for ((from, edge) <- g.se){
      for (e <- edge){
        if haveMiddle(e,g) then{
          if g.active(e) then
            mermaid = mermaid + e.from + id + "(" + e.from + ") ---"+  n(e) + id + "( ) --> |" + e.act + "|"+ e.to + id + "(" + e.to + ") \n"
          else
            mermaid = mermaid + e.from + id + "(" + e.from + ") -.-"+  n(e) + id + "( ) -.-> |" + e.act + "|"+ e.to + id + "(" + e.to + ") \n"
          mermaid = mermaid + "style " + n(e) + id + " width:0px \n"
          mermaid = mermaid + "linkStyle " + numLinhas +" stroke:black"+", stroke-width:2px \n"
          mermaid = mermaid + "linkStyle " + (numLinhas + 1) +" stroke:black"+", stroke-width:2px \n"
          numLinhas = numLinhas + 2
        }
        else{
          if g.active(e) then
            mermaid = mermaid + s2 + e.from + id +"(" + e.from + ") --> |" + e.act + "|" + s2 + e.to + id +"(" + e.to + ") \n"
          else
            mermaid = mermaid + s2 + e.from + id + "(" + e.from + ") -.-> |" + e.act + "|"+ s2 + e.to + id +"(" + e.to + ") \n"
          mermaid = mermaid + "linkStyle " + numLinhas + " stroke:black"+", stroke-width:2px \n"
          numLinhas = numLinhas + 1
        }
      }
    }

    // Loops to draw edges levelN
    for ((from, edge) <- g.he){
      for (ee <- edge){
        ee match {
          case e: SimpleEdge => mermaid = mermaid + " "
          case e: HyperEdge =>
            if haveMiddle(e,g) then{
              if g.active(e) then
                mermaid = mermaid + n(e.from) + id +"( ) ---" + n(e) + id +"( ) --"+ head(e.activate) + n(e.to) + id +"( ) \n"
              else
                mermaid = mermaid + n(e.from) + id +"( ) -.-" + n(e) + id +"( ) -.-"+ head(e.activate)+ n(e.to) + id +"( ) \n"

              mermaid = mermaid + "style " + n(e) + id +" width:0px \n"
              if (order(e.from) < order(e.to)) then{
                mermaid = mermaid + "linkStyle " + numLinhas +" stroke:dark"+ colors(order(e))+", stroke-width:2px \n"
                mermaid = mermaid + "linkStyle " + (numLinhas + 1) +" stroke:dark"+ colors(order(e))+", stroke-width:2px \n"
              }else{
                mermaid = mermaid + "linkStyle " + numLinhas +" stroke:"+ colors(order(e))+", stroke-width:2px \n"
                mermaid = mermaid + "linkStyle " + (numLinhas + 1) +" stroke:"+ colors(order(e))+", stroke-width:2px \n"
              } 
              numLinhas = numLinhas + 2

            }else{

              if g.active(e) then
                mermaid = mermaid + n(e.from) + id +"( ) --"+ head(e.activate) + n(e.to) + id +"( ) \n"
              else
                mermaid = mermaid + n(e.from) + id +"( ) -.-"+ head(e.activate)+ n(e.to) + id +"( ) \n"

              if (order(e.from) < order(e.to)) then{
                mermaid = mermaid + "linkStyle " + numLinhas +" stroke:"+ colors(order(e))+", stroke-width:3px \n"
              }else{
                mermaid = mermaid + "linkStyle " + numLinhas +" stroke:dark"+ colors(order(e))+", stroke-width:2px \n"
              } 
              numLinhas = numLinhas + 1
            }
        }
      }
    }

    mermaid +=  "style " + g.init + id + " fill:#8f7,stroke:#363,stroke-width:4px \n" //+"```"
    mermaid +=  "style " + s2 + g.init + id + " fill:#8f7,stroke:#363,stroke-width:4px \n" //+"```"
    mermaid
  }

  /* function to decide the head arrow (ON, OFF)*/
  private def head(b: Boolean):String ={
    if b then ">"
    else "x" 
  }

  /* function to get the name in the middle of the edge
  Because there is some edges wich start in the middle*/
  private def n(edge: Edge): String = {
    var m: String = ""
    edge match
      case e: SimpleEdge => m = m + e.from + e.to + e.act
      case he: HyperEdge => m = n(he.from) + n(he.to)
    m
  }

  /*function to find the order of edge, it's important to choose the color in Mermaid code */
  private def order(edge: Edge): Int = {
    var n: Int = 1
    edge match
      case e: SimpleEdge => n = 0
      case he: HyperEdge => n = n + order(he.to) + order(he.from)
    n
  }

  /*function wich give true if the edege needs midle*/ 
  private def haveMiddle(e: Edge, g: RxGr):Boolean = 
    g.he.contains(e) || g.he.exists{ 
      case (_, s) => 
        var f: Boolean   = false
        for (edge <- s){ 
          edge match{
            case ee: SimpleEdge => if (ee == e) then f = true  
            case ee: HyperEdge => if (ee.to == e) then f = true
          }
        }
        f
      }

  




