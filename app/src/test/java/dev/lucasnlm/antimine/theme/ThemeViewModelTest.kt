package dev.lucasnlm.antimine.theme

import dev.lucasnlm.antimine.IntentViewModelTest
import dev.lucasnlm.antimine.common.R
import dev.lucasnlm.antimine.core.analytics.IAnalyticsManager
import dev.lucasnlm.antimine.core.preferences.IPreferencesRepository
import dev.lucasnlm.antimine.core.themes.model.AppTheme
import dev.lucasnlm.antimine.core.themes.model.AreaPalette
import dev.lucasnlm.antimine.core.themes.model.Assets
import dev.lucasnlm.antimine.core.themes.repository.IThemeRepository
import dev.lucasnlm.antimine.theme.viewmodel.ThemeEvent
import dev.lucasnlm.antimine.theme.viewmodel.ThemeState
import dev.lucasnlm.antimine.theme.viewmodel.ThemeViewModel
import dev.lucasnlm.external.IBillingManager
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Test

class ThemeViewModelTest : IntentViewModelTest() {
    private val lightTheme = AppTheme(
        id = 1L,
        theme = R.style.CustomLightTheme,
        themeNoActionBar = R.style.CustomLightTheme_NoActionBar,
        palette = AreaPalette(
            border = 0x424242,
            background = 0xFFFFFF,
            covered = 0x424242,
            coveredOdd = 0x424242,
            uncovered = 0xd5d2cc,
            uncoveredOdd = 0xd5d2cc,
            minesAround1 = 0x527F8D,
            minesAround2 = 0x2B8D43,
            minesAround3 = 0xE65100,
            minesAround4 = 0x20A5f7,
            minesAround5 = 0xED1C24,
            minesAround6 = 0xFFC107,
            minesAround7 = 0x66126B,
            minesAround8 = 0x000000,
            highlight = 0x212121,
            focus = 0xD32F2F
        ),
        assets = Assets(
            wrongFlag = R.drawable.red_flag,
            flag = R.drawable.flag,
            questionMark = R.drawable.question,
            toolbarMine = R.drawable.mine,
            mine = R.drawable.mine,
            mineExploded = R.drawable.mine_exploded_red,
            mineLow = R.drawable.mine_low
        )
    )

    private val darkTheme = AppTheme(
        id = 2L,
        theme = R.style.CustomDarkTheme,
        themeNoActionBar = R.style.CustomDarkTheme_NoActionBar,
        palette = AreaPalette(
            border = 0x171717,
            background = 0x212121,
            covered = 0x171717,
            coveredOdd = 0x171717,
            uncovered = 0x424242,
            uncoveredOdd = 0x424242,
            minesAround1 = 0xd5d2cc,
            minesAround2 = 0xd5d2cc,
            minesAround3 = 0xd5d2cc,
            minesAround4 = 0xd5d2cc,
            minesAround5 = 0xd5d2cc,
            minesAround6 = 0xd5d2cc,
            minesAround7 = 0xd5d2cc,
            minesAround8 = 0xd5d2cc,
            highlight = 0xFFFFFF,
            focus = 0xFFFFFF
        ),
        assets = Assets(
            wrongFlag = R.drawable.flag,
            flag = R.drawable.flag,
            questionMark = R.drawable.question,
            toolbarMine = R.drawable.mine_low,
            mine = R.drawable.mine,
            mineExploded = R.drawable.mine_exploded_white,
            mineLow = R.drawable.mine_low
        )
    )

    private val gardenTheme = AppTheme(
        id = 3L,
        theme = R.style.CustomGardenTheme,
        themeNoActionBar = R.style.CustomGardenTheme_NoActionBar,
        palette = AreaPalette(
            border = 0x171717,
            background = 0xefebe9,
            covered = 0x689f38,
            coveredOdd = 0x558b2f,
            uncovered = 0xefebe9,
            uncoveredOdd = 0xd7ccc8,
            minesAround1 = 0x527F8D,
            minesAround2 = 0x2B8D43,
            minesAround3 = 0xE65100,
            minesAround4 = 0x20A5f7,
            minesAround5 = 0xED1C24,
            minesAround6 = 0xFFC107,
            minesAround7 = 0x66126B,
            minesAround8 = 0x000000,
            highlight = 0xFFFFFF,
            focus = 0xFFFFFF
        ),
        assets = Assets(
            wrongFlag = R.drawable.flag,
            flag = R.drawable.flag,
            questionMark = R.drawable.question,
            toolbarMine = R.drawable.mine_low,
            mine = R.drawable.mine,
            mineExploded = R.drawable.mine_exploded_white,
            mineLow = R.drawable.mine_low
        )
    )

    private val allThemes = listOf(
        lightTheme, darkTheme, gardenTheme
    )

    @Test
    fun testInitialValue() {
        val themeRepository = mockk<IThemeRepository> {
            every { getAllThemes() } returns allThemes
            every { getTheme() } returns gardenTheme
        }

        val billingManager = mockk<IBillingManager>()

        val preferencesRepository = mockk<IPreferencesRepository> { }

        val analyticsManager = mockk<IAnalyticsManager> { }

        val viewModel = ThemeViewModel(themeRepository, billingManager, preferencesRepository, analyticsManager)
        assertEquals(ThemeState(gardenTheme, allThemes), viewModel.singleState())
    }

    @Test
    fun testChangeValue() {
        val themeRepository = mockk<IThemeRepository> {
            every { getAllThemes() } returns allThemes
            every { getTheme() } returns gardenTheme
            every { setTheme(any()) } returns Unit
        }

        val billingManager = mockk<IBillingManager> {
            every { isEnabled() } returns false
        }

        val preferencesRepository = mockk<IPreferencesRepository> {
            every { areExtrasUnlocked() } returns true
        }

        val analyticsManager = mockk<IAnalyticsManager> {
            every { sentEvent(any()) } returns Unit
        }

        val state = ThemeViewModel(themeRepository, billingManager, preferencesRepository, analyticsManager).run {
            sendEvent(ThemeEvent.ChangeTheme(darkTheme))
            singleState()
        }
        assertEquals(ThemeState(darkTheme, allThemes), state)

        verify { themeRepository.setTheme(darkTheme) }
    }

    @Test
    fun testChangeValueWithoutExtras() {
        val themeRepository = mockk<IThemeRepository> {
            every { getAllThemes() } returns allThemes
            every { getTheme() } returns gardenTheme
            every { setTheme(any()) } returns Unit
        }

        val billingManager = mockk<IBillingManager>()

        val preferencesRepository = mockk<IPreferencesRepository> {
            every { areExtrasUnlocked() } returns false
        }

        val analyticsManager = mockk<IAnalyticsManager> {
            every { sentEvent(any()) } returns Unit
        }

        val state = ThemeViewModel(themeRepository, billingManager, preferencesRepository, analyticsManager).run {
            sendEvent(ThemeEvent.ChangeTheme(darkTheme))
            singleState()
        }
        assertEquals(ThemeState(gardenTheme, allThemes), state)
    }
}
