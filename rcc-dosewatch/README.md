## RELEASE NOTES

Version|Date|File
:--|:--|:--
1.1|2020-09-02|rcc-dosewatch-1.1.jar

### 发布版本1.1

1. 针对dosewatch升级至3.2.1以后MR检查数据存储结构变化，升级对应MR检查处理任务。
2. 针对CT通过RDSR方式发送的检查，新增处理任务模板StudyPullJob-RDSR.template。
3. 增强studyCleanJob，通过调整时间范围，可处理指定时间范围内的检查。


***

## install dw-pojo lib to local mvn repository

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

***

## start application
```
java -Xbootclasspath/a:config -jar rcc-dosewatch-1.0-SNAPSHOT.jar
```