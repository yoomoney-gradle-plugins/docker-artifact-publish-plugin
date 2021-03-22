package ru.yoomoney.gradle.plugins.docker.publish

import com.bmuschko.gradle.docker.DockerRemoteApiPlugin
import com.bmuschko.gradle.docker.tasks.image.DockerPushImage
import com.bmuschko.gradle.docker.tasks.image.DockerTagImage
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings
import org.gradle.api.Plugin
import org.gradle.api.Project
import java.nio.file.Paths

/**
 * Plugin to publish docker images
 *
 * @author Ilya Doroshenko
 * @since 18.03.2021
 */
class DockerArtifactPublishPlugin : Plugin<Project> {

    companion object {
        /**
         * Main extension name
         */
        const val extensionName: String = "dockerArtifactPublish"
    }

    override fun apply(project: Project) {
        project.pluginManager.apply(DockerRemoteApiPlugin::class.java)

        val extension = project.extensions.findByType(DockerArtifactPublishExtension::class.java)
        if (extension == null) {
            project.extensions.create(extensionName, DockerArtifactPublishExtension::class.java)
        }

        val publishTask = project.tasks.findByName("publish")
        if (publishTask == null) {
            project.tasks.create("publish") {
                it.dependsOn("build")

                it.group = "publication"
                it.description = "Publish artifact general task"
            }
        }

        project.afterEvaluate {
            configureTagTask(it)
            configurePublishTask(it)
            configureStoreVersion(it)
        }
    }

    /**
     * Retag local image in order to add registry url to imageId
     * [See issue](https://github.com/bmuschko/gradle-docker-plugin/issues/788)
     */
    private fun configureTagTask(project: Project) {
        val settings = project.extensions.getByType(DockerArtifactPublishExtension::class.java)
        val registryUrl = chooseRegistryUrl(project, settings)

        project.tasks.create("dockerTagImageWithVersion", DockerTagImage::class.java) {
            it.description = "Tag local image with current version for remote registry"
            it.group = "docker"

            it.imageId.set("${settings.groupId}/${settings.artifactId}:${project.version}")
            it.repository.set("$registryUrl/${settings.groupId}/${settings.artifactId}")
            it.tag.set("${project.version}")
        }

        project.tasks.create("dockerTagImageWithLatest", DockerTagImage::class.java) {
            it.description = "Tag local image with latest version for remote registry"
            it.group = "docker"

            it.imageId.set("${settings.groupId}/${settings.artifactId}:${project.version}")
            it.repository.set("$registryUrl/${settings.groupId}/${settings.artifactId}")
            it.tag.set("latest")
        }
    }

    private fun configurePublishTask(project: Project) {
        val settings = project.extensions.getByType(DockerArtifactPublishExtension::class.java)
        val pushImageTask = project.tasks.create("dockerPushImage", DockerPushImage::class.java) {
            it.description = "Push image to a remote registry"
            it.group = "publication"

            it.dependsOn("dockerTagImageWithVersion", "dockerTagImageWithLatest")

            val registryUrl = chooseRegistryUrl(project, settings)

            it.images.add("$registryUrl/${settings.groupId}/${settings.artifactId}:${project.version}")
            it.images.add("$registryUrl/${settings.groupId}/${settings.artifactId}:latest")

            it.registryCredentials.apply {
                url.set(registryUrl)
                username.set(settings.username)
                password.set(settings.password)
                email.set(settings.maintainerEmail)
            }
        }

        project.tasks.findByName("publish")!!.dependsOn(pushImageTask)
    }

    private fun configureStoreVersion(project: Project) {
        val settings = project.extensions.getByType(DockerArtifactPublishExtension::class.java)
        val registryUrl = chooseRegistryUrl(project, settings)
        val storeVersionTask = project.tasks.create("storeVersion") {
            it.description = "Generates file, which contains information about build version"
            it.doLast {
                val version = "$registryUrl/${settings.groupId}/${settings.artifactId}:${project.version}"
                storeVersionToFile(project, project.buildDir.absolutePath, version)
            }
        }
        project.tasks.getByName("publish").finalizedBy(storeVersionTask)
    }

    @SuppressFBWarnings("PATH_TRAVERSAL_IN")
    private fun storeVersionToFile(project: Project, versionDir: String, content: String) {
        Paths.get(versionDir).toFile().resolve("version.txt").let {
            it.writeText(content)
            project.logger.lifecycle("File with version generated successfully into $it")
        }
    }

    private fun chooseRegistryUrl(project: Project, settings: DockerArtifactPublishExtension): String? {
        return if (project.isSnapshot()) {
            settings.snapshotRegistry
        } else {
            settings.releaseRegistry
        }
    }

    private fun Project.isSnapshot() = project.version.toString().endsWith("-SNAPSHOT")
}