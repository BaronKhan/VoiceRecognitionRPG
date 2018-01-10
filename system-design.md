System Design
========================

Sentence Structure
---------------------

Always imply 'I' as the subject. Therefore, only concerned with the main verb of the sentence, and the noun.

- For verbs, first use the semantic similarity. If > 0.7/1, then probably the same thing. If not, then check if any of the primitive verbs are in the word tree.

Battle Systems
---------------------

- Turn-based

### Actions

- Attack ("attack")
- Defend ("prevent")
- Heal ("better")

Items
---------

Types:
- Weapons
-- sharp
-- blunt
- Shields
- Healing Items
-- potion
-- hi potion
- Key Items


Inventory System
--------------------

- Just an array of all the items the user has.
- Items have properties. Items is an abstract class.
- Item has a type, assigned to enum.

Create a lookup-table, mapping items to verbs, and the functions that are called