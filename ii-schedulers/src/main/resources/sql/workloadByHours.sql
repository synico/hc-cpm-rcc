insert into data_store
  (column1, column2, column3, column4, is_active, job_group, job_name,job_type,last_fire_time)
  SELECT exam_day as column1,
    actual_exam_place as column2,
    exam_hour_of_day as column3,
    COUNT(*)||'' as column4,
    true as is_active,
    'workloadByHoursDailyJob' as job_group,
    'workloadByHours' as job_name,
    'HOURLY' as job_type,
    now() as last_fire_time
  FROM (
    select actual_exam_place,device_type,
      to_char(exam_date,'yyyy') as exam_year,to_char(exam_date,'MM') as exam_month_of_year,
      to_char(exam_date,'yyyy-MM-dd') as exam_day,to_char(exam_date,'hh24') as exam_hour_of_day
    from ris_exam
    where device_type in ('CT','MR')
      and actual_exam_place is not null
  ) cb_view
  GROUP BY exam_hour_of_day, actual_exam_place, exam_day;