CONTENTS

	1. ABOUT
	2. REQUIREMENTS
	3. QUICK START
	4. VERSION
	5. LICENSE


================================================================================
1. ABOUT 
================================================================================
This package provides an implementation of Align, Disambiguate, and Walk (ADW).
ADW is a WordNet-based approach for measuring semantic similarity of arbitrary 
pairs of lexical items, from word senses to full texts. The approach leverages 
random walks on semantic networks for modeling lexical items.


================================================================================
2. REQUIREMENTS
================================================================================

	- Java 7 (JRE 1.7) or newer version
	- ADW jar file (adw.*.jar)
	- Semantic signatures (ppvs.30g.1k.tar.bz2)
	- Resources and config files


================================================================================
3. QUICK START
================================================================================

	Setting up ADW is easy, you just need to download the required resources
	and set their paths in the properties files.

	(1) Download and extract the followings in your project folder:

		(a) semantic signatures 
			http://lcl.uniroma1.it/adw/ppvs.30g.1k.tar.gz

		(b) ADW's JAR package, resources & config files (the package containing this readme)
			http://lcl.uniroma1.it/adw/jar/adw.v1.0.tar.gz
		
	Now, you should have three new sub-directories in your project directory: config, ppvs.30g.1k, and resources.


	(2) Create a Test.java class:

		import it.uniroma1.lcl.adw.ADW;
		import it.uniroma1.lcl.adw.DisambiguationMethod;
		import it.uniroma1.lcl.adw.ItemType;
		import it.uniroma1.lcl.adw.comparison.SignatureComparison;
		import it.uniroma1.lcl.adw.comparison.WeightedOverlap;


		class Test
		{

			public static void main(String args[])
			{
				ADW pipeLine = new ADW();
			 	   
				//the two lexical items
				String text1 = "a mill that is powered by the wind";
				String text2 = "windmill#n rotate#v wind#n";

				//types of the lexical items (set as auto-detect)
				ItemType text1Type = ItemType.SURFACE;
				ItemType text2Type = ItemType.SURFACE_TAGGED;

				//measure for comparing semantic signatures
				SignatureComparison measure = new WeightedOverlap(); 

			  	//calculate the similarity of text1 and text2
				double similarity = pipeLine.getPairSimilarity(
				    text1, text2,
				    DisambiguationMethod.ALIGNMENT_BASED, 
				    measure,
				    text1Type, text2Type);
			    
				//print out the similarity
				System.out.println(similarity);

			}
		}

	(3) In the project directory, compile and run the Test class:

		$ javac -classpath adw.feb2015.jar Test.java
		$ java -classpath adw.feb2015.jar Test


	You might want to modify project parameters or source/config/signature directory
	locations in the config/*.properties files.
	
	For more details, please read the Wiki of ADW's github repository at:
	https://github.com/pilehvar/ADW

=================================================================================
2. VERSION
=================================================================================

   Version 1.0 : 2014-11-19 


=================================================================================
7. LICENSE
=================================================================================

    ADW - Align, Disambiguate, and Walk 
    A unified approach for measuring semantic similarity
    Copyright (c) 2014 Sapienza University of Rome.
    All Rights Reserved.

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.

    If you use this system, please cite the following paper:
	M. T. Pilehvar, D. Jurgens and R. Navigli. 
	Align, Disambiguate and Walk: A Unified Approach for Measuring 
	Semantic Similarity. 
	Proc. of the 51st Annual Meeting of the Association for 
	Computational Linguistics (ACL 2013), Sofia, Bulgaria, 
	August 4-9, 2013, pp. 1341-1351.

    For more information please contact:
        Mohammad Taher Pilehvar
        Department of Computer Science
	Sapienza University of Rome        
        pilehvar[at-sign]di.uniroma1.it
        http://www.pilevar.com/~taher
