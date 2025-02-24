Programming Assignment 02
Course:CS-5313-60412 Formal Langugae theory
Name: Vinay Chegondi
ID:A20446822



overview:

the mainprogram.java implements a program that takes input patten and an input string
Its calls the NDFSMBuilder.java (using the input pattern) this code generates an ndfsm output(consits of transition states)
followed by NDFSMtoDFSM.java (using the ndfsm output) generates an dfsm output
followed by EXDFSM.java (using dfsm output and input string ) checks if the machine accepts or rejects the input string 

Compilation:
javac <MainProgram.java>

Execution:
java MainProgram <patten.txt> <inputstring.txt>
