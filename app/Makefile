# Makefile
run-dist: # Run application
	@./build/install/app/bin/app -h

build:
	@./gradlew clean build

install:
	@./gradlew clean install

test:
	@./gradlew test

report:
	@./gradlew jacocoTestReport


.PHONY: build