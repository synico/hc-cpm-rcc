## ii-schedulers

为提高报表查询效率，提升开发效率，此组件角色为ETL。将需要报表需要使用的数据预先处理好，并按一定格式存于data_store表中。

***

* version: 1.0
* file: ii-schedulers-1.0.jar
* release date: 2020-08-26
* author: 212706300

### features
#### 增加医技科室效率四组实时报表数据处理
* 放射科数据概览12个cases
* 放射科检查等待时间分析2个cases
* 报告超时比率4个cases
* 放射科机房科室工作量24h分析1个cases

#### 实时数据初始化脚本
脚本存于resouces/sql目录下，脚本需要在schedulers运行前运行
* 放射科数据概览2个cases，需要按扫描方式平扫/增强和CTA/MRA分别处理数据
* 放射科检查等待时间分析3个cases，需要按扫描方式平扫/增强，CTA/MRA，XRay分别处理数据
* 报告超时比率2个cases，需要按扫描方式平扫/增强和CTA/MRA分别处理数据
* 放射科机房科室工作量24分析1个case

#### 实时数据处理程序
为提高硬件资源利用率和查询效率，并考虑到数据量，将实时数据处理分为两部分。历史数据由数据初始化脚本实现，而增量数据由schedulers完成。

报表|任务组|任务名|说明|运行频率|运行时间
:--|:--|:--|:--|:--|:--
放射科数据概览|workloadDailyJob|examAmount|仅处理CT和MR扫描类型为平扫或增强检查|每小时|每个整点0分
放射科数据概览|workloadDailyJob|angiographyExamAmount|仅处理CTA和MRA检查|每小时|每个整点02分
放射科检查等待时间分析|waitForExamTimeDailyJob|examByAngiography|仅处理CTA和MRA检查|每小时|每个整点04分
放射科检查等待时间分析|waitForExamTimeDailyJob|examByOtherMethods|仅处理CT和MR扫描类型为平扫或增强检查|每小时|每个整点06分
放射科检查等待时间分析|waitForExamTimeDailyJob|examByXray|仅处理XRay检查|每小时|每个整点08分
报告超时比率|reportDelayRatioDailyJob|examByAngiography|仅处理CTA和MRA检查|每个时|每个整点10分
报告超时比率|reportDelayRatioDailyJob|examByOtherMethods|仅处理CT和MR检查|每个时|每个整点12分
放射科机房科室工作量24h分析|workloadByHoursDailyJob|workloadByHours||每15分钟|

***
