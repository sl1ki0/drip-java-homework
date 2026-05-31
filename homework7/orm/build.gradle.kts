plugins {
    `java-library`
}

group = "orm"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    // api, чтобы H2 был доступен демо-проектам (DBConfig использует драйвер H2)
    api("com.h2database:h2:2.2.224")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}
