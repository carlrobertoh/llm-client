plugins {
    `java-library`
    `maven-publish`
    signing
    checkstyle
    alias(libs.plugins.publish.plugin)
}

group = "ee.carlrobert"
version = "0.8.9"

repositories {
    mavenCentral()
}

java {
    withJavadocJar()
    withSourcesJar()
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

checkstyle {
    toolVersion = libs.versions.checkstyle.get()
}

dependencies {
    api(platform(libs.okhttp.bom))
    api("com.squareup.okhttp3:okhttp")
    api("com.squareup.okhttp3:okhttp-sse")
    api("com.squareup.okhttp3:logging-interceptor")

    implementation(platform(libs.slf4j.bom))
    implementation("org.slf4j:slf4j-api")
    implementation("org.slf4j:slf4j-simple")
    implementation(platform(libs.jackson.bom))
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jdk8")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")

    testImplementation(platform(libs.junit.bom))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation(libs.commons.io)
    testImplementation(libs.assertj.core)
    testImplementation(libs.awaitility)
}

nexusPublishing {
    repositories {
        sonatype {
            nexusUrl.set(uri("https://s01.oss.sonatype.org/service/local/"))
            snapshotRepositoryUrl.set(uri("https://s01.oss.sonatype.org/content/repositories/snapshots/"))
        }
    }
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            versionMapping {
                usage("java-api") {
                    fromResolutionOf("runtimeClasspath")
                }
                usage("java-runtime") {
                    fromResolutionResult()
                }
            }
            pom {
                name.set("LLM Client")
                description.set("Java http client wrapped around the OkHttp3 library")
                url.set("https://github.com/carlrobertoh/llm-client")
                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }
                developers {
                    developer {
                        id.set("linnupuu")
                        name.set("Carl-Robert Linnupuu")
                        email.set("carlrobertoh@gmail.com")
                    }
                }
                scm {
                    url.set("https://github.com/carlrobertoh/llm-client")
                    connection.set("scm:git://github.com/carlrobertoh/llm-client.git")
                    developerConnection.set("scm:git://github.com/carlrobertoh/llm-client.git")
                }
            }
        }
    }
}

signing {
    val signingKey = (findProperty("signingKey") ?: "") as String
    val signingPassword = (findProperty("signingPassword") ?: "") as String
    useInMemoryPgpKeys(signingKey, signingPassword)
    sign(publishing.publications["mavenJava"])
}

tasks {
    test {
        useJUnitPlatform()
        testLogging {
            events("passed", "skipped", "failed")
        }
    }
    jar {
        from(sourceSets["main"].output)
        from(sourceSets["test"].output) {
            include("**/http/**")
            include("**/mixin/**")
            include("**/util/**")
        }
    }
}
