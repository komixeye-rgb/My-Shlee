package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Shlee", appName)
  }

  @Test
  fun `verify study roadmap milestones generated for candidate levels`() {
    val viewModel = com.example.ui.StudentDashboardViewModel()
    val uceMilestones = viewModel.getMilestonesForLevel("UCE (Senior 4)")
    val pleMilestones = viewModel.getMilestonesForLevel("PLE (Primary 7)")
    val uaceMilestones = viewModel.getMilestonesForLevel("UACE (Senior 6)")

    org.junit.Assert.assertTrue(uceMilestones.isNotEmpty())
    org.junit.Assert.assertTrue(pleMilestones.isNotEmpty())
    org.junit.Assert.assertTrue(uaceMilestones.isNotEmpty())
    assertEquals(6, uceMilestones.size)
  }

  @Test
  fun `verify pomodoro session recording increments student focus sessions`() {
    val viewModel = com.example.ui.StudentDashboardViewModel()
    val initialSessions = viewModel.userProfile.value.focusSessionsCompleted
    viewModel.recordCompletedPomodoroSession(25)
    val updatedSessions = viewModel.userProfile.value.focusSessionsCompleted
    assertEquals(initialSessions + 1, updatedSessions)
  }

  @Test
  fun `verify candidate level update changes profile level successfully`() {
    val viewModel = com.example.ui.StudentDashboardViewModel()
    viewModel.updateCandidateLevel("PLE (Primary 7)")
    assertEquals("PLE (Primary 7)", viewModel.userProfile.value.candidateLevel)
  }
}
