package com.arcadan.dodgetheenemies.data

class FirebaseArrayList<T> : ArrayList<T>() {
    fun addAndSync(element: T, listKey: String) {
        super.add(element)
        DatabaseManager.setValue(listKey, this as Any)
    }
}
