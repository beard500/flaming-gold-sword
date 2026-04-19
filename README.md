# Flaming Gold Sword (Fabric server-side mod)

Adds a flaming gold sword to your Fabric server. Players **don't install anything** — the mod auto-sends the required resource pack on join.

## What the sword does

**Stats**
- Damage: ~8 per hit (netherite-tier +1)
- Attack speed: 1.6 per second (slow, like netherite)
- Durability: **unbreakable**
- Fireproof (won't despawn in lava)

**Left-click an enemy** → hits for full damage + sets on fire for 6s + **AoE splash**: everything within 2 blocks of the target takes 3 fire damage and burns for 4s. Spawns flame, lava, and smoke particles.

**Left-click the ground** (top face of a block) → **places lava** on top of it. Emits flame particles + lava-bucket sound.

**Right-click** (cooldown: 30s) → **Flame Wave**: launches a cone of fire in front of you up to 8 blocks out. Any enemy in the cone takes 6 damage and burns for 8s. Lots of fire particles.

**Shift + right-click** (shares the 30s cooldown) → **Self-Ignite**: grants yourself Fire Resistance I + Strength I for 10 seconds. Big flame burst around you.

**Passive while held** → Fire Resistance is refreshed every second, so you're immune to fire and lava as long as the sword is in your main hand. (Effect doesn't linger after you put it away.)

**Obtaining** → **5% drop from Blazes.** A plays a Totem sound when one drops. Ops can also spawn it via `/give` (see below).

## How to get the built `.jar`

This repo builds itself via GitHub Actions. You don't need Java installed locally.

1. After `git push` finishes (done by the CLI setup — see `UPLOAD.md`), go to your GitHub repo in a browser.
2. Click the **Actions** tab.
3. Wait ~90 seconds for the run to go green ✓.
4. Click the run name → scroll to **Artifacts** → click `flaming-gold-sword-jar` to download a zip.
5. Unzip → you have `flaming-gold-sword-1.0.0.jar`.

## Check your Minecraft version BEFORE pushing

This defaults to **Minecraft 1.21.1**. If your server runs a different version, edit `gradle.properties` **before pushing** — the top 5 lines:

```
minecraft_version=1.21.1
yarn_mappings=1.21.1+build.3
loader_version=0.16.5
fabric_version=0.102.1+1.21.1
polymer_version=0.9.19+1.21.1
```

Find your correct values at:
- https://fabricmc.net/develop/ (pick your MC version → copy the Yarn/Loader/Fabric API values)
- https://modrinth.com/mod/polymer/versions (pick the line matching your MC version)

Also update `src/main/resources/fabric.mod.json`, change `"minecraft": "~1.21.1"` to match your version.

## How to install on your Fabric server

Your server needs three mods in `/mods/`:

1. **Fabric API** — you almost certainly have this already (`fabric-api-*.jar`). If not: https://modrinth.com/mod/fabric-api
2. **Polymer** — from https://modrinth.com/mod/polymer (match the MC version). Drop the downloaded `.jar` into `/mods/`.
3. **This mod** — the `flaming-gold-sword-1.0.0.jar` you downloaded from GitHub Actions. Drop it into `/mods/`.

Then:
1. Stop your server from your host's control panel.
2. Upload the three `.jar` files via the host's file manager into `/mods/`.
3. Start your server.
4. Join the server. On first join, clients get prompted to download the resource pack — tell players to accept ("Yes" or "Proceed"). Once accepted, the flaming gold sword renders correctly in their inventory and hand.

## Getting one in-game

```
/give @s flaming_gold_sword:flaming_gold_sword
```

Or farm Blazes — 5% drop rate per kill.

## Troubleshooting

- **"Unknown item flaming_gold_sword:flaming_gold_sword"** — the mod didn't load. Check server logs for errors about missing dependencies (Fabric API / Polymer / Java version mismatch).
- **Sword looks like a netherite sword, not a gold pixel sword** — the client rejected the resource pack. Tell them to accept it when prompted, or enable `require-resource-pack=true` in `server.properties` to force it.
- **Build fails on GitHub Actions** — open the failed run, read the red log. Most common cause: MC version mismatch between `gradle.properties` and available Fabric/Polymer versions. Double-check all 5 version numbers at the links above.
- **"Incompatible mod loader"** — your server is running Minecraft, not Fabric, or a different MC version than the jar was built for. Verify with `/version` in-game.
- **Abilities do nothing / left-click on ground doesn't place lava** — make sure you're left-clicking the **top** face of a block (looking down). Side and bottom faces do nothing special.

## What this mod is NOT

- The fancy 7-animation flaming dance you saw in Blockbench does **not** carry over — Minecraft's in-hand item renderer doesn't play Blockbench keyframes. The in-world look is a static 16x16 pixel-art sword. The flame particles (on hit + idle + abilities) are what makes it feel alive in-game.
- Future work: a "3D in-hand model" is possible by shipping the Blockbench `.bbmodel` converted to Minecraft's item model JSON format. Not in v1 because the free-form rotations don't all convert cleanly.
