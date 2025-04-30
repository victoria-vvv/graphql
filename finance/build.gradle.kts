buildscript {
	dependencies {
		classpath("org.flywaydb:flyway-database-postgresql:10.0.0")
	}
}

plugins {
	kotlin("jvm") version "1.9.25"
	kotlin("plugin.spring") version "1.9.25"
	id("org.springframework.boot") version "3.4.4"
	id("io.spring.dependency-management") version "1.1.7"
	id("com.netflix.dgs.codegen") version "7.0.3"
	id("org.flywaydb.flyway") version "10.0.0"
	id("nu.studer.jooq") version "9.0"
}

group = "com.project"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
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
	implementation("org.springframework.boot:spring-boot-starter-data-r2dbc")
	implementation("org.springframework.boot:spring-boot-starter-webflux")
	implementation("org.springframework.boot:spring-boot-starter-jooq")
	implementation("org.flywaydb:flyway-core")
	implementation("com.graphql-java:graphql-java:21.5")
	implementation("org.jooq:jooq:3.19.19")
	implementation("org.jooq:jooq-meta:3.19.19")
	implementation("org.jooq:jooq-codegen:3.19.19")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
	implementation("org.postgresql:r2dbc-postgresql:1.0.7.RELEASE")
	implementation("io.r2dbc:r2dbc-spi")
	runtimeOnly("org.postgresql:postgresql:42.7.5")
	implementation("com.graphql-java:graphql-java-extended-scalars:22.0")
	implementation("com.graphql-java:graphql-java-extended-validation:22.0")
	implementation("com.apollographql.federation:federation-graphql-java-support:5.0.0")
	compileOnly("org.projectlombok:lombok")
	annotationProcessor("org.projectlombok:lombok")
	runtimeOnly("org.flywaydb:flyway-database-postgresql:10.0.0")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("io.projectreactor:reactor-test")
	testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
	testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test")
	testImplementation("org.springframework.graphql:spring-graphql-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
	jooqGenerator("org.postgresql:postgresql:42.7.5")
}

kotlin {
	compilerOptions {
		freeCompilerArgs.addAll("-Xjsr305=strict")
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}

flyway {
	url = "jdbc:postgresql://localhost:5433/finance-db"
	user = "postgresuser"
	password = "postgrespassword"
	locations = arrayOf("filesystem:src/main/resources/db/migration")
	driver = "org.postgresql.Driver"
	cleanDisabled = false
}

jooq {
	version.set("3.19.19")
	edition.set(nu.studer.gradle.jooq.JooqEdition.OSS)

	configurations {
		create("main") {
			generateSchemaSourceOnCompilation.set(true)

			jooqConfiguration.apply {
				logging = org.jooq.meta.jaxb.Logging.WARN
				generator.apply {
					name = "org.jooq.codegen.DefaultGenerator"
					jdbc.apply {
						url = "jdbc:postgresql://localhost:5433/finance-db"
						user = "postgresuser"
						password = "postgrespassword"
					}
					database.apply {
						name = "org.jooq.meta.postgres.PostgresDatabase"
						inputSchema = "public"
					}
					target.apply {
						packageName = "com.project.finance.model"
						directory = "build/generated-src/jooq/main"
					}
				}
			}
		}
	}
}