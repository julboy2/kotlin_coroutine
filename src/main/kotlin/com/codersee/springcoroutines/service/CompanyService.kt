package com.codersee.springcoroutines.service

import com.codersee.springcoroutines.model.Company
import com.codersee.springcoroutines.repository.CompanyRepository
import kotlinx.coroutines.flow.Flow
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class CompanyService(
    private val companyRepository: CompanyRepository
) {

    // 값이 없으면 중지 하기위해 suspend 사용

    suspend fun saveCompany(company: Company): Company? =
        companyRepository.save(company)

    suspend fun findAll(): Flow<Company> =
        companyRepository.findAll()

    suspend fun findById(id: Long): Company? =
        companyRepository.findById(id)

    suspend fun findByName(name: String): Flow<Company> =
        companyRepository.findByNameContaining(name)

    suspend fun deleteCompanyId(id:Long){
        val foundCompany = companyRepository.findById(id)

        if (foundCompany == null){
            throw ResponseStatusException(HttpStatus.NOT_FOUND)
        }else {
            companyRepository.deleteById(id)
        }
    }

    suspend fun updateCompanyById(id: Long , companyRequest: Company): Company{
        val foundCompany = companyRepository.findById(id)

        return if (foundCompany == null){
            throw ResponseStatusException(HttpStatus.NOT_FOUND)
        }else {
            companyRepository.save(
                companyRequest.copy(id = foundCompany.id)
            )
        }
    }

}