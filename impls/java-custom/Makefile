
SOURCES =

all:
	mvn install

#dist: mal.jar mal

#mal.jar: target/classes/com/example/mal/stepA_mal.class
#	mvn assembly:assembly
#	cp target/mal-0.0.1.jar $@

SHELL := bash
#mal: mal.jar
#	cat <(echo -e '#!/bin/sh\nexec java -jar "$$0" "$$@"') mal.jar > $@
#	chmod +x mal

#src/main/mal/%.java:
#	mvn install

target/classes/com/example/mal/step%.class: src/main/java/com/example/mal/step%.java ${SOURCES}
	mvn install

clean:
	mvn clean
