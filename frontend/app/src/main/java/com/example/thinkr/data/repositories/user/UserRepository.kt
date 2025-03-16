package com.example.thinkr.data.repositories.user

import com.example.thinkr.data.models.User

/**
 * Implementation of the user repository interface for managing user authentication state.
 *
 * This repository stores the current authenticated user in memory and provides
 * methods to manipulate the user's state. It handles basic user operations such as
 * retrieving user details, updating subscription status, and managing sign-in/sign-out state.
 */
class UserRepository : IUserRepository {
    private var _signedInUser: User? = null
    private var _signedOut: Boolean = false

    /**
     * Sets the currently authenticated user.
     *
     * @param user The user to be stored as the currently authenticated user.
     */
    override fun setUser(user: User) {
        _signedInUser = user
    }

    /**
     * Retrieves the currently authenticated user.
     *
     * @return A copy of the current user object, or null if no user is authenticated.
     */
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

    /**
     * Marks the current user as a subscribed/premium user.
     */
    override fun subscribeUser() {
        _signedInUser = _signedInUser?.copy(subscribed = true)
    }

    /**
     * Removes the current user and marks the session as signed out.
     */
    override fun delUser() {
        _signedInUser = null
        _signedOut = true
    }

    /**
     * Checks if the user has explicitly signed out.
     *
     * @return True if the user has signed out, false otherwise.
     */
    override fun isSignedOut(): Boolean {
        return _signedOut
    }
}
