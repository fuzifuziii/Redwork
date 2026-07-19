# Redwork

**Redwork** is a Minecraft (NeoForge) mod that adds a small set of compact, redstone-driven automation blocks. Each block is a single-block "machine" — no multiblock structures to build (aside from Chutes, which stack), no external power source, just place it, wire it into redstone and item pipes, and go.

### Blocks

* **Drill** — Faces a direction and, while powered by redstone, mines the block directly in front of it. Right-click it with a pickaxe, axe, shovel or hoe to load that tool into the Drill; it uses the tool's stats (mining speed, Efficiency, Unbreaking) and wears it down like a player would, storing everything it digs up in its own internal inventory. Outputs a redstone signal proportional to the loaded tool's remaining durability, so you can tell when it needs a replacement.
* **Striker** — *New!* The combat counterpart to the Drill. Faces a direction and, while powered by redstone, repeatedly attacks any living entity standing in the space in front of it. Right-click it with a sword or axe to mount it as the Striker's weapon; it fights using that weapon's attack damage, attack speed and enchantments (e.g. Sharpness), and wears it down with use, same as the Drill does with its tool. Works bare-handed if no weapon is mounted, just noticeably weaker. Also outputs a redstone signal based on the mounted weapon's remaining durability. Handy for redstone-triggered defenses, farms, or automated combat trials.
* **Placer** — Loaded with block items, it places one block into the space in front of it whenever it receives a redstone pulse. If the target space can't be filled, it drops the item instead of wasting it.
* **Extractor** — A directional item mover. On a redstone pulse, it pulls one stack of items from the inventory behind it into the inventory in front of it.
* **Chute** — A vertical item pipe. Stack Chutes on top of each other to form a shaft that carries items from the inventory at the top down to the inventory (or the floor) at the bottom. Powering any Chute in the stack pauses the whole column.
* **Breeze Collector** — While powered, it pulls in nearby dropped items (and certain entities/drops, with Sable/sub-level support) toward itself and feeds them into the inventory behind it — a redstone-controlled vacuum.
* **Copper Observer** — A directional entity sensor. It emits a short redstone pulse whenever something enters or leaves the space it's facing, similar in spirit to a vanilla Observer but for entities instead of blocks.

### Features
* **Advanced Automation:** Streamline your resource processing, defense and item management with compact technical blocks.
* **Compact Efficiency:** Replace bulky vanilla contraptions with smart, single-block logic.
* **Tool & Weapon Aware:** The Drill and Striker actually use the stats and enchantments of the tool/weapon you feed them, and report their durability over redstone.
* **Modpack Ready:** Fully optimized to easily integrate into technical modpacks and custom survival playthroughs.

### Requirements
* **Minecraft Version:** 1.21.1
* **Mod Loader:** NeoForge
* **Dependencies:** None
* **Compatibility:** Sable (optional)

### License
Redwork is open-source under the **MIT License**, and heavily based on and inspired by [Autowork](https://github.com/Ict00/autowork) by Ict00.

---

# Redwork (Русский)

**Redwork** — мод для Minecraft (NeoForge), добавляющий компактный набор автоматики на редстоуне. Каждый блок — отдельная "машина" в один блок: без громоздких мультиблоков (кроме Желобов, которые сами складываются в шахту) и без отдельного источника питания — просто ставите блок, подключаете редстоун и инвентари рядом, и он делает своё дело.

### Блоки

* **Бур (Drill)** — Смотрит в выбранную сторону и, пока подаётся сигнал редстоуна, добывает блок прямо перед собой. ПКМ с киркой, топором, лопатой или мотыгой — и инструмент встанет в Бур: он копает с учётом характеристик инструмента (скорость, Эффективность, Прочность) и постепенно изнашивает его, как это делал бы игрок, а всё добытое складывает в собственный инвентарь. Отдаёт редстоун-сигнал в зависимости от оставшейся прочности инструмента — удобно следить, когда пора его менять.
* **Ударник (Striker)** — *Новый блок!* Боевой напарник Бура. Смотрит в выбранную сторону и, пока подаётся сигнал редстоуна, повторно атакует любое живое существо, оказавшееся в пространстве перед ним. ПКМ с мечом или топором — оружие встанет в Ударник, и он будет драться с учётом урона, скорости атаки и зачарований (например, Остроты) этого оружия, постепенно изнашивая его — точно так же, как Бур обращается с инструментом. Без оружия бьёт голыми руками, но заметно слабее. Тоже отдаёт редстоун-сигнал по прочности установленного оружия. Полезен для автоматической защиты, ферм мобов или тренировочных арен на редстоуне.
* **Установщик (Placer)** — Загрузите в него предметы блоков — по импульсу редстоуна он поставит один блок в пространство перед собой. Если поставить некуда, предмет просто выбросится, а не пропадёт впустую.
* **Извлекатель (Extractor)** — Направленный переносчик предметов: по импульсу редстоуна забирает один стак предметов из инвентаря позади себя и кладёт в инвентарь перед собой.
* **Желоб (Chute)** — Вертикальная труба для предметов. Ставьте желоба друг на друга — получится шахта, которая переносит предметы из инвентаря наверху в инвентарь (или просто на землю) внизу. Подача сигнала на любой желоб в шахте останавливает всю колонну.
* **Сборщик вихря (Breeze Collector)** — Пока подан сигнал, притягивает к себе ближайшие выпавшие предметы (и некоторые сущности/дропы, с поддержкой саб-уровней Sable) и отправляет их в инвентарь позади себя — редстоун-управляемый пылесос.
* **Медный наблюдатель (Copper Observer)** — Направленный датчик существ. Выдаёт короткий импульс редстоуна каждый раз, когда кто-то входит в пространство перед ним или покидает его — как ванильный Наблюдатель, только реагирует на существ, а не на блоки.

### Особенности
* **Продвинутая автоматика:** обработка ресурсов, защита и логистика предметов без лишних построек.
* **Компактность:** один блок вместо громоздких ванильных конструкций.
* **Учитывает инструменты и оружие:** Бур и Ударник реально используют характеристики и зачарования того, что вы в них вставили, и сообщают об их износе по редстоуну.
* **Готово для сборок:** легко встраивается в технические модпаки и обычные survival-миры.

### Требования
* **Версия Minecraft:** 1.21.1
* **Загрузчик:** NeoForge
* **Зависимости:** нет
* **Совместимость:** Sable (опционально)

### Лицензия
Redwork — проект с открытым исходным кодом под лицензией **MIT**, во многом основанный на идеях и коде мода [Autowork](https://github.com/Ict00/autowork) от Ict00.
