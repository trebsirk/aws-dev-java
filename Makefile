all: target/aws-dev-java-1.0.jar

target/aws-dev-java-1.0.jar: src/main/java/aws/example/*/*.java
	mvn package

clean:
	mvn clean
