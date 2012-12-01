Space Wars
==========

Autoren:
_Kai Boschung_
_Mathias Winkler_


__Want to kick ass in space?__

Projektantrag
-------------


__Space Wars__
__Modul Prog 1: JAVA-Projekt__


### Projektbeschrieb ###

Im Internet haben wir folgendes interessante Flash-Game entdeckt:
http://www.kongregate.com/games/CasualCollective/the-space-game

In diesem 2D-Spiel werden in Minen Rohstoffe abgebaut, welche man zum Ausbau seiner Mienenlandschaft einsetzen kann oder in die Verteidigung seiner Planeten investieren kann. Ein wichtiger Faktor ist auch das Energienetz: Jede Miene muss im Einflussgebiet des Energienetzes sein. Energie wird im Solarkraftwerk produziert und kann über Relais verteilt werden.

Wir wollen noch einen Schritt weitergehen und das Spiel multiplayer-fähig machen. Durch eine Client-Server-Architektur, werden zwei Spieler auf derselben Karte platziert. Um dem Spiel noch etwas Würze zu verleihen, können die Spieler Angriffe mit Raumschiffen starten.


### Herausforderung ###

Die Herausforderung besteht auf der einen Seite in der Implementation eines Netzwerkspiels. Beide Spieler sollen auf der gleichen Karte in Echtzeit gegeneinander spielen können. Dabei soll kein Spieler auf den anderen warten müssen, der Spielzustand muss also synchronisiert werden.

Auf der anderen Seite ist es nicht ganz trivial, auf einer grossen Karte mit vielen Objekten die Kollisionserkennung korrekt und auch performant durchzuführen. Zudem sollen die Objekte noch in einer vernünftigen Framerate gerendert werden.

 
### Projektziele ###

__Woche 45__

- Game Loop (Update und Rendering) realisiern, Framework zur grafischen Ausgabe am Bildschirm
- Client/Server-Architektur implementieren (RMI oder SIMON, 2 Spieler-fähig und ausbaubar)
- Game State modellieren
- Map generieren mit Heimatplaneten, Mineralienplaneten mit verschiedenen Kapazitäten

__Woche 48__

- Userinteraktionen abfangen (Tastatur, Maus), Karte scrollbar machen
- Head-up-Display implementieren (diverse Indikatoren für Abbau/Min, Energieproduktion, Score)
- Objekte (Solarkraftwerk, Mienen, Relais, Laserkanonen usw.) platzierbar und auswählbar zum Upgraden oder Recyceln

__Woche 51__

- Collision Detection implementieren (Bounding Sphere)
- Abbau von Mineralien, korrekter Energiefluss über die Relais, Korrekte Berechnung der Indikatoren
- Spiel zu zweit spielbar, jedoch noch ohne Interaktionen

__Woche 2__

- Laserkanone kann mit Schüssen verteidigen und schiesst korrekt auf Gegner
- Raumschiffe bauen und diese gehen korrekt in den Angriff
- Schaden wird korrekt berechnet und Objekte werden zerstört, wenn Energie auf 0 ist

### Projektrisiko ###

Die Zusammenarbeit ist nicht sehr einfach. Doch dieses Risiko wird auf ein Minimum reduziert, indem wir GIT zur Versionierung einsetzen und uns fleissig in der Schule treffen können. Die Programmiererfahrungen sind sehr unterschiedlich. Kai wird die fehlende Erfahrung mit etwas mehr Fleiss wieder wettmachen.

Eine der grössten technische Schwierigkeiten in diesem Projekt ist die Netzwerksynchronisation zwischen den beiden Spielern. Diese Synchronisation kann man nur rudimentär machen, oder auch mit sehr cleveren aber komplexen Methoden. Die Interaktion zwischen zwei Spielern haben wir deshalb erst für das Ende des Projekts vorgesehen.
Als Hilfe werden wir uns an folgenden Artikeln orientieren:

http://www.gabrielgambetta.com/?p=11
http://www.gabrielgambetta.com/?p=22
http://www.gabrielgambetta.com/?p=63
