package xyz.saarthakdevelopsstuff.stock_dashboard_api.service

import jakarta.transaction.Transactional
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import xyz.saarthakdevelopsstuff.stock_dashboard_api.beans.repositories.WatchListRepository
import xyz.saarthakdevelopsstuff.stock_dashboard_api.entities.WatchList
import xyz.saarthakdevelopsstuff.stock_dashboard_api.exceptions.WatchListException
import xyz.saarthakdevelopsstuff.stock_dashboard_api.exceptions.WatchListExceptionErrorCode

@Service
class WatchListServiceV1(
    private val watchListRepository: WatchListRepository
) {
    fun createWatchList() {

    }
    // I'm working on this, ignore it
    @Transactional
    fun updateWatchList(id: Long, username: String) {
        val watchList = getWatchList(id)
        val createdByUsername = watchListRepository.findCreatedByByWatchListId(id)
        if ((createdByUsername ?: "") != username) {
            throw WatchListException(WatchListExceptionErrorCode.UNAUTHORIZED, "Not authorized for this action")
        }
        

    }


    fun getWatchList(id: Long) : WatchList {
        val watchList = watchListRepository.findByIdOrNull(id)
        return watchList ?:  throw WatchListException(WatchListExceptionErrorCode.NOT_FOUND, "This watch list doesn't exists")
    }
}