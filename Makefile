.DELETE_ON_ERROR:

ifeq ($(origin JAVA_HOME), undefined)
  JAVA_HOME=/usr
endif

ifeq ($(origin NETLOGO), undefined)
  NETLOGO=/Applications/NetLogo\ 5.0beta2
endif

ifeq ($(origin SCALA_JAR), undefined)
  SCALA_JAR=$(NETLOGO)/lib/scala-library.jar
endif

JAVAC=$(JAVA_HOME)/bin/javac

NLink.class: NLink.java JLink.jar
	@echo "@@@ building Mathematica Link/NLink.class"
	$(JAVAC) -source 1.5 -target 1.5 -classpath $(NETLOGO)/NetLogo.jar:$(SCALA_JAR):JLink.jar NLink.java

JLink.jar:
	cp /Applications/Mathematica.app/SystemFiles/Links/JLink/JLink.jar .

.PHONY: clean
clean:
	rm -f *.class JLink.jar
