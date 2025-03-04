package com.example.thinkr.data.repositories.user

import com.example.thinkr.data.models.User

class UserRepository : IUserRepository {
    private var _signedInUser: User? = null
    override fun setUser(user: User) {
        _signedInUser = user
    }

    override fun getUser(): User? {
        if (_signedInUser == null) {
            return null
        }
        return User(
            email = _signedInUser!!.email,
            name = _signedInUser!!.name,
            googleId = _signedInUser!!.googleId,
            subscribed = _signedInUser!!.subscribed
        )
    }

    override fun subscribeUser() {
        _signedInUser = _signedInUser?.copy(subscribed = true)
    }

    override fun delUser() {
        _signedInUser = null
    }
}
