Spiced Cider server (auto-updating)

Requires Java 21.

Start the server with start.bat (Windows), start.sh (Linux/macOS), or directly with:
    java -Xmx6G -jar spicedcider-server.jar

Every start, the launcher downloads the latest Spiced Cider pack, installs the NeoForge
version the pack needs, then starts the server. Accept the Minecraft EULA in eula.txt after
the first start, then start it again.

While the server runs, it checks the pack for updates every 5 minutes. When an update
changes anything the server uses, players are warned in chat and the server restarts
itself to install it (straight away if nobody is online). Settings are in spicedcider-server.properties.

Set the server jar to spicedcider-server.jar, or use
"java -Xms128M -Xmx{{SERVER_MEMORY}}M -jar spicedcider-server.jar" as the startup command.
