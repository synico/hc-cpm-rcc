insert into data_store
  (column1, column2, column3, column4, column5, column6, column7,
    is_active, job_group, job_name,job_type,last_fire_time)
  SELECT
    exam_day as column1,
    device_type as column2,
    exam_method as column3,
    pre_exam_from_type as column4,
    round(sum(gt2_count)::numeric * 100 / (count(*))::numeric, 2)||'' as column5,
    round(sum(gt7_count)::numeric * 100 / (count(*))::numeric, 2)||'' as column6,
    COUNT(*)||'' as column7,
    true as is_active,
    'reportDelayRatioDailyJob' as job_group,
    'examByAngiography' as job_name,
    'HOURLY' as job_type,
    now() as last_fire_time
  FROM (
    select his.sheetid,ris.accession_num,to_char(ris.approve_report_time,'yyyy-MM-dd') as exam_day,
      his.pre_exam_from_type,submit_report_time,approve_report_time,
      extract(EPOCH from (approve_report_time - submit_report_time))/3600 as wait_hour,
      CASE
        WHEN position('CTA' in ris.exam_body_part) > 0 THEN 'CTA'
        WHEN position('MRA' in ris.exam_body_part) > 0 THEN 'MRA'
      END as exam_method,
      ris.device_type,
      CASE
      WHEN extract(EPOCH from (approve_report_time - submit_report_time)) > 172800 THEN 1
      ELSE 0
      END as gt2_count,
      CASE
      WHEN extract(EPOCH from (approve_report_time - submit_report_time)) > 604800 THEN 1
      ELSE 0
      END as gt7_count
    from ris_exam ris join his_exam his on ris.sheetid=his.sheetid
    where ris.approve_report_time is not null and ris.exam_date is not null
      and his.pre_exam_from_type is not null
      and (position('CTA' in ris.exam_body_part) > 0 or position('MRA' in ris.exam_body_part) > 0)
  ) cb_view
  GROUP BY pre_exam_from_type, exam_day, device_type, exam_method;

------------------------------------------

insert into data_store
  (column1, column2, column3, column4, column5, column6, column7,
    is_active, job_group, job_name,job_type,last_fire_time)
  SELECT
    exam_day as column1,
    device_type as column2,
    exam_method as column3,
    pre_exam_from_type as column4,
    round(sum(gt2_count)::numeric * 100 / (count(*))::numeric, 2)||'' as column5,
    round(sum(gt7_count)::numeric * 100 / (count(*))::numeric, 2)||'' as column6,
    COUNT(*)||'' as column7,
    true as is_active,
    'reportDelayRatioDailyJob' as job_group,
    'examByOtherMethods' as job_name,
    'HOURLY' as job_type,
    now() as last_fire_time
  FROM (
    select his.sheetid,ris.accession_num,to_char(ris.approve_report_time,'yyyy-MM-dd') as exam_day,
      his.pre_exam_from_type,submit_report_time,approve_report_time,
      extract(EPOCH from (approve_report_time - submit_report_time))/3600 as wait_hour,
      CASE
        WHEN position('平扫' in exam_method) > 0 THEN '平扫'
        WHEN position('增强' in exam_method) > 0 THEN '增强'
      END as exam_method,
      ris.device_type,
      CASE
        WHEN extract(EPOCH from (approve_report_time - submit_report_time)) > 172800 THEN 1
        ELSE 0
      END as gt2_count,
      CASE
        WHEN extract(EPOCH from (approve_report_time - submit_report_time)) > 604800 THEN 1
        ELSE 0
      END as gt7_count
    from ris_exam ris join his_exam his on ris.sheetid=his.sheetid
    where ris.approve_report_time is not null and ris.exam_date is not null
      and his.pre_exam_from_type is not null
      and (position('平扫' in exam_method) > 0 or position('增强' in exam_method) > 0)
  ) cb_view
  GROUP BY exam_day, device_type, exam_method,pre_exam_from_type;