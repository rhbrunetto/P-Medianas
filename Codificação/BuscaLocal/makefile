SHELL=cmd.exe
FILE=Medianas
CLASS=Main

all: posexecute
	@echo "Sucesso!"

compile:
	@javac $(FILE).java

makejar: compile
	@jar cf $(FILE).jar $(CLASS).class

execute: makejar
	@java Main < ../Testes/SJC3a.dat

posexecute: execute
	@del *.class
