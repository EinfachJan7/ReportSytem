# 🛡️ AbyssBay Report System

> **Das ultimative Java Plugin für professionelles Server-Management.** > Eine nahtlose Brücke zwischen **Minecraft Java & Bedrock Edition** mit direkter Discord-Anbindung.

---

## 🚀 Highlights

* **Dual-Engine Support:** Volle Integration von Java-UUIDs und Bedrock-XUIDs (via Floodgate).
* **Discord Live-Feed:** Echtzeit-Benachrichtigungen mit Rich Embeds und Admin-Ping.
* **Smart Tracking:** Automatisches Datenbank-Logging für Offline-Spieler und Statistiken.
* **Performance:** Leichtgewichtige SQLite-Anbindung und optimiertes Cooldown-System.

---

## 🛠️ Features im Detail

### 🌐 Cross-Platform Erkennung

* **Bridges:** Erkennt automatisch, ob ein Spieler über Java oder Bedrock beitritt.
* **Floodgate API:** Nutzt XUID-Tracking für lückenlose Identifikation.
* **Status-Check:** Zeigt den Online-Status und "Zuletzt gesehen"-Daten an.

### 📊 Management & Admin-Tools

* **Interaktive Menüs:** `/report <Name>` öffnet ein GUI/Menü für intuitive Nutzung.
* **ID-System:** Jeder Fall erhält eine eindeutige Ticket-ID (z.B. `REP-123`).
* **Admin Dashboard:** `/reportdash` für den schnellen Überblick offener Fälle.
* **Data-Mining:** Umfassende `/reportstats` zur Analyse von Wiederholungstätern.

### 💬 Discord Integration

* **Rich Embeds:** Inklusive Spieler-Skins (Thumbnails) und detaillierter Infos.
* **Status-Sync:** Updates im Discord, wenn ein Report geschlossen wird.
* **Role-Mentions:** Sofortige Benachrichtigung für dein Mod-Team.

---

## 📋 Befehlsreferenz

| Befehl | Beschreibung | Berechtigung |
| --- | --- | --- |
| `/report <Spieler> [Grund]` | Erstellt einen neuen Report | `report.use` |
| `/reportdash` | Öffnet das Admin-Dashboard | `report.admin` |
| `/reportinfo <ID>` | Zeigt alle Details zu einem Report | `report.admin` |
| `/reportclose <ID>` | Schließt und archiviert einen Fall | `report.admin` |
| `/reportstats [Spieler]` | Ruft globale oder spielerspezifische Stats ab | `report.admin` |

---

## 🔧 Installation & Setup

1. **Voraussetzungen:** Java 16+, Bukkit/Spigot/Paper 1.20+ (Floodgate optional).
2. **Build:** Führe `mvn clean package` aus.
3. **Deployment:** Schiebe die `.jar` aus dem `target/` Ordner in deinen `/plugins/` Ordner.
4. **Config:** Hinterlege deine Webhook-URL in der `config.yml`:
```yaml
discord:
  webhook-url: "DEINE_URL_HIER"
  admin-role-id: "123456789"
settings:
  cooldown: 60
  bedrock-support: true

```


5. **Start:** Server neu starten – fertig!

---

## 📂 Projektstruktur

Das System ist nach dem **Model-View-Controller (MVC)** Prinzip aufgebaut:

* `commands/` – Saubere Trennung der Logik für Spieler- und Admin-Befehle.
* `managers/` – Zentrale Verwaltung für Datenbank, Discord und Reports.
* `listeners/` – Event-Handling für automatisiertes Tracking.
* `models/` – Abstraktion der Report-Objekte für einfache Erweiterbarkeit.

---

## 🛠 Troubleshooting

* **Keine Discord-Nachricht?** Prüfe die Webhook-URL und die Server-Firewall (Port 443).
* **Bedrock-Spieler fehlen?** Stelle sicher, dass Floodgate korrekt installiert ist.
* **Datenbank-Fehler?** Prüfe, ob Schreibrechte im Plugin-Ordner für die `.db` Datei bestehen.

---

**Version:** `1.0.0-RELEASE` | **Entwickelt von:** `AbyssBay Team` 🚀