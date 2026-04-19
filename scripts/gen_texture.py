#!/usr/bin/env python3
"""Generates the 16x16 flaming_gold_sword.png item icon using only the Python stdlib."""
import os
import struct
import zlib

OUT = os.path.join(
    os.path.dirname(os.path.dirname(os.path.abspath(__file__))),
    "src", "main", "resources", "assets", "flaming_gold_sword", "textures", "item",
    "flaming_gold_sword.png",
)

COLORS = {
    ".": (0, 0, 0, 0),
    "G": (255, 210, 63, 255),
    "g": (200, 146, 45, 255),
    "d": (138, 94, 20, 255),
    "L": (59, 31, 16, 255),
    "R": (220, 30, 20, 255),
    "O": (255, 120, 30, 255),
    "Y": (255, 210, 70, 255),
    "W": (255, 250, 220, 255),
    "B": (90, 10, 10, 255),
}

PATTERN = [
    "...............O",
    "..............RY",
    ".............OGG",
    "............OGGR",
    "...........OGGd.",
    "..........OGGd..",
    ".........OGGd...",
    "........OGGd....",
    ".......GGGg.....",
    ".GGG..GGGg......",
    "GGGGGGGGg.......",
    ".gggggg.........",
    "...LL...........",
    "...LL...........",
    "..gGg...........",
    "..dBd...........",
]


def png_chunk(tag, data):
    length = struct.pack(">I", len(data))
    payload = tag + data
    crc = struct.pack(">I", zlib.crc32(payload) & 0xFFFFFFFF)
    return length + payload + crc


def main():
    w, h = 16, 16
    assert len(PATTERN) == h and all(len(r) == w for r in PATTERN), "pattern must be 16x16"

    raw = bytearray()
    for row in PATTERN:
        raw.append(0)
        for ch in row:
            raw.extend(COLORS[ch])

    sig = b"\x89PNG\r\n\x1a\n"
    ihdr = struct.pack(">IIBBBBB", w, h, 8, 6, 0, 0, 0)
    idat = zlib.compress(bytes(raw), 9)

    png = sig + png_chunk(b"IHDR", ihdr) + png_chunk(b"IDAT", idat) + png_chunk(b"IEND", b"")

    os.makedirs(os.path.dirname(OUT), exist_ok=True)
    with open(OUT, "wb") as f:
        f.write(png)
    print(f"wrote {len(png)} bytes -> {OUT}")


if __name__ == "__main__":
    main()
