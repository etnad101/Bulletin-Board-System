# Client-Server Bulletin Board System

PREREQUISITES:
1. Ensure Java JDK (version 21 or higher recommended) is installed.
2. For the Client, download the JavaFX SDK for your OS from:
   https://gluonhq.com/products/javafx/
   (Extract it to a known location, e.g., C:\javafx-sdk-25.0.2 or /Users/name/javafx-sdk-25.0.2)


--------------------------------------------------------------------------------
SERVER INSTRUCTIONS
(Run these commands inside the /Server directory)
--------------------------------------------------------------------------------

1. COMPILE:
   javac *.java

2. RUN (Example):
   java Server 4444 800 600 100 50 red green blue yellow


--------------------------------------------------------------------------------
CLIENT INSTRUCTIONS
(Run these commands inside the /Client directory)
--------------------------------------------------------------------------------

WINDOWS
1. COMPILE:
   javac --module-path "path\to\javafx-sdk-25.0.2\lib" --add-modules javafx.controls,javafx.fxml *.java

2. RUN:
   java --module-path "path\to\javafx-sdk-25.0.2\lib" --add-modules javafx.controls,javafx.fxml Client


MACOS / LINUX
1. COMPILE:
   javac --module-path /path/to/javafx-sdk-25.0.2/lib --add-modules javafx.controls,javafx.fxml *.java

2. RUN:
   java --module-path /path/to/javafx-sdk-25.0.2/lib --add-modules javafx.controls,javafx.fxml Client