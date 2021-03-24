package com.arcadan.dodgetheenemies.models

import com.arcadan.dodgetheenemies.enums.Consumables

data class Consumable(val consumables: Consumables = Consumables.SOON, val size: Int = 0)
