#Makefile

.DEFAULT_GOAL:=build-run

clean:
	./gradlew clean

build:
	./gradlew clean build

install:
	./gradlew clean install

run-dist:
	APP_ENV=production ./build/install/app//bin/app

run:
	APP_ENV=development ./gradlew run

check-updates:
	./gradlew dependencyUpdates

test:
	./gradlew test

report:
	./gradlew jacocoTestReport

lint:
	./gradlew checkstyleMain checkstyleTest

build-run: build run

migrations:
	./gradlew generateMigrations

list:
	@grep '^[^#[:space:]].*:' Makefile

 .PHONY: build
