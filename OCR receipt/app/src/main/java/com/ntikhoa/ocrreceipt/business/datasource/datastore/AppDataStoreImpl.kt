package com.ntikhoa.ocrreceipt.business.datasource.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.ntikhoa.ocrreceipt.business.domain.utils.Constants
import kotlinx.coroutines.flow.first
import androidx.datastore.preferences.core.Preferences

class AppDataStoreImpl(
    private val context: Context
) : AppDataStore {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(Constants.DATASTORE_NAME)

    override suspend fun setValue(key: String, value: String) {
        context.dataStore.edit {
            it[stringPreferencesKey(key)] = value
        }
    }

    override suspend fun readValue(key: String): String {
        return context.dataStore.data.first()[stringPreferencesKey(key)]
            ?: Constants.DATASTORE_VALUE_NOT_FOUND
    }

    override suspend fun clear() {
        context.dataStore.edit {
            it.clear()
        }
    }
}
