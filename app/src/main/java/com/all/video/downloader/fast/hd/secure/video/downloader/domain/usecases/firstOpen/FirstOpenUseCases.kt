package com.all.video.downloader.fast.hd.secure.video.downloader.domain.usecases.firstOpen

import com.all.video.downloader.fast.hd.secure.video.downloader.domain.repository.firstOpen.FirstOpenRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

data class FirstOpenUseCases @Inject constructor(
    val isOnBoardingCompletedUseCase: IsOnBoardingCompletedUseCase,
    val setOnBoardingCompletedFlagUseCase: SetOnBoardingCompletedFlagUseCase
)


class IsOnBoardingCompletedUseCase @Inject constructor(
    private val repository: FirstOpenRepository
){
    operator fun invoke(): Flow<Boolean>{
        return repository.isOnBoardingCompleted()
    }
}
class SetOnBoardingCompletedFlagUseCase @Inject constructor(
    private val repository: FirstOpenRepository
){
    suspend operator fun invoke(){
        return repository.setOnBoardingCompletedFlag()
    }
}
