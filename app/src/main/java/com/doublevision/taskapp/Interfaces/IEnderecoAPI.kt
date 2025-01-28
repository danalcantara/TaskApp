package com.doublevision.taskapp.Interfaces

import com.doublevision.taskapp.Model.Endereco
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface IEnderecoAPI {
    @GET("{cep}/json/")
    suspend fun getEndereco(
@Path("cep") cep:String
    ): Response<Endereco>


}