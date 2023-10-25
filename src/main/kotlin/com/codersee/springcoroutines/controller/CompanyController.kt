package com.codersee.springcoroutines.controller

import com.codersee.springcoroutines.dto.CompanyRequest
import com.codersee.springcoroutines.dto.CompanyResponse
import com.codersee.springcoroutines.dto.UserResponse
import com.codersee.springcoroutines.model.Company
import com.codersee.springcoroutines.model.User
import com.codersee.springcoroutines.service.CompanyService
import com.codersee.springcoroutines.service.UserService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/api/companies")
class CompanyController(
    private val companyService: CompanyService,
    private val userService: UserService
) {
    @PostMapping
    suspend fun createCompany(@RequestBody companyRequest: CompanyRequest): CompanyResponse =
        companyService.saveCompany(
            company = companyRequest.toModel()
        )
            ?.toResponse()
            ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR)

    @GetMapping
    suspend fun findCompany(
        @RequestParam("name", required = false) name: String?
    ): Flow<CompanyResponse> {
        val companies = name?.let { companyService.findByName(it) } ?: companyService.findAll()

        return companies
            .map { company ->
                company.toResponse(
                    users = findCompanyUsers(company)
                )
            }
    }

    @GetMapping("/{id}")
    suspend fun findById(@PathVariable id: Long): CompanyResponse =
        companyService.findById(id)
            ?.let { company ->
                company.toResponse(
                    users = findCompanyUsers(company)
                )
            }
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND)

    @DeleteMapping("/{id}")
    suspend fun deleteById(@PathVariable id: Long) {
        companyService.deleteCompanyId(id)
    }

    @PutMapping("/{id}")
    suspend fun updateById(@PathVariable id: Long, @RequestBody companyRequest: CompanyRequest): CompanyResponse =
        companyService.updateCompanyById(id, companyRequest.toModel())
            .let { company ->
                company.toResponse(
                    users = findCompanyUsers(company)
                )
            }


    private suspend fun findCompanyUsers(company: Company): List<User> =
        userService.findByCompanyId(company.id!!)
            .toList()

}


private fun CompanyRequest.toModel(): Company =
    Company(
        name = this.name,
        address = this.address
    )

fun Company.toResponse(users: List<User> = emptyList()): CompanyResponse =
    CompanyResponse(
        id = this.id!!,
        name = this.name,
        address = this.address,
        users = users.map { user -> user.toResponse() }

    )