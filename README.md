# Flaming Gold Sword

A custom 3D sword for Fabric Minecraft servers. Players don't install anything — the server sends the model and textures to them automatically when they join.

---

## What you need before you start

Three `.jar` files. They all go into your server's `mods` folder.

1. **Fabric API** — almost every Fabric server already has this. Look in your server's `mods` folder for a file starting with `fabric-api-`. If it's not there, download it from https://modrinth.com/mod/fabric-api (pick the version matching your Minecraft version).

2. **Polymer** — download from https://modrinth.com/mod/polymer/versions. Pick the line matching your Minecraft version (e.g. for MC 1.21.1, get the one labeled `0.9.19+1.21.1` or similar).

3. **The Flaming Gold Sword .jar** — get it from this repo:
   - Go to the **Actions** tab at the top of this GitHub page
   - Click the most recent run with a green ✓
   - Scroll to the bottom, click **flaming-gold-sword-jar** to download a zip
   - Unzip it — inside is `flaming-gold-sword-1.0.0.jar`

> ⚠️ **Minecraft version matters.** This is built for Minecraft **1.21.1**. If your server is on a different version, see [Different Minecraft version?](#different-minecraft-version) at the bottom.

---

## Install on your server

1. Open your server hosting control panel.
2. **Stop the server.** (Don't skip this — files might not load right if the server is running.)
3. Open the file manager and navigate to the `mods` folder.
4. Upload all three `.jar` files into `mods`.
5. **Start the server.**
6. Wait until it says "Done" in the console (means it's ready).

---

## Get the sword in-game

Join the server. The first time you join, you'll see a popup that says *"This server is requesting a resource pack"*. **Click Yes / Proceed.** The sword needs this to look right.

Then in chat, type:

```
/give @s flaming_gold_sword:flaming_gold_sword
```

You'll get the sword in your inventory. (Only ops can run `/give`.)

You can also fight Blazes in the Nether — they have a **5% chance** to drop the sword when killed. You'll hear a Totem sound when one drops, so you'll know.

---

## How to use it (test each thing)

| What you do | What happens |
|---|---|
| **Left-click an enemy** | Hits for ~8 damage, sets them on fire for 6 seconds, AND damages everything within 2 blocks (3 damage + 4s burn) |
| **Left-click the top of a block** | Places lava on top of that block 🔥 |
| **Right-click in the air** | **Flame Wave** — shoots fire in a cone in front of you, 8 blocks long. Burns enemies for 6 damage + 8s |
| **Shift + right-click** | **Self-Ignite** — gives you Fire Resistance + Strength for 10 seconds |
| **Just hold it** | You're immune to fire and lava as long as it's in your hand |

**Cooldown:** Right-click and Shift+right-click share a 30-second cooldown.

**Durability:** None. The sword never breaks.

**Fireproof:** If you drop it in lava, it won't burn up.

---

## Things to watch out for

- **Sword looks plain / like a netherite sword?** The player rejected the resource pack. Have them disconnect, rejoin, and click **Yes** when prompted. Or open `server.properties` and set `require-resource-pack=true` — this forces them to accept (kicks them if they don't).
- **`/give` says unknown item?** The mod didn't load. Check your server console for red errors — usually it's a missing dependency (Fabric API or Polymer).
- **Left-click on a block doesn't place lava?** You have to click the **top face** of the block (looking down at it). Sides and bottom don't work.
- **Right-click does nothing?** You're probably still in cooldown — wait 30 seconds.
- **Building the .jar fails on GitHub Actions?** Open the failed run, scroll to the red error. Most common: Minecraft version mismatch.

---

## Different Minecraft version?

This is built for **MC 1.21.1**. If your server is on something else (e.g. 1.20.4, 1.21.4), you need to edit one file before downloading the .jar:

1. In this repo, click `gradle.properties`.
2. Click the pencil icon (Edit).
3. Change these 5 lines to match your version:

```
minecraft_version=1.21.1
yarn_mappings=1.21.1+build.3
loader_version=0.16.5
fabric_version=0.102.1+1.21.1
polymer_version=0.9.19+1.21.1
```

4. Find the right values at:
   - https://fabricmc.net/develop/ — pick your MC version, copy Yarn / Loader / Fabric API
   - https://modrinth.com/mod/polymer/versions — pick the one matching your MC version
5. Also edit `src/main/resources/fabric.mod.json` and change `"minecraft": "~1.21.1"` to your version.
6. Commit the changes. GitHub Actions will rebuild automatically.
7. Download the new `.jar` from the Actions tab and drop it into `mods`.
