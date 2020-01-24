all: deploy

build:
	lein run
	scripts/postprocess-feed out/

sass:
	lein sass once

deploy: build sass
	rsync -avz out/ nathell@danieljanus.pl:www/nhp

.PHONY: all deploy build build-sass
