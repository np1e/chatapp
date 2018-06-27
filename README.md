# ChatApp

Wir haben eine ChatApp gebaut, die es Nutzern erlaubt miteinander zu chatten. 

## Erkläuterung

Vor dem Start der Clients muss der JavaServer gestartet sein!
Die Kommunikation zwischen Client-Client und Client-Server wird testweise über Ports abgebildet (Deshalb ist es zwingend notwendig entsprechende TCP und UDP Ports als Kommandozeilenparameter zu übergeben).

Unsere ChatApp besteht aus:
-JavaClient (ClientGUI.java) (Mit Kommandozeilenparametern "9010 9081")
Der JavaClient kann vom Benutzer gestartet werden, indem er die ClientGUI.java ausführt.
Hier wird dem Benutzer eine GUI zur Verfügung gestellt, auf der er sich registrieren und einloggen kann (Vor dem Start muss der JavaServer gestaret sein [siehe unten]).
Nach dem erfolgreichen Login wird der Nutzer auf die Chat-Oberfläche weitergeleitet. Hier kann er links einen Nutzer anklicken.
Durch den Klick wird eine Chatanfrage an den Nutzer gesendet. Nimmt der Nutzer die erhaltene Chatanfrage an, können die beiden Nutzer chatten.

-PythonClient (clientGUI.py) (Mit Kommandozeilenparametern "8010 8081")
Der PythonClient kann gestaret werden, indem clientGUI.py ausgeführt wird (Mit Kommandozeilenparametern "8010 8081").
Der Client bietet ebenfalls eine GUI und dieselben Funktionen an.

-JavaServer
Der JavaServer ist für das anlegen von Userdaten (Registrieren), die Validation dieser beim Einloggen (Login), und die Aktualisierung der Liste von aktiven Usern zuständig.

### Technische Erläuterung

Die Kommunikation zwischen Client-Client findet über UDP statt. Hierbei ist rdt3.0 umgesetzt. 
Die Kommunikation zwischen Client-Server findet über TCP statt.

Im JavaClient wird JavaFX für die Umsetzung der GUI verwendet. Die Daten zwischen Model und GUI werden mit Hilfe des Observable Mechanismus aktualisiert und verwaltet.

Im PythonClient wird Tkinter für die Umsetzung der GUI verwendet. Die Daten zwischen Model und GUI werden mit Hilfe des Tkinter-Queue Mechanismus aktualisiert und verwaltet.

Bei der Umsetzung der Clients wurden die "receiving-parts" der Software in Threads ausgelagert, so dass die Gui weiterhin flüssig läuft. 

### Vorbedingungen

Die folgenden Komponenten müssen auf dem ausführenden System existieren.

Python 3.6
Tkinter
Java 1.8
JavaFX
(Optional: IDE (z.B. IntelliJ), das es erlaubt Kommandozeilen-Parameter zu übergeben)

### Installation

Git Repo clonen (optional direkt in IDE)

## Built With

Python 3.6
Tkinter
Java 1.8
JavaFX

## Autoren

Nick Hafkemeyer, Paul Schade, Jakob Kohlhas

