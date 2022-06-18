package com.ntikhoa.ocrreceipt.business.datasource.network.auth

import com.ntikhoa.ocrreceipt.business.datasource.network.GenericResponse
import retrofit2.http.*

interface AuthService {

    @POST("api/v1/auth/login")
    @FormUrlEncoded
    suspend fun login(
        @Field("username") username: String,
        @Field("password") password: String
    ): GenericResponse<LoginResponse>

    @POST("api/v1/auth/auto_login")
    suspend fun autoLogin(
        @Header("Authorization") token: String
    ): GenericResponse<Any?>
}