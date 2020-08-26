insert into data_store
  (column1, column2, column3, column4, column5, is_active, job_group, job_name,job_type,last_fire_time)
  select
    exam_day as column1,
    pre_exam_from_type as column2,
    exam_method as column3,
    round(avg(wait_sec/86400)::numeric, 2)||'' as column4,
    count(*)||'' as column5,
    true as is_active,
    'waitForExamTimeDailyJob' as job_group,
    'examByAngiography' as job_name,
    'HOURLY' as job_type,
    now() as last_fire_time
  from (
    select his.sheetid,ris.accession_num,his.pre_exam_schedule_submit_date,
      ris.pre_registe_date,ris.exam_date,to_char(ris.exam_date,'yyyy-MM-dd') as exam_day,
      his.pre_exam_from_type,
      extract(EPOCH from(exam_date - pre_exam_schedule_submit_date)) as wait_sec,
      CASE
        WHEN position('CTA' in ris.exam_body_part) > 0 THEN 'CTA'
        WHEN position('MRA' in ris.exam_body_part) > 0 THEN 'MRA'
      END as exam_method
    from ris_exam ris join his_exam his on ris.sheetid=his.sheetid
    where his.pre_exam_schedule_submit_date is not null and ris.exam_date is not null
      and ris.exam_date > his.pre_exam_schedule_submit_date
      and pre_exam_from_type is not null
  ) v_exam
  where exam_method is not null
  group by exam_method,pre_exam_from_type,exam_day;

-------------------------------

insert into data_store
  (column1, column2, column3, column4, column5, is_active, job_group, job_name,job_type,last_fire_time)
  select
    exam_day as column1,
    pre_exam_from_type as column2,
    device_type||' '||exam_method as column3,
    round(avg(wait_sec/86400)::numeric, 2)||'' as column4,
    count(*)||'' as column5,
    true as is_active,
    'waitForExamTimeDailyJob' as job_group,
    'examByOtherMethods' as job_name,
    'HOURLY' as job_type,
    now() as last_fire_time
  from (
    select his.sheetid,ris.accession_num,his.pre_exam_schedule_date,ris.pre_registe_date,
      ris.exam_date,to_char(ris.exam_date,'yyyy-MM-dd') as exam_day,
      his.pre_exam_from_type,
      extract(EPOCH from(exam_date - pre_exam_schedule_submit_date)) as wait_sec,
      CASE
        WHEN position('平扫' in exam_method) > 0 THEN '平扫'
        WHEN position('增强' in exam_method) > 0 THEN '增强'
      END as exam_method,
      ris.device_type
    from ris_exam ris join his_exam his on ris.sheetid=his.sheetid
    where his.pre_exam_schedule_submit_date is not null and ris.exam_date is not null
      and ris.exam_date > his.pre_exam_schedule_submit_date
      and pre_exam_from_type is not null
  ) v_exam
  where exam_method is not null
  group by device_type,exam_method,pre_exam_from_type,exam_day;

-------------------------------

insert into data_store
  (column1, column2, column3, column4, column5, is_active, job_group, job_name,job_type,last_fire_time)
  select
    exam_day as column1,
    pre_exam_from_type as column2,
    exam_method as column3,
    round(avg(wait_sec/86400)::numeric, 2)||'' as column4,
    count(*)||'' as column5,
    true as is_active,
    'waitForExamTimeDailyJob' as job_group,
    'examByXRay' as job_name,
    'HOURLY' as job_type,
    now() as last_fire_time
  from (
    select his.sheetid,ris.accession_num,his.pre_exam_schedule_date,ris.pre_registe_date,
      ris.exam_date,to_char(ris.exam_date,'yyyy-MM-dd') as exam_day,
      his.pre_exam_from_type,
      extract(EPOCH from(exam_date - pre_exam_schedule_submit_date)) as wait_sec,
      ris.device_type as exam_method,
      ris.device_type
    from ris_exam ris join his_exam his on ris.sheetid=his.sheetid
    where his.pre_exam_schedule_submit_date is not null and ris.exam_date is not null
      and ris.device_type='X-RAY'
      and ris.exam_date > his.pre_exam_schedule_submit_date
      and pre_exam_from_type is not null
  ) v_exam
  where exam_method is not null
  group by device_type,exam_method,pre_exam_from_type,exam_day;