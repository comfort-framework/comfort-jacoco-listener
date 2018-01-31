# COMFORT Jacoco Listener
[![Build Status](https://travis-ci.org/comfort-framework/comfort-jacoco-listener.svg?branch=master)](https://travis-ci.org/comfort-framework/comfort-jacoco-listener)
[![codecov](https://codecov.io/gh/comfort-framework/comfort-jacoco-listener/branch/master/graph/badge.svg)](https://codecov.io/gh/comfort-framework/comfort-jacoco-listener)
[![BCH compliance](https://bettercodehub.com/edge/badge/comfort-framework/comfort-jacoco-listener?branch=master)](https://bettercodehub.com/)


### Description
The COMFORT jacoco listener provides a custom JUnit listener. It works together with  
[Jacoco](https://github.com/jacoco/jacoco) and lets Jacoco create a .exec file where one session corresponds to one
test method that was executed. This .exec file can be used as input for the TestCoverageLoader of the 
[COMFORT framework](https://github.com/comfort-framework/comfort)

### Test 
```bash
gradle check
```

### Build
Change into the directory and call
```bash
gradle jar
```

### Install
Change into the main directory and call
```bash
gradle install
```

### Use 
The jacoco listener can be used via Maven or Gradle. For Gradle, you need to use the 
[forked gradle version](https://github.com/ftrautsch/gradle), as we need
to make some adjustments, so that we can add a custom listener easily via the build.gradle.

#### Maven
Include dependencies
```xml
<dependencies>
    <dependency>
        <groupId>de.ugoe.cs</groupId>
        <artifactId>comfort-jacoco-listener</artifactId>
        <version>1.0.0</version>
        <scope>test</scope>
    </dependency>
    
    <dependency>
        <groupId>junit</groupId>
        <artifactId>junit</artifactId>
        <version>4.12</version>
        <scope>test</scope>
    </dependency>
</dependencies>
```

Include the jacoco plugin in the build process
```xml
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <version>0.7.9</version>
    <executions>
        <execution>
            <id>prepare-agent</id>
            <phase>process-test-classes</phase>
            <goals>
                <goal>prepare-agent</goal>
            </goals>
        </execution>
        <execution>
            <id>report</id>
            <phase>site</phase>
            <goals>
                <goal>report</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

Add the new listener to the test process
```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-surefire-plugin</artifactId>
    <version>2.20.1</version>
    <dependencies>
        <dependency>
            <groupId>org.apache.maven.surefire</groupId>
            <artifactId>surefire-junit47</artifactId>
            <version>2.20.1</version>
        </dependency>
    </dependencies>
    <configuration>
        <properties>
            <property>
                <name>listener</name>
                <value>de.ugoe.cs.comfort.listener.JUnitListener</value>
            </property>
        </properties>
    </configuration>
</plugin>
```

Test the program:
```bash
mvn test
```

#### Gradle
Add the following to the build.gradle. If this project has subprojects, make sure that these are added for each subproject 
that you want to analyze. Furthermore, note that your project can have other dependencies or repositories that it
uses. So just add the ones that are shown below.

```groovy
apply plugin: 'jacoco'

repositories {
    mavenLocal()
}

dependencies {
    testCompile group: 'de.ugoe.cs', name: 'comfort-jacoco-listener', version: '1.0.0'
}

test {
    useJUnit {
        listeners 'de.ugoe.cs.comfort.listener.JUnitListener'
    }
}
```

#### Result
Afterwards, a .exec file is generated, which can be used as input for the TestCoverageLoader of the 
[COMFORT framework](https://github.com/comfort-framework/comfort).
