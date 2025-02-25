package fr.isen.soubry.isensmartcompanion.data

import androidx.room.*

@Dao
interface InteractionDao {
    @Insert
    suspend fun insertInteraction(interaction: Interaction): Long // ✅ Retourne l'ID inséré

    @Query("SELECT * FROM interactions ORDER BY date DESC")
    suspend fun getAllInteractions(): List<Interaction> // ✅ Retourne l'historique complet

    @Delete
    suspend fun deleteInteraction(interaction: Interaction): Int // ✅ Supprime une interaction spécifique

    @Query("DELETE FROM interactions")
    suspend fun deleteAllInteractions(): Int // ✅ Supprime tout l'historique

    @Query("SELECT * FROM interactions WHERE question = :question ORDER BY date DESC LIMIT 1")
    suspend fun getLastInteraction(question: String): Interaction?
}
