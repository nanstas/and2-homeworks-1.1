package ru.netology.nmedia.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import ru.netology.nmedia.repository.PostRepository
import javax.inject.Inject
import javax.inject.Singleton

class RemovePostWorker(
    applicationContext: Context,
    params: WorkerParameters,
    private val repository: PostRepository,
) : CoroutineWorker(applicationContext, params) {
    companion object {
        const val postKey = "ru.netology.work.RemovePostWorker"
    }

    override suspend fun doWork(): Result {
        val id = inputData.getLong(postKey, 0L)
        if (id == 0L) {
            return Result.failure()
        }
        return try {
            repository.removeById(id)
            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.retry()
        }
    }
}

@Singleton
class RemovePostsWorkerFactory @Inject constructor(
    private val repository: PostRepository,
) : WorkerFactory() {
    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? = when (workerClassName) {
        RemovePostWorker::class.java.name ->
            RemovePostWorker(appContext, workerParameters, repository)
        else ->
            null
    }
}