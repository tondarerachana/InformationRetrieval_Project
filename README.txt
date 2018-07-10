#########################     README    ############################# 
-------------------------------------------------------
Assignment A4

GOAL: Design and build your information retrieval systems, evaluate and compare	
their performance levels in terms of retrieval effectiveness
-------------------------------------------------------

#NOTE: MAKE SURE YOU ARE IN THE “Project” FOLDER WHEN YOU RUN THE COMMANDS FROM TERMINAL

-------------------------------------------------------
1. Software
-------------------------------------------------------
 
* JDK version 1.8
* Maven

-------------------------------------------------------
2. Environment variables
-------------------------------------------------------
 
* Check that the following environment variables are set:$JAVA_HOME, $MAVEN_HOME

-------------------------------------------------------
3. Project
-------------------------------------------------------

The project file contains the following files and folders:
1. src: Source Code for this assignment
   Packages within the src folder are:
	a. com.ir.model : all code files for the 4 model implementations
	b. com.ir.tasks : Code files for all the ir tasks like processing, precision recall, query expansion,
			   Indexing, and object classes for doc frequencies, entry and measure class.
       c. com.main.task1 : Code files for baseline runs for every model
       d. com.main.task2 : Code file for Pseudo relevance implementation using cosine as the baseline.
       e. com.main.task3 : Code files for stemming and stopping using cosine as the baseline.
       f. com.main.task 4 : Code files for all the evaluations
			      Note: Have included the 7th run here using TfIdf as the baseline with stopping.

2. Report: Pdf file containing all the assumptions, approaches and explanations asked for. 

3. FinalResults: This folder contains all results asked for. 

-------------------------------------------------------
4. Execution:
-------------------------------------------------------

Steps to run the project:
1. mvn clean install
2. mvn assembly:assembly 
3. java -jar target/Project5-0.0.1-SNAPSHOT-jar-with-dependencies.jar

Change the main class in the pom.xml to the desired implementation

In order to run the Task 1:
	-> com.main.task1.Cosine 
	-> com.main.task1.LuceneBM25 
	-> com.main.task1.LuceneDefault 
	-> com.main.task1.TfIdfImpl

In order to run the Task 2:
	-> com.main.task2.PseudoRelevance 

In order to run the Task 3:
	-> com.main.task3.Stemming
	-> com.main.task3.Stopping 

In order to run the Task 4:
	-> com.main.task4.EvalCosine 
	-> com.main.task4.EvalLuceneDefault 
	-> com.main.task4.EvalLuceneBM25 
	-> com.main.task4.EvalTfIdf 
	-> com.main.task4.EvalPseudoRel 
	-> com.main.task4.EvalStemming 
	-> com.main.task4.EvalStopping 
	-> com.main.task4.Eval_7thRun 

and then run the above steps again.