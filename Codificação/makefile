SHELL=cmd.exe
FILE=Medianas
CLASS=Main

all: execute
	@echo "Sucesso!"

compile:
	@javac $(FILE).java

makejar: compile
	@jar cf $(FILE).jar $(CLASS).class

execute: compile
	@del *.class
