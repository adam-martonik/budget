import sun.jvmstat.monitor.MonitoredVmUtil.mainClass

plugins {
    java
    application
}

group = "org.example"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    // Bez Google Drive API - len lokálne ukladanie
}

application {
    mainClass.set("org.example.MainUI")
}

tasks.jar {
    manifest {
        attributes(
            "Main-Class" to "org.example.MainUI",
            "Implementation-Version" to archiveVersion
        )
    }

    from(sourceSets["main"].output)

    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}