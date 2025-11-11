package br.com.joaovictor.meumercadojusto.supabase

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.realtime.Realtime
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.auth.Auth
import br.com.joaovictor.meumercadojusto.BuildConfig

/**
 * Cliente Supabase para conexão com PostgreSQL
 * 
 * Configuração:
 * 1. Adicione no arquivo local.properties (na raiz do projeto):
 *    supabase.url=https://seu-projeto.supabase.co
 *    supabase.key=sua-chave-anon-key
 * 
 * 2. As credenciais serão carregadas automaticamente via BuildConfig
 */
object SupabaseClient {
    
    // Usar lazy para evitar acesso prematuro ao BuildConfig
    private val SUPABASE_URL: String by lazy {
        try {
            BuildConfig.SUPABASE_URL
        } catch (e: Exception) {
            android.util.Log.w("SupabaseClient", "BuildConfig.SUPABASE_URL não disponível: ${e.message}")
            ""
        }
    }
    
    private val SUPABASE_KEY: String by lazy {
        try {
            BuildConfig.SUPABASE_KEY
        } catch (e: Exception) {
            android.util.Log.w("SupabaseClient", "BuildConfig.SUPABASE_KEY não disponível: ${e.message}")
            ""
        }
    }
    
    /**
     * Cliente Supabase configurado
     * Retorna null se as credenciais não estiverem configuradas ou se houver erro
     * IMPORTANTE: Desabilitado temporariamente até resolver dependência do Ktor HttpTimeout
     */
    val client: SupabaseClient? by lazy {
        try {
            val url = SUPABASE_URL
            val key = SUPABASE_KEY
            
            if (url.isBlank() || key.isBlank()) {
                android.util.Log.d("SupabaseClient", "Supabase não configurado (URL ou KEY vazios)")
                return@lazy null
            }
            
            android.util.Log.d("SupabaseClient", "Inicializando cliente Supabase...")
            try {
                createSupabaseClient(
                    supabaseUrl = url,
                    supabaseKey = key
                ) {
                    install(Postgrest)
                    install(Realtime)
                    install(Storage)
                    install(Auth)
                }.also {
                    android.util.Log.d("SupabaseClient", "Cliente Supabase inicializado com sucesso")
                }
            } catch (e: NoClassDefFoundError) {
                // Erro específico de classe não encontrada (ex: HttpTimeout)
                android.util.Log.e("SupabaseClient", "Erro de dependência do Ktor: ${e.message}", e)
                android.util.Log.w("SupabaseClient", "Supabase desabilitado - dependência do Ktor faltando")
                null
            } catch (e: ClassNotFoundException) {
                // Erro específico de classe não encontrada
                android.util.Log.e("SupabaseClient", "Classe não encontrada: ${e.message}", e)
                android.util.Log.w("SupabaseClient", "Supabase desabilitado - classe faltando")
                null
            }
        } catch (e: Exception) {
            // Em caso de erro, retornar null para não quebrar o app
            android.util.Log.e("SupabaseClient", "Erro ao criar cliente Supabase: ${e.message}", e)
            null
        }
    }
    
    /**
     * Verifica se o Supabase está configurado
     */
    fun isConfigured(): Boolean {
        return try {
            SUPABASE_URL.isNotBlank() && SUPABASE_KEY.isNotBlank()
        } catch (e: Exception) {
            false
        }
    }
}
