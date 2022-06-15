package com.ntikhoa.ocrreceipt.business.datasource.network.auth

import com.ntikhoa.ocrreceipt.business.datasource.network.GenericResponse
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface AuthService {

    @POST("api/v1/auth/login")
    @FormUrlEncoded
    suspend fun login(
        @Field("username") username: String,
        @Field("password") password: String
    ): GenericResponse<LoginResponse>
}