import androidx.test.rule.ActivityTestRule
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import androidx.test.espresso.*
import androidx.test.espresso.Espresso.*
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import nl.rvbsoftdev.curiosityreporting.R
import nl.rvbsoftdev.curiosityreporting.global.NavigationActivity

/** Some Espresso UI tests **/

@RunWith(AndroidJUnit4::class)
class UITests {

    @Rule
    @JvmField

    val app = ActivityTestRule(NavigationActivity::class.java)

    @Test
    fun goToExploreFragmentAndSelectRandomDate() {
        onView(withId(R.id.explore_fragment)).perform(click())
        onView(withId(R.id.select_random_date)).perform(click())
    }

    @Test
    fun goToExploreFragmentAndSelectTodayFrom2018() {
        onView(withId(R.id.explore_fragment)).perform(click())
        onView(withId(R.id.launch_date_picker_dialog)).perform(click())
        onView(withText("2018")).perform(click())
        onView(withText("OK")).perform(click())
    }

    @Test
    fun goToFactsFragment() {
        onView(withId(R.id.facts_fragment)).perform(click())
    }

    @Test
    fun goToFavoritesFragment() {
        onView(withId(R.id.favorites_fragment)).perform(click())
    }

    @Test
    fun goToMoreFragment() {
        onView(withId(R.id.more_fragment)).perform(click())
    }

    @Test
    fun goToMissionDetailFragment1andBack() {
        onView(withId(R.id.journey_cardview)).check(matches(isCompletelyDisplayed())).perform(click())
        Espresso.pressBack()
    }


    @Test
    fun goToMissionDetailFragment2andBack() {
        onView(withId(R.id.mars_cardview)).check(matches(isCompletelyDisplayed())).perform(click())
        Espresso.pressBack()
    }
}

