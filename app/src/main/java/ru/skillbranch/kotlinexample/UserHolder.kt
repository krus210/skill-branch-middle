package ru.skillbranch.kotlinexample

import androidx.annotation.VisibleForTesting

object UserHolder {
    private val map = mutableMapOf<String, User>()

    fun registerUser(
        fullName: String,
        email: String,
        password: String
    ): User = User.makeUser(fullName, email = email, password = password)
        .also { user ->
            if (map.contains(user.login)) throw IllegalArgumentException("A user with this email already exists")
            else map[user.login] = user
        }

    fun registerUserByPhone(fullName: String, rawPhone: String): User =
        User.makeUser(fullName, phone = rawPhone).also { user ->
            when {
                map.contains(user.login) -> throw IllegalArgumentException("A user with this email already exists")
                user.login.first() != '+' || user.login.length != 12 -> throw IllegalArgumentException(
                    "Enter a valid phone number starting with a + and containing 11 digits"
                )
                else -> map[user.login] = user
            }
        }

    fun loginUser(login: String, password: String): String? =
        map[login.trim().replace("""[() -]""".toRegex(), "")]?.let {
            if (it.checkPassword(password)) it.userInfo
            else null
        }

    fun requestAccessCode(login: String) {
        map[login.trim().replace("""[() -]""".toRegex(), "")]?.let { user ->
            user.accessCode?.also {
                val newCode = user.generateAccessCode()
                user.changePassword(it, newCode)
                user.accessCode = newCode
            }
        }
    }

    @VisibleForTesting(otherwise = VisibleForTesting.NONE)
    fun clearHolder() {
        map.clear()
    }
}