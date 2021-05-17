# TablutAshtonPlayers
A player for the Tablut Students Competition - UNIBO2019 - AIofdTiger team

```
          _____          __       _   _______ _                 
    /\   |_   _|        / _|     | | |__   __(_)                
   /  \    | |     ___ | |_    __| |    | |   _  __ _  ___ _ __ 
  / /\ \   | |    / _ \|  _|  / _` |    | |  | |/ _` |/ _ \ '__|
 / ____ \ _| |_  | (_) | |   | (_| |    | |  | | (_| |  __/ |   
/_/    \_\_____|  \___/|_|    \__,_|    |_|  |_|\__, |\___|_|   
                                                 __/ |          
                                                |___/           
```

## Installation of the repository on Ubuntu/Debian
For this project we've used the following versions of java and ant.

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
First, run the server in `https://github.com/AGalassi/TablutCompetition`, then you can choose 4 differents launch configuration:
```
#playerColor MUST be white or black (case insensitive)

#serverTimeout is set by default to 60 secs

#serverAddress is set by default to localhost

#debugSearchTime is used for debugging, otherwise searchTime is set by default to (serverTimeout - 3) secs

1) Specify playerColor

2) Specify playerColor, serverTimeout

3) Specify playerColor, serverTimeout, serverAddress

4) Specify playerColor, serverTimeout, serverAddress, debugSearchTime
```
For the TABLUT STUDENTS COMPETITION is recommended to choose the 1st configuration if the timeout is set to 60 secs, otherwise the 2nd.

### Run the program with ant
 If you want to make sure you're about to run the latest version you have to type:
```
cd /path/to/repository/TablutAshtonPlayers/Tablut

ant dist
```
then:

```
ant run -Dargs='(white|black) serverTimeout'

#example
ant run -Dargs='white 60'
```

### Run the program with java
The last compiled version of the project is in the Tablut/dist/ directory, if you want to make sure you're about to run the latest version you have to type:
```
cd /path/to/repository/TablutAshtonPlayers/Tablut

ant dist
```
then:
```
cd /path/to/repository/TablutAshtonPlayers/Tablut/dist

java -jar AIofdTiger.jar (white|black) serverTimeout

#example
java -jar AIofdTiger.jar white 60
```
###Test webhook
