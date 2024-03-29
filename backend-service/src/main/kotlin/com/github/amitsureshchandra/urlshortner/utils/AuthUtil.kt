package com.github.amitsureshchandra.urlshortner.utils

import com.github.amitsureshchandra.urlshortner.entity.User
import com.github.amitsureshchandra.urlshortner.exception.NotFoundException
import com.github.amitsureshchandra.urlshortner.exception.UnAuthException
import com.github.amitsureshchandra.urlshortner.repo.UserRepo
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service

@Service
class AuthUtil(val userRepo: UserRepo) {
    fun getAuthEmail(): String{
        if(!SecurityContextHolder.getContext().authentication.isAuthenticated) throw UnAuthException("Unauthorized");
        return SecurityContextHolder.getContext().authentication.principal.toString()
    }

    fun getAuthUser(): User {
        return userRepo.findByEmail(getAuthEmail()) ?: throw NotFoundException("user not found")
    }
}