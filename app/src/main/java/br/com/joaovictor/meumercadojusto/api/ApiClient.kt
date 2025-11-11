package br.com.joaovictor.meumercadojusto.api

import br.com.joaovictor.meumercadojusto.BuildConfig
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {
    // URL base da API Economiza Alagoas (conforme documentação)
    // Usar lazy para evitar acesso prematuro ao BuildConfig
    private val BASE_URL: String by lazy {
        try {
            BuildConfig.API_GOVERNO_URL.ifBlank { 
                "http://api.sefaz.al.gov.br/sfz-economiza-alagoas-api/api/public/"
            }
        } catch (e: Exception) {
            android.util.Log.w("ApiClient", "BuildConfig.API_GOVERNO_URL não disponível: ${e.message}")
            "http://api.sefaz.al.gov.br/sfz-economiza-alagoas-api/api/public/"
        }
    }
    
    private val APP_TOKEN: String by lazy {
        try {
            BuildConfig.API_GOVERNO_KEY
        } catch (e: Exception) {
            android.util.Log.w("ApiClient", "BuildConfig.API_GOVERNO_KEY não disponível: ${e.message}")
            ""
        }
    }
    
    // Interceptor para adicionar AppToken no header (conforme documentação)
    // Interceptors e clientes criados com lazy para evitar inicialização prematura
    private val appTokenInterceptor: Interceptor by lazy {
        Interceptor { chain ->
            val original = chain.request()
            val requestBuilder = original.newBuilder()
            
            // Adicionar headers necessários
            requestBuilder.addHeader("Content-Type", "application/json")
            requestBuilder.addHeader("Accept", "application/json")
            
            // Adicionar AppToken no header (conforme documentação - seção 4)
            val token = APP_TOKEN
            if (token.isNotBlank()) {
                requestBuilder.addHeader("AppToken", token)
                android.util.Log.d("ApiClient", "AppToken adicionado ao header")
            } else {
                android.util.Log.w("ApiClient", "AppToken vazio - requisição pode falhar")
            }
            
            chain.proceed(requestBuilder.build())
        }
    }
    
    private val loggingInterceptor: HttpLoggingInterceptor by lazy {
        HttpLoggingInterceptor().apply {
            level = try {
                if (BuildConfig.DEBUG) {
                    HttpLoggingInterceptor.Level.BODY
                } else {
                    HttpLoggingInterceptor.Level.NONE
                }
            } catch (e: Exception) {
                HttpLoggingInterceptor.Level.NONE
            }
        }
    }
    
    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(appTokenInterceptor)
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }
    
    // Gson configurado para omitir campos null (requisito da API)
    // Por padrão, o Gson NÃO serializa campos null, então não precisamos chamar serializeNulls()
    // IMPORTANTE: codigoIBGE agora é Int (conforme API Python que funciona)
    private val gson: Gson by lazy {
        GsonBuilder()
            .setLenient() // Permitir JSON mais flexível
            .create()
    }
    
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }
    
    val apiGovernoService: ApiGovernoService by lazy {
        retrofit.create(ApiGovernoService::class.java)
    }
    
    val economizaAlagoasService: EconomizaAlagoasService by lazy {
        retrofit.create(EconomizaAlagoasService::class.java)
    }
    
    /**
     * Verifica se a API está configurada
     */
    fun isConfigured(): Boolean {
        return try {
            BASE_URL.isNotBlank() && APP_TOKEN.isNotBlank()
        } catch (e: Exception) {
            false
        }
    }
}

