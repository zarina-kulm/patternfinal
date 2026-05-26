# ThronesOfPatterns

A 2D fantasy RPG battle game developed with **Java** and **libGDX**.

This project was inspired by medieval fantasy worlds similar to *Game of Thrones* and was created as a university Design Patterns project.

Players choose a hero, fight enemies through multiple levels, unlock abilities, survive increasingly difficult battles, and ultimately claim the throne.

---

# 🎮 Gameplay

The game contains a full 7-level campaign divided into 2 parts:

## Part I — The Northern Kingdom
- Levels 1–4
- Easier enemies
- Introductory battles
- Snow forest environment

## Part II — The Dark Realm
- Levels 5–7
- Stronger enemies
- Increased enemy damage and HP
- Final throne battles

The final objective is to survive all levels and become the ruler of the realm.

---

# ⚔ Heroes

The player can choose between 3 heroes:

| Hero | Description |
|---|---|
| Knight | Balanced melee fighter with shield |
| Mage | Powerful magic attacks |
| Archer | Fast ranged attacks |

Each hero has unique abilities and combat style.

---

# 💣 Game Features

- 7 campaign levels
- Multiple fantasy maps
- Enemy AI system
- HP and damage system
- Bomb ability
- Shield system
- Hero special abilities
- Victory and Game Over screens
- Background soundtrack system
- Animated gameplay
- Increasing difficulty system

---

# 🎵 Audio System

Different soundtrack themes are used depending on the current game screen:

| Screen | Music |
|---|---|
| Main Menu | Menu soundtrack |
| Gameplay | Battle soundtrack |
| Victory Screen | Victory theme |
| Game Over Screen | Defeat theme |

---

# 🎮 Controls

| Key | Action |
|---|---|
| W / A / S / D | Move |
| SPACE | Attack |
| Q | Shield |
| E | Hero Skill |
| B | Bomb |
| SHIFT | Dash |
| TAB | Change Target |
| ESC | Menu |

---

# 🧩 Design Patterns Used

The project was developed using multiple Design Patterns:

| Pattern | Purpose |
|---|---|
| Singleton | Stores global game state |
| Factory Method | Creates hero objects |
| Abstract Factory | Creates fantasy house systems |
| Prototype | Clones enemies for spawning |
| Builder | Builds battle configurations |
| Facade | Simplifies combat system logic |
| State/Screen | Manages game screens |

---

# 🏗 Project Structure

```text
core/
 └─ src/main/java/com/thrones/patterns/
    ├─ screens/
    ├─ characters/
    ├─ enemies/
    ├─ factories/
    ├─ patterns/
    └─ utils/

assets/
 ├─ sounds/
 ├─ music/
 ├─ backgrounds/
 ├─ ui/
 └─ sprites/

lwjgl3/
 └─ Desktop launcher
