package com.example.thinkr.data.repositories.user

import com.example.thinkr.data.models.User

/**
 * Interface for managing user authentication and profile data.
 *
 * This repository interface defines the contract for user-related operations such as
 * retrieving the currently authenticated user, managing authentication state, and
 * handling subscription status.
 */
interface IUserRepository {
    /**
     * Sets the currently authenticated user.
     *
     * @param user The user to be stored as the currently authenticated user.
     */
    fun setUser(user: User)

    /**
     * Retrieves the currently authenticated user.
     *
     * @return A copy of the current user object, or null if no user is authenticated.
     */
    fun getUser(): User?

    /**
     * Marks the current user as a subscribed/premium user.
     */
    fun subscribeUser()

    /**
     * Removes the current user and marks the session as signed out.
     */
    fun delUser()

    /**
     * Checks if the user has explicitly signed out.
     *
     * @return True if the user has signed out, false otherwise.
     */
    fun isSignedOut(): Boolean
}
