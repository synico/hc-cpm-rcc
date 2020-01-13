FROM openjdk:8-alpine

MAINTAINER "nick.liu"<xuewei.liu@ge.com>

ADD $PWD/target/rcc-dosewatch-0.9.jar /opt/ge/rcc-dosewatch.jar

WORKDIR /opt/ge/

CMD java -Xbootclasspath/a:config -jar rcc-dosewatch.jar

