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
    fun setUser(user: User)
    fun getUser(): User?
    fun delUser()
    fun subscribeUser()
    fun isSignedOut(): Boolean
}
