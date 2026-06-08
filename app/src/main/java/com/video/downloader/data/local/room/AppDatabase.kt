package com.video.downloader.data.local.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.video.downloader.data.local.room.vault.VaultFileDao
import com.video.downloader.data.local.room.vault.VaultFileEntity

@Database(
    entities = [
        VaultFileEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun vaultFileDao(): VaultFileDao

    companion object {

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                rebuildVaultFilesTable(db)
            }
        }

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                rebuildVaultFilesTable(db)
            }
        }

        private fun rebuildVaultFilesTable(db: SupportSQLiteDatabase) {
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS vault_files_new (
                    id TEXT NOT NULL PRIMARY KEY,
                    originalUri TEXT NOT NULL DEFAULT '',
                    originalFileName TEXT NOT NULL DEFAULT '',
                    mimeType TEXT NOT NULL DEFAULT '',
                    fileType TEXT NOT NULL DEFAULT 'VIDEO',
                    originalSizeBytes INTEGER NOT NULL DEFAULT 0,
                    encryptedFilePath TEXT NOT NULL DEFAULT '',
                    thumbnailPath TEXT,
                    ivBase64 TEXT NOT NULL DEFAULT '',
                    durationMillis INTEGER NOT NULL DEFAULT 0,
                    createdAtMillis INTEGER NOT NULL DEFAULT 0,
                    originalDeleteStatus TEXT NOT NULL DEFAULT 'DELETE_FAILED'
                )
                """.trimIndent()
            )

            val tableExists = doesTableExist(db, "vault_files")

            if (tableExists) {
                val columns = getColumns(db, "vault_files")

                fun columnOrDefault(
                    column: String,
                    defaultValue: String
                ): String {
                    return if (columns.contains(column)) {
                        column
                    } else {
                        defaultValue
                    }
                }

                db.execSQL(
                    """
                    INSERT OR REPLACE INTO vault_files_new (
                        id,
                        originalUri,
                        originalFileName,
                        mimeType,
                        fileType,
                        originalSizeBytes,
                        encryptedFilePath,
                        thumbnailPath,
                        ivBase64,
                        durationMillis,
                        createdAtMillis,
                        originalDeleteStatus
                    )
                    SELECT
                        ${columnOrDefault("id", "hex(randomblob(16))")},
                        ${columnOrDefault("originalUri", "''")},
                        ${columnOrDefault("originalFileName", "'Vault File'")},
                        ${columnOrDefault("mimeType", "''")},
                        ${columnOrDefault("fileType", "'VIDEO'")},
                        ${columnOrDefault("originalSizeBytes", "0")},
                        ${columnOrDefault("encryptedFilePath", "''")},
                        ${columnOrDefault("thumbnailPath", "NULL")},
                        ${columnOrDefault("ivBase64", "''")},
                        ${columnOrDefault("durationMillis", "0")},
                        ${columnOrDefault("createdAtMillis", "0")},
                        ${columnOrDefault("originalDeleteStatus", "'DELETE_FAILED'")}
                    FROM vault_files
                    """.trimIndent()
                )

                db.execSQL("DROP TABLE vault_files")
            }

            db.execSQL("ALTER TABLE vault_files_new RENAME TO vault_files")
        }

        private fun doesTableExist(
            db: SupportSQLiteDatabase,
            tableName: String
        ): Boolean {
            db.query(
                """
                SELECT name FROM sqlite_master 
                WHERE type='table' AND name=?
                """.trimIndent(),
                arrayOf(tableName)
            ).use { cursor ->
                return cursor.moveToFirst()
            }
        }

        private fun getColumns(
            db: SupportSQLiteDatabase,
            tableName: String
        ): Set<String> {
            val result = mutableSetOf<String>()

            db.query("PRAGMA table_info($tableName)").use { cursor ->
                val nameIndex = cursor.getColumnIndex("name")

                while (cursor.moveToNext()) {
                    if (nameIndex != -1) {
                        result.add(cursor.getString(nameIndex))
                    }
                }
            }

            return result
        }
    }
}