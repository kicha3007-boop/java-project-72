.DEFAULT_GOAL := build

setup:
	make -C app setup

start:
	make -C app start

start-dist:
	make -C app start-dist

build:
	make -C app build

test:
	make -C app test

lint:
	make -C app lint

report:
	make -C app report

.PHONY: build test lint setup start
