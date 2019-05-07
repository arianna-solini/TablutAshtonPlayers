# TablutAshtonPlayers
A player for the Tablut Students Competition - UNIBO2019

```
           .,;%%,                                   .,%%;
         ;%%%%%%%%,     ...%%%%%::::::::::::::..   ,%%% %;
       ;%%% %%%%%%% ..:::%%%%:::::::@@@@@@@@@@@@aa.%% %%%%
       %%%%%% %%%%%:::::%%%::::@@@@@@@@@@@@@@@@@@@@@.%%%%%
       %%%%%%% %%%%:::%%%::::@@@@@@@@@   @@@@@   @@@@@.%%
        %%%%%%%%%%%::%%::::@@@@@@@@@@ @@@ @@@ @@@ @@@@@.
         `%%%%'':: ::%%:::@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
                  %::%%%::@@@@@@@@@@@    @@@@@    @@@@@@
                  %%%:%%%::@@@@@@@@@@@aa@@@@@@@aa@@@@@@          ,
                   %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%;,    ,
                    %%%%%%%%:::::%%%%%%aa@@@@@@@@^^^^^^^^^^^ ,
                      %%%%%:::%%%%%aa@@@@@@@@@@@^^^^^^^^^^^^^, ' ' '
                        :%%%%%%%%%@@@@@@@@@@@@@@@^^^^^^^^^^^   ,
                       :::::::%%%@@@@@@@@ @@@@@@@@@ ^^^^^^^      ,
                     :::::%%%%%%%@@@@@@@  @@@@@@@@@@@@^^@@
                   %%%%%%%%%%%%%%%@@@@@@   @@@@@@@@@@@@@@
                 ::::::%%%%%%%%%%%%@@@@@,,  @@@@@@@@@@@
               :::::::::::::%%%%%%%@@@@@,,',    @@
             ::::::::%%%%%%%%%%%%% @@@@@, ,,    @@
           %%%%%%%%%%%%%%%%%%%%%%  @@@@@aaaaaaa@@
      :::::::::%%%%%%%%%%%%%%%%%   @@@@@@@@@@@@@
    :::::::::::::::::::%%%%%%%%%%%  @@@@@@@@@@@
 ::::::::::::::%%%%%%%%%%%%%%%%%%%%
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
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
