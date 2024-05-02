package woowacourse.movie.presentation.view.bottom_navigation_bar

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import woowacourse.movie.R

class NavigationActivity : AppCompatActivity() {
    private val bottomNavigationView: BottomNavigationView by lazy {
        findViewById(R.id.navigationView)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_navigation)
        setBottomNavigationView()
        if (savedInstanceState == null) {
            bottomNavigationView.selectedItemId = R.id.home_fragment
        }
    }

    private fun setBottomNavigationView() {
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home_fragment -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.selected_fragment, Home())
                        .addToBackStack(null)
                        .commit()
                    true
                }

                R.id.setting_fragment -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.selected_fragment, Setting())
                        .addToBackStack(null)
                        .commit()
                    true
                }

                R.id.reservation_list_fragment -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.selected_fragment, ReservationList())
                        .addToBackStack(null)
                        .commit()
                    true
                }

                else -> false
            }
        }
    }
}