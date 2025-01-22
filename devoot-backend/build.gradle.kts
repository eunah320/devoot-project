plugins {
	java
	id("org.springframework.boot") version "3.4.1"
	id("io.spring.dependency-management") version "1.1.7"
    id("org.ec4j.editorconfig") version "0.0.3"
    id("checkstyle")
}

group = "com.gamee"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

editorconfig {
    excludes.add("build")
}

checkstyle {
    maxWarnings = 0
    configFile = file("${rootDir}/config/naver-checkstyle-rules.xml")
    configProperties["suppressionFile"] = "${rootDir}/config/naver-checkstyle-suppressions.xml"
    toolVersion = "8.42"
}

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("com.mysql:mysql-connector-j")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-data-elasticsearch")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("com.google.firebase:firebase-admin:9.4.2")
	compileOnly("org.projectlombok:lombok")
	annotationProcessor("org.projectlombok:lombok")
	testImplementation(platform("org.junit:junit-bom:5.10.0"))
	testImplementation("org.junit.jupiter:junit-jupiter")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.withType<Test> {
	useJUnitPlatform()
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.withType<Test> {
    systemProperty("file.encoding", "UTF-8")
}

tasks.named("check") {
    dependsOn("editorconfigCheck") // If checkstyle is configured, run editorconfigCheck before checkstyle
}
