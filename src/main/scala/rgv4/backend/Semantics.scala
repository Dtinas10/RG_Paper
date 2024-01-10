package rgv4.backend

import caos.sos.SOS
// import rgv3.backend.Semantics.RxGr
import rgv4.syntax.Program
import rgv4.syntax.Program.*
import rgv4.syntax.Program.RxGr.*
import rgv4.syntax.Program.Edge.*

/** Small-step semantics for both commands and boolean+integer expressions.  */
object Semantics extends SOS[String,RxGr]:
  def next[A>:String](g : RxGr): Set[(A,RxGr)] = 
    var s: Set[(A,RxGr)] = Set.empty
    for (i<- g.nextEdg) {
      var k = step(g,i)
      if k != None then
        // s = s ++ Set((k.map(_._1).get.init + ": " + i.action,k.map(_._1).get))
        s = s ++ Set((i.action,k.map(_._1).get))
        // s = s ++ Set((i.action,RxGr(Map.empty,Map.empty," ",Set.empty)))
    }
    s
  
  
