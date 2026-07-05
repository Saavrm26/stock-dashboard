package xyz.saarthakdevelopsstuff.stock_dashboard_api.service

import jakarta.transaction.Transactional
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import xyz.saarthakdevelopsstuff.stock_dashboard_api.beans.mappers.WatchListMapper
import xyz.saarthakdevelopsstuff.stock_dashboard_api.beans.repositories.WatchListRepository
import xyz.saarthakdevelopsstuff.stock_dashboard_api.exceptions.WatchListException
import xyz.saarthakdevelopsstuff.stock_dashboard_api.exceptions.WatchListExceptionErrorCode
import xyz.saarthakdevelopsstuff.stock_dashboard_api.models.WatchListResponse

@Service
class WatchListServiceV1(
    private val watchListRepository: WatchListRepository,
    private val watchListMapper: WatchListMapper
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


    fun getWatchList(id: Long) : WatchListResponse {
        val watchList = watchListRepository.findByIdOrNull(id)
            ?: throw WatchListException(WatchListExceptionErrorCode.NOT_FOUND, "This watch list doesn't exists")
        return watchListMapper.toWatchListResponse(watchList)
    }
}