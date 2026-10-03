setup: build-css
	./gradlew installDist

build-css:
	npm ci
	npm run build:css

start:
	npx --no concurrently --kill-others --names css,app \n		"npm run watch:css" \n		"APP_ENV=development ./gradlew run"

start-dist:
	APP_ENV=production ./build/install/app/bin/app

clean:
	./gradlew clean

build: build-css
	./gradlew clean build

test:
	./gradlew test

report:
	./gradlew jacocoTestReport

lint:
	./gradlew spotlessCheck

lint-fix:
	./gradlew spotlessApply

check-deps:
	./gradlew dependencyUpdates -Drevision=release

.PHONY: build test lint setup start
