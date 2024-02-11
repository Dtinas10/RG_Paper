package rgv4.backend

import caos.sos.SOS
import rgv4.syntax.Program
import rgv4.syntax.Program.*
import rgv4.syntax.Program.RxGr.*
import rgv4.syntax.Program.Edge.*
import rgv4.syntax.Program.System

/** Small-step semantics for both commands and boolean+integer expressions.  */
// object Semantics extends SOS[String,RxGr]:
//   def next[A>:String](g : RxGr): Set[(A,RxGr)] = 
//     var s: Set[(A,RxGr)] = Set.empty
//     for (i<- g.nextEdg) {
//       var k = step(g,i)
//       if k != None then
//         // s = s ++ Set((k.map(_._1).get.init + ": " + i.action,k.map(_._1).get))
//         s = s ++ Set((i.action,k.map(_._1).get))
//         // s = s ++ Set((i.action,RxGr(Map.empty,Map.empty," ",Set.empty)))
//     }
//     s

val cross = '\u274C' 
val proibitionMark = '\u26D4' 
val exclamationMark  = '\u2757' 

object Semantics extends SOS[String,System]: 
  def next[A>:String](st : System): Set[(A,System)] =
    val g: RxGr = st.main 
    var s: Set[(A,System)] = Set.empty
    for (i<- g.nextEdg) {
      var k = step(g,i)
      if k != None then
        s = s ++ Set((i.action,System(k.map(_._1).get,st.toCompare)))
    }
    s
  
object Warnings extends SOS[String,System]:
  def next[A>:String](st : System): Set[(A,System)] =
    val g: RxGr = st.main 
    var s: Set[(A,System)] = Set.empty
    for (i<- g.nextEdg) {
      var k = step(g,i)
      if k != None then
        s = s ++ Set((i.action,System(k.map(_._1).get,st.toCompare)))
      else if (g.se.get(g.init).contains(i)) then //Problem because get function returns None or Some()
        s = s ++ Set((s"${exclamationMark}Warning$exclamationMark: $cross ${i.action}",System(g.empty,st.toCompare)))
      // else 
        // s = s ++ Set((g.se.get(g.init) match{ case Some(t) => t.toString},System(g.empty,st.toCompare)))

    }
    s


  
