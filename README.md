# Deltarunic
Deltarune's iconic battle-box combat system, now brought to you by the worst person to try this already.

---
## Features
* **Offhand Combat Trigger:** Holding a designated item in your offhand intercepts incoming damage (`LivingIncomingDamageEvent`), cancels it, shoves the attacker 7 blocks away, and forces a combat session.
* **TPA-Style Challenges:** Send duel invites with a 30-second expiry timer and raycasted Line-of-Sight validation between eye positions. Generates clickable chat prompt strings (`[ACCEPT]`) linked to execution commands.
---
## Commands
* `/deltarunic testbbox [@e[limit=1]]` - Force-opens the battle GUI against yourself, your crosshair, or a targeted selector (enforces a 30-block distance limit).
* `/deltarunic accept <uuid>` - Accepts a pending combat invitation.
---
## Technical Details
* Built for `NeoForge 21.1.248`, orignially.
* Written with code that is held together with duct tape.
---
## CREDITS:
* Made by `Conditional_Cognition`(GitHub) AKA `Cndtnl_Cognition`(Minecraft) in his free time. For fun. Even though it was not fun to make.