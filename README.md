[![Build Status](https://travis-ci.com/yoomoney-gradle-plugins/docker-artifact-publish-plugin.svg?branch=master)](https://travis-ci.com/yoomoney-gradle-plugins/docker-artifact-publish-plugin)
[![codecov](https://codecov.io/gh/yoomoney-gradle-plugins/docker-artifact-publish-plugin/branch/master/graph/badge.svg)](https://codecov.io/gh/yoomoney-gradle-plugins/docker-artifact-publish-plugin)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

# docker-artifact-publish-plugin
Plugin that allows to publish local docker image to registry depending on gradle project version type.

## Features
In order to publish docker image to one of configured registries you should execute `publish` task.

Depending on a project version the plugin will publish a local image to one of configured registries:
* snapshotRegistry - for a version ends with `-SNAPSHOT`
* releaseRegistry - for all other versions

Also, the plugin stores published image id to `version.txt` file.

## Image name format
The plugin requires the name of a local image to be in the following format before publishing: `groupId`/`artifactId`:`project.version`.
Where `groupId` and `artifactId` are values from `dockerArtifactPublish`plugin extension.

## How to apply
In order to apply the plugin just add to `build.gradle`:

Using legacy plugin application:
```groovy
buildscript {
    repositories {
        maven { 
            url "https://plugins.gradle.org/m2/" 
        }
    }
    dependencies {
        classpath 'ru.yoomoney.gradle.plugins:docker-artifact-publish-plugin:1.+'
    }
}
apply plugin: 'ru.yoomoney.gradle.plugins.docker-artifact-publish-plugin'
```

Using the plugin DSL:
```groovy
plugins {
  id "ru.yoomoney.gradle.plugins.docker-artifact-publish-plugin" version "1.0.0"
}
```

## Configuration example
```groovy
dockerArtifactPublish {
    // Release (stable) registry URL needed to push images
    releaseRegistry = ""
    
    // Snapshot (testing) registry URL needed to push images
    snapshotRegistry = null
    
    // Registry username needed to push images
    username = null
    
    // Registry password needed to push images
    password = null
    
    // Maintainer email address
    email = null
    
    // Artifact name
    artifactId = null
    
    // Artifact group
    groupId = null
}
```
