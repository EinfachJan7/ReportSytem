ABYSSBAY REPORT SYSTEM - Java Plugin
=====================================

EIN PROFESSIONELLES REPORT-SYSTEM FÜR MINECRAFT JAVA & BEDROCK EDITION

FEATURES:
=========

✓ JAVA & BEDROCK EDITION SUPPORT
  - Automatische Java/Bedrock-Erkennung
  - UUID-Tracking für Java-Spieler
  - XUID-Tracking für Bedrock-Spieler (via FloodgateAPI)
  - Spieler-Datenbank für Bedrock

✓ DISCORD WEBHOOK INTEGRATION
  - Live-Reports mit Embed-Nachrichten
  - Admin-Role Mentions
  - Report-Status Updates
  - Discord Channel Notifications

✓ REPORT MANAGEMENT
  - Automatische Report-IDs
  - Cooldown-System
  - Report-Statistiken
  - Status Tracking (OPEN/CLOSED)
  - SQLite Datenbank

✓ ADMIN-BEFEHLE
  /report <Spieler> <Grund>    - Report erstellen
  /reportstats [Spieler]        - Statistiken
  /reportinfo <Report-ID>       - Details anzeigen
  /reportclose <Report-ID>      - Report schließen
  /reportdash                   - Dashboard

INSTALLATION:
=============

1. Voraussetzungen:
   - Java 16+
   - Bukkit/Spigel Server 1.20+
   - FloodgateAPI (optional, für Bedrock-Support)

2. Build:
   mvn clean package

3. JAR kopieren:
   Kopiere die .jar aus target/ zu plugins/

4. Konfiguration:
   Bearbeite config.yml und trage Discord Webhook ein:
   - discord-webhook: <DEINE_WEBHOOK_URL>
   - admin-role-id: <DEINE_ROLE_ID>

5. Server neustarten

KONFIGURATION:
==============

config.yml:
- Discord Webhook URL
- Admin Role ID
- Server-Name
- Report-Gründe
- Cooldown (Sekunden)
- Bedrock-Support aktivieren/deaktivieren

plugin.yml:
- Berechtigungen
- Befehl-Aliase

DATABASE:
=========

SQLite-Datenbank mit 3 Tabellen:
- reports: Report-Daten
- bedrock_players: Bedrock-Spieler (XUID)
- report_stats: Report-Statistiken

BEFEHLE:
========

SPIELER:
  /report PlayerName Hacking
  /melden PlayerName XRay
  (Alternativ: /report PlayerName zum interaktiven Menü)

ADMIN:
  /reportstats                   - Alle Reports
  /reportstats PlayerName        - Stats für Spieler
  /reportinfo REP-123-456        - Report-Details
  /reportclose REP-123-456       - Report schließen
  /reportdash                    - Dashboard mit offenen Reports

BERECHTIGUNGEN:
===============

report.use      - Spieler können Reports erstellen (default: true)
report.admin    - Admin kann Reports verwalten (default: op)

STRUKTUR:
=========

src/main/java/com/minetales/report/
├── MinetalesReportPlugin.java          (Main Plugin Class)
├── commands/
│   ├── ReportCommand.java              (/report)
│   ├── ReportStatsCommand.java         (/reportstats)
│   ├── ReportInfoCommand.java          (/reportinfo)
│   ├── ReportCloseCommand.java         (/reportclose)
│   └── ReportDashCommand.java          (/reportdash)
├── managers/
│   ├── ReportManager.java              (Report-Verwaltung)
│   ├── DatabaseManager.java            (SQLite)
│   ├── DiscordManager.java             (Discord Webhooks)
├── models/
│   └── Report.java                     (Report-Datenmodell)
├── listeners/
│   ├── PlayerJoinListener.java         (Bedrock-Tracking)
│   └── PlayerQuitListener.java         (Cleanup)
└── utils/
    ├── ConfigManager.java              (Config-Verwaltung)
    └── PlayerUtils.java                (Player-Utilities)

BEDROCK SUPPORT:
================

- Bedrock-Spieler werden mit "." Präfix erkannt
- XUID wird beim Join automatisch gespeichert
- FloodgateAPI Integration für Live-Daten
- "Zuletzt gesehen" für Offline-Spieler

DISCORD INTEGRATION:
====================

Format der Webhook-Nachricht:
- Report ID
- Server-Name
- Reporter-Name
- Gemeldeter Spieler
- Spieler-Typ (Java/Bedrock)
- Online/Offline Status
- Grund
- Report-Statistiken
- Thumbnail mit Spieler-Skin

Admin-Role wird automatisch gepingt

DEPENDENCIES:
==============

- Bukkit API 1.20
- FloodgateAPI 2.2.2
- GSON (JSON-Parsing)
- SQLite JDBC

BUILD:
======

mvn clean package
→ output/target/report-system-1.0.0.jar

TROUBLESHOOTING:
================

[ERROR] FloodgateAPI nicht gefunden?
→ FloodgateAPI Plugin nicht installiert
→ Installiere: floodgate-bukkit.jar

[ERROR] Webhook funktioniert nicht?
→ Webhook-URL überprüfen
→ Discord-Kanal-Berechtigungen
→ Server-Firewall prüfen

[ERROR] Build fehlgeschlagen?
→ Java 16+ erforderlich
→ Maven aktualisieren
→ Dependencies downloaden: mvn clean install

SUPPORT & COMMUNITY:
====================

Bukkit: https://bukkit.gamepedia.com
Spigel: https://www.spigotmc.org
GitHub: https://github.com/

DEBUG MODE:
===========

Server-Log prüfen für Fehler:
- Datenbank-Fehler
- Discord-Fehler
- FloodgateAPI-Fehler
- Command-Fehler

ZUKÜNFTIGE FEATURES:
====================

- Web-Dashboard
- Report-Archiv (Auto-Delete)
- Reward-System
- Multi-Webhook Support
- Report-Export (CSV/JSON)
- Auto-Ban nach X Reports
- Custom Report-Gründe (in-game)

VERSION:
========

Plugin Version: 1.0.0
Kompatibilität: Bukkit/Spigel 1.20+
Java: 16+
Status: Produktionsreif

LIZENZ:
=======

Open Source - Frei nutzbar für deine Server

---

Viel Erfolg mit deinem Report-System! 🚀

Entwickelt von AbyssBay Team
