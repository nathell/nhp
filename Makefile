all: deploy

build: resources/css/nhp.css
	clojure -X:build
	scripts/postprocess-feed out/

resources/css/nhp.css: src/sass/nhp.sass
	sassc $< $@

deploy: build
	rsync -avz out/ nathell@danieljanus.pl:/var/www

sass: resources/css/nhp.css

.PHONY: all deploy build sass
