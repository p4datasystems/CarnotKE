If maven is not installed on your machine, install it with:
$ brew update

$ brew install maven

After extracting cyphersim.zip, navigate to the cyphersim/ directory and build with the following command:

$ mvn install

The built jar will be located at cyphersim/target/cyphersim-0.0.1-SNAPSHOT-jar-with-dependencies.jar

Source code is located in the cyphersim/src/ directory
The code for the translator is in cyphersim/src/main/java/org/cyphersim
The unit tests are located in the cyphersim/src/test/java/org/cyphersim/CypherSimTranslatorTest.java file
