# Quick upload guide (no git CLI needed)

You've already made the empty `flaming-gold-sword` repo on GitHub. Now:

## 1. Select all files in this folder

Open `/Users/franklin/flaming-gold-sword/` in Finder. Press `Cmd+A` to select everything, **including hidden files**:
- `.github/` (folder)
- `.gitignore` (file)
- All the other files and folders

> **Showing hidden files in Finder**: press `Cmd+Shift+.` (period) inside the folder. Hidden files/folders (starting with `.`) become visible. You need this to see `.github/` and `.gitignore`.

## 2. Drag onto GitHub

1. In your browser, go to your empty `flaming-gold-sword` repo page on GitHub.
2. You'll see a section with a link that says "**uploading an existing file**" — click it.
3. Drag the selected files/folders from Finder into the browser's drop zone.
4. GitHub will show them listed. Scroll to bottom.
5. Commit message: type `initial` or anything.
6. Click **Commit changes**.

## 3. Wait for the build

1. Click the **Actions** tab at the top.
2. A run called "Build" will be running (yellow dot). Wait ~90 sec.
3. When green ✓: click on the run name.
4. Scroll to **Artifacts** at the bottom.
5. Click `flaming-gold-sword-jar` — downloads a zip.
6. Unzip. The `.jar` inside is what goes into `/mods/` on your server.

Then follow the **Install on your Fabric server** section of `README.md`.
