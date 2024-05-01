package woowacourse.movie.presentation.view.reservation.detail

import woowacourse.movie.data.repository.MovieRepositoryImpl
import woowacourse.movie.data.repository.ReservationMovieInfoRepositoryImpl
import woowacourse.movie.domain.model.Movie
import woowacourse.movie.domain.model.reservation.ReservationCount
import woowacourse.movie.domain.model.reservation.ReservationMovieInfo
import woowacourse.movie.presentation.repository.MovieRepository
import woowacourse.movie.presentation.repository.ReservationMovieInfoRepository
import woowacourse.movie.presentation.uimodel.MovieUiModel
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class MovieDetailPresenterImpl(
    val movieId: Int,
    private val movieRepository: MovieRepository = MovieRepositoryImpl,
) : MovieDetailContract.Presenter {
    private var view: MovieDetailContract.View? = null
    private val movie: Movie = movieRepository.findMovieById(movieId)
    private val reservationMovieInfo: ReservationMovieInfo = setScreeningMovieInfo()
    private val reservationCount: ReservationCount = ReservationCount()
    private val reservationMovieInfoRepository: ReservationMovieInfoRepository =
        ReservationMovieInfoRepositoryImpl

    private fun setScreeningMovieInfo(): ReservationMovieInfo {
        return ReservationMovieInfo(movie.title, movie.screeningInfo)
    }

    override fun attachView(view: MovieDetailContract.View) {
        this.view = view
        onViewSetUp()
    }

    override fun detachView() {
        this.view = null
    }

    override fun onViewSetUp() {
        view?.showMovieDetail(MovieUiModel(movie))
        loadScreeningDates(movieId)
    }

    override fun loadScreeningDates(movieId: Int) {
        val dates =
            movieRepository.getScreeningDateInfo(movieId).map { date ->
                date.format(DEFAULT_DATE_FORMAT)
            }
        val times = getScreeningTimeSchedule(reservationMovieInfo.dateTime.screeningDate.isWeekend())
        view?.setScreeningDatesAndTimes(dates, times, DEFAULT_DATA_INDEX)
    }

    override fun loadScreeningTimes(isWeekend: Boolean) {
        val times = getScreeningTimeSchedule(isWeekend)
        view?.updateScreeningTimes(times, DEFAULT_DATA_INDEX)
    }

    override fun selectDate(date: String) {
        val screeningDate = LocalDate.parse(date, DEFAULT_DATE_FORMAT)
        reservationMovieInfo.changeDate(
            year = screeningDate.year,
            month = screeningDate.monthValue,
            day = screeningDate.dayOfMonth,
        )
        loadScreeningTimes(reservationMovieInfo.dateTime.screeningDate.isWeekend())
    }

    override fun selectTime(time: String) {
        val screeningTime = LocalTime.parse(time)
        reservationMovieInfo.changeTime(
            hour = screeningTime.hour,
            minute = screeningTime.minute,
        )
    }

    override fun minusReservationCount() {
        reservationCount.minusCount()
        view?.showReservationCount(reservationCount.count)
    }

    override fun plusReservationCount() {
        reservationCount.plusCount()
        view?.showReservationCount(reservationCount.count)
    }

    override fun initReservationCount(count: Int) {
        reservationCount.initCount(count)
        view?.showReservationCount(reservationCount.count)
    }

    override fun onReserveButtonClicked() {
        reservationMovieInfoRepository.saveMovieInfo(reservationMovieInfo)
        view?.moveToSeatSelection(reservationCount.count, reservationMovieInfo.title)
    }

    private fun getScreeningTimeSchedule(isWeekend: Boolean): List<String> {
        val times =
            movieRepository.getScreeningTimeInfo(isWeekend).map { time ->
                time.format(DateTimeFormatter.ofPattern(DEFAULT_TIME_FORMAT))
            }
        return times
    }

    companion object {
        val DEFAULT_DATE_FORMAT: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE
        const val DEFAULT_TIME_FORMAT = "HH:mm"
        const val DEFAULT_DATA_INDEX = 0
    }
}