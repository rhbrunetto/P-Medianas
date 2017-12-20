SHELL=cmd.exe
FILE=Medianas
CLASS=Main
INPUT=SJC4b

all: posexecute
	@echo "Sucesso!"

compile:
	@javac $(FILE).java

makejar: compile
	@jar cf $(FILE).jar *.class

execute: makejar

posexecute: execute
	@del *.class
