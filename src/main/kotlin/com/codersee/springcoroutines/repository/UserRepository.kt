package com.codersee.springcoroutines.repository

import com.codersee.springcoroutines.model.User
import kotlinx.coroutines.flow.Flow
//import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface UserRepository: CoroutineCrudRepository<User, Long> {

    fun findByNameContaining(name: String): Flow<User>

    fun findByCompanyId(companyId: Long): Flow<User>

    @Query("select * from app_user where email = :email")
    fun byEmail(email: String): Flow<User>
}