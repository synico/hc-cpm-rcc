create or replace view public.v_study as
    select s.id,
        to_char(s.study_date,'yyyy-MM-dd') as studydate,
        s.study_date as studydatetime,
        extract(DOW from s.study_date) as weekday_num,
        d.name,
        d.location,
        d.aet,
        d.aet as device_name,
        d.device_type,
        d.org_name,
        null as pattern,
        d.site,
        d.dashboard_page,
        d.ae_name as asset_name,
        d.station_name as device_number,
        d.mf_code as manufacture,
        d.photo_link,
        s.prev_local_study_id,
        ps.study_end_time as last_study_time,
        s.next_local_study_id,
        ns.study_start_time as next_study_time,
        s.study_start_time as start_time,
        s.study_end_time as end_time,
        EXTRACT(EPOCH FROM (s.study_end_time - s.study_start_time)) as studyexposuretime,
        s.patient_sex,
        s.patient_age,
        s.patient_id,
        s.accession_number,
        min(s.study_start_time) over( partition by date_trunc('day'::text, s.study_date),s.aet ) as up_time,
        max(s.study_end_time) over( partition by date_trunc('day'::text, s.study_date),s.aet ) as down_time,
        s.target_region_count as protocolcount,
        s.published
        from study s left join device d on s.aet=d.aet
        left join study ps on s.prev_local_study_id = ps.local_study_id
        left join study ns on s.next_local_study_id = ns.local_study_id
    where s.study_start_time is not null and s.study_end_time is not null;


create or replace view public.v_exam as
    select his.uid as id,
        his.sheetid,
        his.pre_exam_doctor_id,
        his.pre_exam_doctor_name,
        his.pre_exam_from_dept_id,
        his.pre_exam_from_dept,
        his.pre_exam_date,
        his.patient_card_num,
        his.patient_name,
        CASE
          WHEN his.patient_sex='1' THEN '男性'
          WHEN his.patient_sex='2' THEN '女性'
        END as patient_sex,
        his.patient_birth,
        COALESCE(his.patient_id,ris.patient_id) as patient_id,
        COALESCE(his.accession_num,ris.accession_num) as accession_num,
        his.exam_dept_id,
        his.exam_dept,
        his.pre_exam_from_type,
        his.is_canceled,
        his.exam_cancel_date,
        his.pre_exam_schedule_submit_date,
        his.pre_exam_schedule_date,
        his.pre_exam_schedule_duration,
        his.is_schedule_canceled,
        his.pre_exam_schedule_cancel_date,
        his.exam_place,
        CASE
            WHEN position('三舍1机房' in his.exam_place) > 0 THEN '三舍1机房'
            WHEN position('九舍2机房' in his.exam_place) > 0 THEN '九舍2机房'
            WHEN position('九舍3机房' in his.exam_place) > 0 THEN '九舍3机房'
            WHEN position('九舍4机房' in his.exam_place) > 0 THEN '九舍4机房'
            WHEN position('九舍5机房' in his.exam_place) > 0 THEN '九舍5机房'
            WHEN position('九舍6机房' in his.exam_place) > 0 THEN '九舍6机房'
            WHEN position('十舍2机房' in his.exam_place) > 0 THEN '十舍2机房'
            WHEN position('十舍4机房' in his.exam_place) > 0 THEN '十舍4机房'
            WHEN position('十舍5机房' in his.exam_place) > 0 THEN '十舍5机房'
            WHEN position('十舍6机房' in his.exam_place) > 0 THEN '十舍6机房'
            WHEN position('十舍7机房' in his.exam_place) > 0 THEN '十舍7机房'
            WHEN position('十舍8机房' in his.exam_place) > 0 THEN '十舍8机房'
            WHEN position('十舍9机房' in his.exam_place) > 0 THEN '十舍9机房'
            WHEN position('门诊1机房' in his.exam_place) > 0 THEN '门诊1机房'
            WHEN position('门诊(4机房' in his.exam_place) > 0 THEN '门诊4机房'
            WHEN position('门诊6机房' in his.exam_place) > 0 THEN '门诊6机房'
            WHEN position('门诊9机房' in his.exam_place) > 0 THEN '门诊9机房'
            WHEN position('门诊10机房' in his.exam_place) > 0 THEN '门诊10机房'
            WHEN position('门诊11机房' in his.exam_place) > 0 THEN '门诊11机房'
            WHEN position('门诊12机房' in his.exam_place) > 0 THEN '门诊12机房'
            WHEN position('门诊' in his.exam_place) > 0 THEN '门诊机房'
            ELSE his.exam_place
        END as pre_exam_place,
        CASE
            WHEN position('CT' in his.exam_place) > 0 THEN 'CT'
            WHEN position('MR' in his.exam_place) > 0 THEN 'MR'
        END as dt_from_ep,
        CASE
            WHEN position('MR' in his.pre_exam_device_type) > 0 THEN 'MR'
            ELSE his.pre_exam_device_type
        END as pre_exam_device_type,
        his.body_category as pre_exam_body_part,
        his.pre_exam_method,
        ris.requisition_id,
        ris.exam_date,
        ris.post_exam_out_date,
        CASE
            WHEN position('三舍1机房' in ris.actual_exam_place) > 0 THEN '三舍1机房'
            WHEN position('九舍1机房' in ris.actual_exam_place) > 0 THEN '九舍1机房'
            WHEN position('九舍2机房' in ris.actual_exam_place) > 0 THEN '九舍2机房'
            WHEN position('九舍3机房' in ris.actual_exam_place) > 0 THEN '九舍3机房'
            WHEN position('九舍4机房' in ris.actual_exam_place) > 0 THEN '九舍4机房'
            WHEN position('九舍5机房' in ris.actual_exam_place) > 0 THEN '九舍5机房'
            WHEN position('九舍6机房' in ris.actual_exam_place) > 0 THEN '九舍6机房'
            WHEN position('十舍1机房' in ris.actual_exam_place) > 0 THEN '十舍1机房'
            WHEN position('十舍2机房' in ris.actual_exam_place) > 0 THEN '十舍2机房'
            WHEN position('十舍4机房' in ris.actual_exam_place) > 0 THEN '十舍4机房'
            WHEN position('十舍5机房' in ris.actual_exam_place) > 0 THEN '十舍5机房'
            WHEN position('十舍6机房' in ris.actual_exam_place) > 0 THEN '十舍6机房'
            WHEN position('十舍7机房' in ris.actual_exam_place) > 0 THEN '十舍7机房'
            WHEN position('十舍8机房' in ris.actual_exam_place) > 0 THEN '十舍8机房'
            WHEN position('十舍9机房' in ris.actual_exam_place) > 0 THEN '十舍9机房'
            WHEN position('急诊1机房' in ris.actual_exam_place) > 0 THEN '急诊1机房'
            WHEN position('急诊2机房' in ris.actual_exam_place) > 0 THEN '急诊2机房'
            WHEN position('急诊3机房' in ris.actual_exam_place) > 0 THEN '急诊3机房'
            WHEN position('质子1机房' in ris.actual_exam_place) > 0 THEN '质子1机房'
            WHEN position('质子2机房' in ris.actual_exam_place) > 0 THEN '质子2机房'
            WHEN position('门诊1机房' in ris.actual_exam_place) > 0 THEN '门诊1机房'
            WHEN position('门诊2机房' in ris.actual_exam_place) > 0 THEN '门诊2机房'
            WHEN position('门诊3机房' in ris.actual_exam_place) > 0 THEN '门诊3机房'
            WHEN position('门诊4机房' in ris.actual_exam_place) > 0 THEN '门诊4机房'
            WHEN position('门诊5机房' in ris.actual_exam_place) > 0 THEN '门诊5机房'
            WHEN position('门诊6机房' in ris.actual_exam_place) > 0 THEN '门诊6机房'
            WHEN position('门诊7机房' in ris.actual_exam_place) > 0 THEN '门诊7机房'
            WHEN position('门诊9机房' in ris.actual_exam_place) > 0 THEN '门诊9机房'
            WHEN position('门诊10机房' in ris.actual_exam_place) > 0 THEN '门诊10机房'
            WHEN position('门诊11机房' in ris.actual_exam_place) > 0 THEN '门诊11机房'
            WHEN position('门诊12机房' in ris.actual_exam_place) > 0 THEN '门诊12机房'
            WHEN position('门诊' in ris.actual_exam_place) > 0 THEN '门诊机房'
            ELSE ris.actual_exam_place
        END as actual_exam_place,
        CASE
            WHEN position('CT' in ris.actual_exam_place) > 0 THEN 'CT'
            WHEN position('MR' in ris.actual_exam_place) > 0 THEN 'MR'
        END as post_exam_device_type,
        ris.device_type,
        ris.technician_id,
        ris.technician_name,
        ris.body_category as exam_body_part,
        CASE
            WHEN position('平扫' in ris.exam_method) > 0 THEN '平扫'
            WHEN position('增强' in ris.exam_method) > 0 THEN '增强'
            ELSE ris.exam_method
        END as exam_method,
        ris.study_uid,
        ris.pre_registe_date,
        ris.submit_report_time,
        ris.post_exam_reporter_id,
        ris.post_exam_reporter_name,
        ris.exam_result,
        ris.approve_report_time,
        ris.post_exam_reviewer_id,
        ris.post_exam_reviewer_name,
        ris.post_exam_approval_status
    from (select his.*,bpm.body_category from his_exam his left join (select distinct body_part,body_category from body_part_mapping) bpm on his.pre_exam_body_part=bpm.body_part) his
    full join (select ris.*,bpm.body_category from ris_exam ris left join (select distinct body_part,body_category from body_part_mapping) bpm on ris.exam_body_part=bpm.body_part) ris
    on his.sheetid=ris.sheetid;


create or replace view public.v_his_exam as
    select his.uid as id,
        his.sheetid,
        his.pre_exam_doctor_id,
        his.pre_exam_doctor_name,
        his.pre_exam_from_dept_id,
        his.pre_exam_from_dept,
        his.pre_exam_date,
        his.patient_card_num,
        his.patient_name,
        CASE
          WHEN his.patient_sex='1' THEN '男性'
          WHEN his.patient_sex='2' THEN '女性'
        END as patient_sex,
        his.patient_birth,
        his.patient_id,
        his.accession_num,
        his.exam_dept_id,
        his.exam_dept,
        his.pre_exam_from_type,
        his.is_canceled,
        his.exam_cancel_date,
        his.pre_exam_schedule_submit_date,
        his.pre_exam_schedule_date,
        his.pre_exam_schedule_duration,
        his.is_schedule_canceled,
        his.pre_exam_schedule_cancel_date,
        his.exam_place,
        CASE
            WHEN position('三舍1机房' in his.exam_place) > 0 THEN '三舍1机房'
            WHEN position('九舍2机房' in his.exam_place) > 0 THEN '九舍2机房'
            WHEN position('九舍3机房' in his.exam_place) > 0 THEN '九舍3机房'
            WHEN position('九舍4机房' in his.exam_place) > 0 THEN '九舍4机房'
            WHEN position('九舍5机房' in his.exam_place) > 0 THEN '九舍5机房'
            WHEN position('九舍6机房' in his.exam_place) > 0 THEN '九舍6机房'
            WHEN position('十舍2机房' in his.exam_place) > 0 THEN '十舍2机房'
            WHEN position('十舍4机房' in his.exam_place) > 0 THEN '十舍4机房'
            WHEN position('十舍5机房' in his.exam_place) > 0 THEN '十舍5机房'
            WHEN position('十舍6机房' in his.exam_place) > 0 THEN '十舍6机房'
            WHEN position('十舍7机房' in his.exam_place) > 0 THEN '十舍7机房'
            WHEN position('十舍8机房' in his.exam_place) > 0 THEN '十舍8机房'
            WHEN position('十舍9机房' in his.exam_place) > 0 THEN '十舍9机房'
            WHEN position('门诊1机房' in his.exam_place) > 0 THEN '门诊1机房'
            WHEN position('门诊(4机房' in his.exam_place) > 0 THEN '门诊4机房'
            WHEN position('门诊6机房' in his.exam_place) > 0 THEN '门诊6机房'
            WHEN position('门诊9机房' in his.exam_place) > 0 THEN '门诊9机房'
            WHEN position('门诊10机房' in his.exam_place) > 0 THEN '门诊10机房'
            WHEN position('门诊11机房' in his.exam_place) > 0 THEN '门诊11机房'
            WHEN position('门诊12机房' in his.exam_place) > 0 THEN '门诊12机房'
            WHEN position('门诊' in his.exam_place) > 0 THEN '门诊机房'
            ELSE his.exam_place
        END as pre_exam_place,
        CASE
            WHEN position('CT' in his.exam_place) > 0 THEN 'CT'
            WHEN position('MR' in his.exam_place) > 0 THEN 'MR'
        END as dt_from_ep,
        CASE
            WHEN position('MR' in his.pre_exam_device_type) > 0 THEN 'MR'
            ELSE his.pre_exam_device_type
        END as pre_exam_device_type,
        his.pre_exam_method
    from his_exam his;


create or replace view public.v_ris_exam as
    select
        ris.requisition_id,
        ris.patient_id,
        ris.exam_date,
        ris.post_exam_out_date,
        CASE
            WHEN position('三舍1机房' in ris.actual_exam_place) > 0 THEN '三舍1机房'
            WHEN position('九舍1机房' in ris.actual_exam_place) > 0 THEN '九舍1机房'
            WHEN position('九舍2机房' in ris.actual_exam_place) > 0 THEN '九舍2机房'
            WHEN position('九舍3机房' in ris.actual_exam_place) > 0 THEN '九舍3机房'
            WHEN position('九舍4机房' in ris.actual_exam_place) > 0 THEN '九舍4机房'
            WHEN position('九舍5机房' in ris.actual_exam_place) > 0 THEN '九舍5机房'
            WHEN position('九舍6机房' in ris.actual_exam_place) > 0 THEN '九舍6机房'
            WHEN position('十舍1机房' in ris.actual_exam_place) > 0 THEN '十舍1机房'
            WHEN position('十舍2机房' in ris.actual_exam_place) > 0 THEN '十舍2机房'
            WHEN position('十舍4机房' in ris.actual_exam_place) > 0 THEN '十舍4机房'
            WHEN position('十舍5机房' in ris.actual_exam_place) > 0 THEN '十舍5机房'
            WHEN position('十舍6机房' in ris.actual_exam_place) > 0 THEN '十舍6机房'
            WHEN position('十舍7机房' in ris.actual_exam_place) > 0 THEN '十舍7机房'
            WHEN position('十舍8机房' in ris.actual_exam_place) > 0 THEN '十舍8机房'
            WHEN position('十舍9机房' in ris.actual_exam_place) > 0 THEN '十舍9机房'
            WHEN position('急诊1机房' in ris.actual_exam_place) > 0 THEN '急诊1机房'
            WHEN position('急诊2机房' in ris.actual_exam_place) > 0 THEN '急诊2机房'
            WHEN position('急诊3机房' in ris.actual_exam_place) > 0 THEN '急诊3机房'
            WHEN position('质子1机房' in ris.actual_exam_place) > 0 THEN '质子1机房'
            WHEN position('质子2机房' in ris.actual_exam_place) > 0 THEN '质子2机房'
            WHEN position('门诊1机房' in ris.actual_exam_place) > 0 THEN '门诊1机房'
            WHEN position('门诊2机房' in ris.actual_exam_place) > 0 THEN '门诊2机房'
            WHEN position('门诊3机房' in ris.actual_exam_place) > 0 THEN '门诊3机房'
            WHEN position('门诊4机房' in ris.actual_exam_place) > 0 THEN '门诊4机房'
            WHEN position('门诊5机房' in ris.actual_exam_place) > 0 THEN '门诊5机房'
            WHEN position('门诊6机房' in ris.actual_exam_place) > 0 THEN '门诊6机房'
            WHEN position('门诊7机房' in ris.actual_exam_place) > 0 THEN '门诊7机房'
            WHEN position('门诊9机房' in ris.actual_exam_place) > 0 THEN '门诊9机房'
            WHEN position('门诊10机房' in ris.actual_exam_place) > 0 THEN '门诊10机房'
            WHEN position('门诊11机房' in ris.actual_exam_place) > 0 THEN '门诊11机房'
            WHEN position('门诊12机房' in ris.actual_exam_place) > 0 THEN '门诊12机房'
            WHEN position('门诊' in ris.actual_exam_place) > 0 THEN '门诊机房'
            ELSE ris.actual_exam_place
        END as actual_exam_place,
        CASE
            WHEN position('CT' in ris.actual_exam_place) > 0 THEN 'CT'
            WHEN position('MR' in ris.actual_exam_place) > 0 THEN 'MR'
        END as post_exam_device_type,
        ris.device_type,
        ris.technician_id,
        ris.technician_name,
        CASE
            WHEN position('平扫' in ris.exam_method) > 0 THEN '平扫'
            WHEN position('增强' in ris.exam_method) > 0 THEN '增强'
            ELSE ris.exam_method
        END as exam_method,
        ris.study_uid,
        ris.pre_registe_date,
        ris.submit_report_time,
        ris.post_exam_reporter_id,
        ris.post_exam_reporter_name,
        ris.exam_result,
        ris.approve_report_time,
        ris.post_exam_reviewer_id,
        ris.post_exam_reviewer_name,
        ris.post_exam_approval_status
    from ris_exam ris;