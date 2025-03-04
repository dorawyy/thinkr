package com.example.thinkr.data.repositories.user

import com.example.thinkr.data.models.User

interface IUserRepository {
    fun setUser(user: User)
    fun getUser(): User?
    fun delUser()
    fun subscribeUser()
}