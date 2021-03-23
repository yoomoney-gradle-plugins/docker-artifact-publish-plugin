package ru.yoomoney.gradle.plugins.docker.publish

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.containsString
import org.junit.Test

/**
 * Plugin
 *
 * @author Ilya Doroshenko
 * @since 18.03.2021
 */
class PluginTest : AbstractPluginTest() {

    @Test
    fun `should successfully apply plugin and configure all required tasks`() {
        buildFile.appendText("""
        dockerArtifactPublish {
            artifactId = "test_artifact_id"
            groupId = "test_group_id"
            snapshotRegistry = "yoomoney-snapshots"
            releaseRegistry = "yoomoney-releases"
            username = "test"
            password = "test"
        }
        """)

        val result = runTasksSuccessfully("tasks")

        assertThat(result.output, containsString("dockerTagImageWithVersion"))
        assertThat(result.output, containsString("dockerTagImageWithLatest"))
        assertThat(result.output, containsString("dockerPushImage"))
        assertThat(result.output, containsString("publish"))
    }
}