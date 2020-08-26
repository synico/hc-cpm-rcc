insert into data_store
  (column1, column2, column3, column4, column5, column6, column7, is_active, job_group, job_name,job_type,last_fire_time)
  select
    device_type as column1,
    exam_method as column2,
    pre_exam_from_type as column3,
    exam_day as column4,
    exam_day_of_week as column5,
    index_of_week as column6,
    count(*)||'' as column7,
    true as is_active,
    'workloadDailyJob' as job_group,
    'examAmount' as job_name,
    'DAILY' as job_type,
    now() as last_fire_time
  from (
    select
      device_type,
      CASE
        WHEN position('平扫' in exam_method) > 0 THEN '平扫'
        WHEN position('增强' in exam_method) > 0 THEN '增强'
      END as exam_method,
      pre_exam_from_type,
      to_char(exam_date,'yyyy-MM-dd') as exam_day,
      ((to_number(to_char(exam_date, 'D'),'99')+6)%7)||'' as exam_day_of_week,
      (abs(ceil(((extract(doy from current_date) - extract(doy from exam_date)) + 1)/7) - 5))||'' as index_of_week
    from v_exam
    where device_type in ('CT', 'MR')
    and pre_exam_from_type is not null
    and exam_date > (current_date - interval '30 days')
    and (position('平扫' in exam_method) > 0 or position('增强' in exam_method) > 0)
  ) view
  group by device_type,pre_exam_from_type,exam_method,exam_day,exam_day_of_week,index_of_week;

-------------------------------

insert into data_store
  (column1, column2, column3, column4, column5, column6, column7, is_active, job_group, job_name,job_type,last_fire_time)
  select
    device_type as column1,
    exam_method as column2,
    pre_exam_from_type as column3,
    exam_day as colunm4,
    exam_day_of_week as column5,
    index_of_week as column6,
    count(*)||'' as column7,
    true as is_active,
    'workloadDailyJob' as job_group,
    'angiographyExamAmount' as job_name,
    'DAILY' as job_type,
    now() as last_fire_time
  from (
    select
      ris.device_type,
      CASE
        WHEN position('CTA' in ris.exam_body_part) > 0 THEN 'CTA'
        WHEN position('MRA' in ris.exam_body_part) > 0 THEN 'MRA'
      END as exam_method,
      his.pre_exam_from_type,
      to_char(ris.exam_date,'yyyy-MM-dd') as exam_day,
      ((to_number(to_char(ris.exam_date, 'D'),'99')+6)%7)||'' as exam_day_of_week,
      (abs(ceil(((extract(doy from current_date) - extract(doy from ris.exam_date)) + 1)/7) - 5))||'' as index_of_week
    from ris_exam ris join his_exam his on ris.sheetid=his.sheetid
    where ris.device_type in ('CT', 'MR')
    and his.pre_exam_from_type is not null
    and ris.exam_date > (current_date - interval '30 days')
    and (position('CTA' in ris.exam_body_part) > 0 or position('MRA' in exam_body_part) > 0)
  ) view
  group by device_type,pre_exam_from_type,exam_method,exam_day,exam_day_of_week,index_of_week;