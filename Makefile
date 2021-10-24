all: deploy

build:
	lein run
	scripts/postprocess-feed out/

sass:
	lein sass once

deploy: build sass
	rsync -avz out/ nathell@danieljanus.pl:/var/www

.PHONY: all deploy build build-sass
