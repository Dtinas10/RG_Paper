package rgv4.frontend

import caos.frontend.Configurator.*
import caos.frontend.{Configurator, Documentation}
import caos.view.*
import rgv4.backend.*
import rgv4.syntax.Program.RxGr
import rgv4.syntax.{Program, Show}
import rgv4.syntax.Parser 


object Examples:

  val exampleOfReport: String = 
    """init = s0;
      |l0 = {
      |  s0 --> s1 by a,0,
      |  s1 -.-> s1 by b,0};
      |ln ={
      |  ((s0,s1,a,0),(s1,s1,b,0),0,Bullet,ON),
      |  ((s0,s1,a,0),((s0,s1,a,0),(s1,s1,b,0),0,Bullet,ON),0,Bullet,OFF),
      |  ((s1,s1,b,0),((s0,s1,a,0),((s0,s1,a,0),(s1,s1,b,0),0,Bullet,ON),0,Bullet,OFF),0,Circ,ON),
      |  ((s0,s1,a,0),((s1,s1,b,0),((s0,s1,a,0),((s0,s1,a,0),(s1,s1,b,0),0,Bullet,ON),0,Bullet,OFF),0,Circ,ON),0,Bullet,ON)}
    """.stripMargin

  val ex1:String = 
    """init = 0; 
      |l0={ 
      |	0 --> 1 by a,0,
      |  1 --> 2 by b,0,
      |  2 -.-> 0 by c,0};
      |ln = {
      |	((0,1,a,0), (1,2,b,0),0,Bullet,ON),
      |  (((0,1,a,0), (1,2,b,0),0,Bullet,ON), (2,0,c,0),0,Bullet,ON)}
    """.stripMargin

  val gabbayExample:String = 
  """init= Son_of_Tweetie;
    |l0={
    |  Son_of_Tweetie --> Special_Penguin by -,0,
    |  Special_Penguin --> Penguin by -,0,
    |  Penguin --> Bird by -,0,
    |  Bird --> Does_Fly by Fly,0};
    |ln={
    |  ((Penguin,Bird,-,0),(Bird,Does_Fly,Fly,0),0,Bullet,OFF),
    |  ((Special_Penguin,Penguin,-,0),((Penguin,Bird,-,0),(Bird,Does_Fly,Fly,0),0,Bullet,OFF),0,Bullet,OFF)}
  """.stripMargin

  val gabbayExample2:String = 
    """init = n1;
      |l0={
      |  n1 --> t1 by -,0,
      |  t1 --> c1 by -,0,
      |  c1 --> n1 by -,0,
      |  n2 --> t2 by -,0,
      |  t2 --> c2 by -,0,
      |  c2 --> n2 by -,0};
      |ln={
      |  ((c1,n1,-,0),(t2,c2,-,0),0,Bullet,ON),
      |  ((c2,n2,-,0),(t1,c1,-,0),0,Bullet,ON),
      |  ((t1,c1,-,0),(t2,c2,-,0),0,Bullet,ON),
      |  ((t2,c2,-,0),(t1,c1,-,0),0,Bullet,ON)}
    """ .stripMargin

  val counter:String = 
    """init = 0;
      |l0 = { 0 --> 0 by act,0 };
      |ln = {
      |  ((0,0,act,0),(0,0,act,0),0,Circ,OFF),
      |  ((0,0,act,0),((0,0,act,0),(0,0,act,0),0,Circ,OFF),0, Circ,ON),
      |  ((0,0,act,0),((0,0,act,0),((0,0,act,0),(0,0,act,0),0,Circ,OFF),0, Circ,ON),0,Bullet,ON)}
    """.stripMargin
  
  val featureModel:String =
    """init = setup;
      |l0 = {
      |  setup --> setup by safe,0,   
      |  setup --> setup by unsafe,0,
      |  setup --> setup by encrypt,0,     
      |  setup --> setup by dencrypt,0,
      |  setup --> ready by -,0,
      |  ready --> setup by -,0,
      |  ready --> received by receive,0,
      |  received --> routed-safe by route,0,
      |  received --> routed-unsafe by route,0,
      |  routed-safe --> sent by send,0,
      |  routed-unsafe --> sent by send,0,
      |  routed-unsafe --> sent-encrypt by send,0,
      |  sent-encrypt --> ready by ready,0,
      |  sent --> ready by ready,0 };
      |
      |ln = {
      |  ((setup,setup,safe,0),(received,routed-safe,route,0),0, Bullet, ON),
      |  ((setup,setup,safe,0),(received,routed-unsafe,route,0),0, Bullet, OFF),
      |  ((setup,setup,unsafe,0),(received,routed-safe,route,0),0, Bullet, OFF),
      |  ((setup,setup,unsafe,0),(received,routed-unsafe,route,0),0, Bullet, ON),
      |  ((setup,setup,encrypt,0),(routed-unsafe,sent,send,0),0, Bullet, OFF),
      |  ((setup,setup,encrypt,0),(routed-unsafe,sent-encrypt,send,0),0, Bullet, ON),
      |  ((setup,setup,dencrypt,0),(routed-unsafe,sent,send,0),0, Bullet, ON),
      |  ((setup,setup,dencrypt,0),(routed-unsafe,sent-encrypt,send,0),0, Bullet, OFF)}
    """.stripMargin

  val vendingMachine: String = 
    """init = Insert;
      |l0 = {
      |  Insert --> Cofee by 0.5$,0,
      |  Insert --> Apple by 0.5$,0,
      |  Insert --> Chocolate by 1$,0,
      |  Cofee --> Insert by Get_cofee,0,
      |  Apple --> Insert by Get_apple,0,
      |  Chocolate --> Insert by Get_choc,0};
      |ln = {
      |  ((Insert,Chocolate,1$,0),(Insert,Cofee,0.5$,0),0,Bullet,OFF),
      |  ((Insert,Chocolate,1$,0),(Insert,Apple,0.5$,0),0,Bullet,OFF),
      |  ((Insert,Chocolate,1$,0),(Insert,Chocolate,1$,0),0,Bullet,OFF),
      |  ((Insert,Cofee,0.5$,0),(Insert,Cofee,0.5$,0),0,Circ,OFF),
      |  ((Insert,Cofee,0.5$,0),(Insert,Chocolate,1$,0),0,Bullet,OFF),
      |  ((Insert,Cofee,0.5$,0),((Insert,Cofee,0.5$,0),(Insert,Cofee,0.5$,0),0,Circ,OFF),0,Bullet,ON),
      |  ((Insert,Cofee,0.5$,0),((Insert,Apple,0.5$,0),(Insert,Apple,0.5$,0),0,Circ,OFF),0,Bullet,ON),
      |  ((Insert,Apple,0.5$,0),(Insert,Apple,0.5$,0),0,Circ,OFF),
      |  ((Insert,Apple,0.5$,0),(Insert,Chocolate,1$,0),0,Bullet,OFF),
      |  ((Insert,Apple,0.5$,0),((Insert,Apple,0.5$,0),(Insert,Apple,0.5$,0),0,Circ,OFF),0,Bullet,ON),
      |  ((Insert,Apple,0.5$,0),((Insert,Cofee,0.5$,0),(Insert,Cofee,0.5$,0),0,Circ,OFF),0,Bullet,ON),
      |  ((Insert,Apple,0.5$,0),(Insert,Cofee,0.5$,0),0,Circ,OFF),
      |  ((Insert,Cofee,0.5$,0),(Insert,Apple,0.5$,0),0,Circ,OFF),
      |  ((Insert,Apple,0.5$,0),((Insert,Apple,0.5$,0),(Insert,Cofee,0.5$,0),0,Circ,OFF),0,Bullet,ON),
      |  ((Insert,Cofee,0.5$,0),((Insert,Apple,0.5$,0),(Insert,Cofee,0.5$,0),0,Circ,OFF),0,Bullet,ON),
      |  ((Insert,Apple,0.5$,0),((Insert,Cofee,0.5$,0),(Insert,Apple,0.5$,0),0,Circ,OFF),0,Bullet,ON),
      |  ((Insert,Cofee,0.5$,0),((Insert,Cofee,0.5$,0),(Insert,Apple,0.5$,0),0,Circ,OFF),0,Bullet,ON)}
    """.stripMargin

  val vendingMachine2: String = 
    """init = Insert;
      |l0 = {
      |  Insert --> Cofee by 0.5$,0,
      |  Insert --> Chips by 1$,0,
      |  Insert --> Chocolate by 1$,0,
      |  Cofee --> Insert by Get_cofee,0,
      |  Chips --> Insert by Get_apple,0,
      |  Chocolate --> Insert by Get_choc,0};
      |ln = {
      |  ((Insert,Chocolate,1$,0),(Insert,Cofee,0.5$,0),0,Bullet,OFF),
      |  ((Insert,Chocolate,1$,0),(Insert,Chips,1$,0),0,Bullet,OFF),
      |  ((Insert,Chocolate,1$,0),(Insert,Chocolate,1$,0),0,Bullet,OFF),
      |  ((Insert,Cofee,0.5$,0),(Insert,Cofee,0.5$,0),0,Circ,OFF),
      |  ((Insert,Cofee,0.5$,0),(Insert,Chocolate,1$,0),0,Bullet,OFF),
      |  ((Insert,Cofee,0.5$,0),(Insert,Chips,1$,0),0,Bullet,OFF),
      |  ((Insert,Cofee,0.5$,0),((Insert,Cofee,0.5$,0),(Insert,Cofee,0.5$,0),0,Circ,OFF),0,Bullet,ON),
      |  ((Insert,Chips,1$,0),(Insert,Cofee,0.5$,0),0,Bullet,OFF),
      |  ((Insert,Chips,1$,0),(Insert,Chips,1$,0),0,Bullet,OFF),
      |  ((Insert,Chips,1$,0),(Insert,Chocolate,1$,0),0,Bullet,OFF)}
    """.stripMargin

  val inconsistency: String =
    """init = s;
    |l0 = {
    |  s --> s0 by a,1,
    |  s0 --> s1 by a,0,
    |  s1 -.-> s2 by b,0};
    |ln ={
    |  ((s0,s1,a,0),(s1,s2,b,0),0,Bullet,ON),
    |  ((s0,s1,a,0),(s1,s2,b,0),0,Bullet,OFF)}
    """.stripMargin
    
// init = 0; 
// l0={ 
// 	0 --> 1 by a,0,
//   1 --> 2 by b,0,
//   2 -.-> 0 by c,0};
// ln = {
// 	((0,1,a,0), (1,2,b,0),0,Bullet,ON),
//   (((0,1,a,0), (1,2,b,0),0,Bullet,ON), (2,0,c,0),0,Bullet,ON),
//   ((((0,1,a,0), (1,2,b,0),0,Bullet,ON), (2,0,c,0),0,Bullet,ON),(1,2,b,0),0,Bullet,OFF)}
    