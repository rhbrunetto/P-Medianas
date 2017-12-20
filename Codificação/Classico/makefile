SHELL=cmd.exe
FILE=Medianas
CLASS=Main
INPUT=SJC4b

all: posexecute
	@echo "Sucesso!"

compile:
	@javac $(FILE).java

makejar: compile
	@jar cf $(FILE).jar $(CLASS).class

execute: makejar
	@java Main < ../Testes/$(INPUT).dat > Logs/log_$(INPUT).csv

posexecute: execute
	@del *.class
