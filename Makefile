all: deploy

build:
	lein run

sass:
	lein sass once

deploy: build sass
	rsync -avz out/ nathell@danieljanus.pl:www/nhp

.PHONY: all deploy build build-sass
