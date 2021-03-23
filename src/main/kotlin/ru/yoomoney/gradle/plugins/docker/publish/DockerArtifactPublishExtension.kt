package ru.yoomoney.gradle.plugins.docker.publish

/**
 * Publishing plugin general settings
 *
 * @author Ilya Doroshenko
 * @since 18.03.2021
 */
open class DockerArtifactPublishExtension {

    /**
     * Release (stable) registry URL needed to push images
     */
    var releaseRegistry: String? = null

    /**
     * Snapshot (testing) registry URL needed to push images
     */
    var snapshotRegistry: String? = null

    /**
     * Registry username needed to push images
     */
    var username: String? = null

    /**
     * Registry password needed to push images
     */
    var password: String? = null

    /**
     * Image maintainer email address
     */
    var maintainerEmail: String? = null

    /**
     * Artifact name
     */
    var artifactId: String? = null

    /**
     * Artifact group
     */
    var groupId: String? = null
}
