🌌 Skybox Manager (Fabric 1.21.10)
Skybox Manager is a lightweight utility mod for Minecraft 1.21.10 that allows players to dynamically change their skybox in real-time. No more restarting the game or swapping entire Resource Packs just to change the view!

✨ Features
Real-time Switching: Change your sky environment instantly without reloading resources.

In-Game GUI: Simply press the K key to open the skybox selector menu.

Folder-Based Configuration: Manage your skyboxes easily by organizing folders in your config directory.

6-Face Support: Full support for cube-map textures (top, bottom, north, south, east, west).

🛠️ Installation
Ensure you have Fabric Loader installed for 1.21.10.

Download the latest mod .jar from the Releases tab or your build/libs folder.

Place the .jar file into your .minecraft/mods folder.

Requirement: You must also install the Fabric API mod for version 1.21.10.

📁 How to Setup Skyboxes
To make the mod detect your custom skies, follow this folder structure:

Run the game once to allow the mod to generate the base directory.

Navigate to .minecraft/config/myskyboxes/.

Create a new folder for each skybox pack (e.g., space_sky, retro_vibe).

Inside each folder, place your 6 images in .png format with these exact names:

top.png

bottom.png

north.png

south.png

east.png

west.png

<img width="235" height="214" alt="image" src="https://github.com/user-attachments/assets/c78ad512-ac58-41ea-b95a-23b73c5ff784" />

⌨️ Controls
K: Open the Skybox Selector menu (can be rebound in the Minecraft Controls settings).

🔨 Development Details
This mod was built using:

Java 21

Fabric Loom
