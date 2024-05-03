build:
	make -C app build

report:
	make -C app report

install-dist:
	make -C app install-dist

start-dist:
	make -C app start-dist

.PHONY: build
