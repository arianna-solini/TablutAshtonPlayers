# TablutAshtonPlayers
A player for the Tablut Students Competition - UNIBO2019 - AIofdTiger team

```
                           .e@$$$$$$$eeeu=~=._
                        zd$$$$$$$$$$$$$$'   z$b..  _,x,
                     z????$$$$$$$$P",-==   dP""`$$$.  .;
                  .e"..     `"?$Fu^'e$$$$$be.   )$$' 4r'$$nn,     _. -
                ud$$$P  zeeeu.   ld$$$$$$$$$$$$c`=._,$$  <"$$$$= '
              z$$$$$$e ?$$"   "FE=e2R$$$$$$$$$$$$bu"h._..d. "`nMb`    _
           .-J$$$$$$$$,?$$b,.,J",xc3$$$$$$$$$$$$$$$" Mh. "  'MMMMP- ~
      ..ze$F R$$$$$$$$$bcccd$$"J(<!i`"?$$$$$$$$$$$"i'MMP .-=^,xn'.T
z$$$$$$$$$4.  ,ce$$$$$$$$$$$P'dMM `!!!i;,.`"""""`,i  MMx?xr?PTTT%'r
$"..z .x"F"?$$$$$$$$$$$$$$$F.MMMMM.`!!''"!!!!!!! !! xMMMMMn PTM>T-
"b".nMMP'b.   "",c$$$$$$$$P.MMMMMMMb.<       )!! >'dMMMMMMM  __
 .HMMMM  "??$$$$$PF"uPF",, umnmnHMMMMMbx.... ''.n'MMMMMCund~    `~ ~ -
'M'HMMMh         .e$ee$$?7 MMMMMT"",MMMMMMMMMMMMM.MMMMMMMMM
  MMMMMMMx -...e$$$P??,nMM "`,nndMMMMMMMMMMMMMMMMk`MMMMMMMP
 H"MMMMMMMMhx???",nHMMP",nh MMMMMM?MP"xHMMMMMMMMMF-?TMMMM"
   MMPTMMMMMMMMMMP"u- :n.`"%'MF.xnF.nMMMMMMMMMP".::::."Te
  'M".MPTMMMMMP"zeEeP.MMMMM"..4MF,HMMMMMMMMMF'.::::::::`R.
   " MF dMMMf z$$$$$ MMMF'xMMMr`HMMMMMMMMM".:::::::.::...?_
     T  M P  $$$$$$%dMF'dMMMM"xk'MPJMMMMP ::'.'';i!!!!'`^.xhMmx
       'M   4$$$$$P.P,HMMMMM,HMMh TMMMMM> :!!!!!!!'`.xnMMMMMMMMMn
            J$$$$$ ",MMMMMM,MMMM'h TMMMML'!;!!!`.nHMMMMMMMMMMMMMMMn
            $$$$$P HMMMMMM,MMMM MMM."MMMM \!i`.HMMMMMMMMMMMMMMMMMMMMr
            $$$$$"dMMMMMMMMMMMfdMMMM.`MMMh   xMMMMMMMMMMMMMMMMMMMMMMM
           4$$$$$ MMMMMMMMMMMMMMMMMMMx`MMMMMMMMMMMMMMMMMMMMMMMMMMMMMM'
          ,4$$$$$.MMMMMMMMMMMMMMMMMMMMh TMMMMMMMMMMMMMMMMMMMMMMMMMMMf
         ; J$$$$F;MMMMMMMMMMMMMMMMMMMMMM."MMMMMMMMMMMMMMMMMMMMMMP""
       .db $$$$$ MMMMMMMMMMMMMMMMMMMMMMMMh."MMMMMMMMMMMMMMPF"
      , d$b$$$$$ MMMMMMMMMMMMMMMMMMMMMMMMMM u"?f""?=
     .  $$$$$$$$ MMMMMMMMMMMMMMMMMMMMMMMMMfJ$b
    z$  d$$$$$$$ MMMMMMMMMMMMMMMMMMMMMMMMM P" `
   e$$h ?$$$$$$$ MMMMMMMMMMMMMMMMMMMMMMMMf^  zF`.
```

## Installation on Ubuntu/Debian
Open a terminal and type the following to install JDK8 and ANT:
```
sudo apt update
sudo apt install openjdk-8-jdk -y
sudo apt install ant -y
```
Then clone the repository with:

```
git clone https://github.com/arianna-solini/TablutAshtonPlayers.git
```
## Run the program
If you use ant, first compile the project:
```
cd /path/to/project/TablutAshtonPlayers/Tablut

ant clean

ant compile
```
then:
```
ant -Darg0=aiofdtiger -Darg1=(white|black)
```
