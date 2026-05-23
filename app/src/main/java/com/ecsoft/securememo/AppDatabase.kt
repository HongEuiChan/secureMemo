package com.ecsoft.securememo

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [Note::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // 1. 새로운 구조의 임시 테이블 생성
                db.execSQL("""
                    CREATE TABLE notes_new (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        type TEXT NOT NULL,
                        title TEXT NOT NULL,
                        field1 TEXT,
                        field2 TEXT,
                        field3 TEXT,
                        field4 TEXT,
                        createdAt INTEGER NOT NULL
                    )
                """.trimIndent())

                // 2. 기존 데이터를 새 테이블로 이전 (기존 body -> field1, 기본 타입 -> 'NOTE')
                db.execSQL("""
                    INSERT INTO notes_new (id, type, title, field1, createdAt)
                    SELECT id, 'NOTE', title, body, createdAt FROM notes
                """.trimIndent())

                // 3. 이전 테이블 삭제
                db.execSQL("DROP TABLE notes")

                // 4. 새 테이블의 이름을 원래 이름으로 변경
                db.execSQL("ALTER TABLE notes_new RENAME TO notes")
            }
        }
    }
}
