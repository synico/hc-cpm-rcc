install dw-pojo lib to local mvn repository

### Step1 
```
mvn clean package -Dmaven.test.skip=true
```
### Step2
```
mvn install:install-file -Dfile=rcc-dosewatch-0.9-pojo.jar -DgroupId=cn.gehc.cpm -DartifactId=dw-pojo -Dversion=0.9 -Dpackaging=jar -DgeneratePom=true -DcreateChecksum=true
```

### Usage:
```
<dependency>
    <groupId>cn.gehc.cpm</groupId>
    <artifactId>dw-pojo</artifactId>
    <version>0.9</version>
</dependency>
```