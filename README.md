#ChatApp

Wir haben eine ChatApp gebaut, die es Nutzern erlaubt miteinander zu chatten.

###Installation:
1.	IDE mit Java und Python Unterstützung öffnen und Git Repository clonen (IntelliJ: VCS > Git > Clone). Clone URL finden Sie auf der RhodeCode Seite: https://scm.mi.hs-rm.de/rhodecode/2018netze/2018netze13/chatApp
2.	gson-2.7.jar (liegt in src/ Ordner) als Library hinzufügen 
a.	(IntelliJ: src/ gson-2.7.jar > Rechtsklick > Add as Library > OK)
3.	Python Interpreter aktivieren 
a.	Python installieren (IntelliJ: File > Settings > Plugins > Install JetBrains plugin > „Python“ > Install)
b.	Python Interpreter aktivieren (IntelliJ: File > Project Structure > SDKs > „Grünes Plus“ > Python SDK > OK) 
4.	Kommandozeilenparameter für Files festlegen (IntelliJ: File auswählen > Dropdown links neben „Play“/Start-Button oben rechts klicken > Edit configurations > Program arguments ausfüllen > Apply > OK):
a.	JavaServer/Main.java: “-m gui”
b.	JavaClient/ClientGUI.java: “9010 9081”
c.	PythonClient/clientGUI.py: “9010 9091”
###Startanweisung:
1.	Starten JavaServer/Main.java > Start-Button drücken
2.	Starten JavaClient/ClientGUI.java > Login z.B. mit Name “test1”, PW “test” oder Registrieren
3.	Starten PythonClient/clientGUI.py > Login z.B. mit Name “test2”, PW “test” oder Registrieren
4.	Klick auf Nutzer in Client A > Schickt entstprechende Anfrage an Client B
5.	Klick auf Annahmen in Client B
6.	Chatnachrichten schreiben in Client A und B
7.	Beenden des Programms über Logout und Schließen des Windows oder direkt Window schließen
###Erläuterung:

Vor dem Start der Clients muss der JavaServer gestartet sein!
Die Kommunikation zwischen Client-Client und Client-Server wird testweise über Ports abgebildet (Deshalb ist es zwingend notwendig entsprechende TCP und UDP Ports als Kommandozeilenparameter zu übergeben).

######Unsere ChatApp besteht aus:

#####JavaClient (ClientGUI.java) (Mit Kommandozeilenparametern "9010 9081")
Der JavaClient kann vom Benutzer gestartet werden, indem er die ClientGUI.java ausführt.
Hier wird dem Benutzer eine GUI zur Verfügung gestellt, auf der er sich registrieren und einloggen kann. Nach dem erfolgreichen Login wird der Nutzer auf die Chat-Oberfläche weitergeleitet. Hier kann er links einen Nutzer anklicken. Durch den Klick wird eine Chatanfrage an den Nutzer gesendet. Nimmt der Nutzer die erhaltene Chatanfrage an, können die beiden Nutzer chatten.
#####PythonClient (clientGUI.py) (Mit Kommandozeilenparametern "8010 8081")
Der PythonClient kann gestaret werden, indem clientGUI.py ausgeführt wird (Mit Kommandozeilenparametern "8010 8081").
Der Client bietet ebenfalls eine GUI und dieselben Funktionen an.
#####JavaServer
Der JavaServer ist für das anlegen von Userdaten (Registrieren), die Validation dieser beim Einloggen (Login), und die Aktualisierung der Liste von aktiven Usern zuständig.
Technische Erläuterung:
Die Kommunikation zwischen Client-Client findet über UDP statt.
Die Kommunikation zwischen Client-Server findet über TCP statt.
Im JavaClient wird JavaFX für die Umsetzung der GUI verwendet. Die Daten zwischen Model und GUI werden mit Hilfe des Observable Mechanismus aktualisiert und verwaltet.
Im PythonClient wird Tkinter für die Umsetzung der GUI verwendet. Die Daten zwischen Model und GUI werden mit Hilfe des Tkinter-Queue Mechanismus aktualisiert und verwaltet.
Bei der Umsetzung der Clients wurden die "receiving-parts" der Software in Threads ausgelagert, so dass die Gui weiterhin flüssig läuft.
###Vorbedingungen:
Die folgenden Komponenten müssen auf dem ausführenden System existieren.

Python 3.6
Tkinter
Java 1.8
JavaFX
(Optional: IDE (z.B. IntelliJ), das es erlaubt Kommandozeilen-Parameter zu übergeben)
###Autoren:
Nick Hafkemeyer, Paul Schade, Jakob Kohlhas

