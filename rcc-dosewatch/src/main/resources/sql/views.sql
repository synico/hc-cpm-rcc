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
        s.published,
        COALESCE(d.prepare_time1, 0) * 60 as prepare_sec1,
        COALESCE(d.prepare_time2, 0) * 60 as prepare_sec2,
        COALESCE(cts.protocol_name, mrs.protocol_name, xas.protocol_name, '') as protocol_name,
        d.province,
        d.city,
        d.org_id,
        s.local_study_id,
	s.device_key
        from study s left join device d on s.aet = d.aet and s.modality = d.device_type and s.org_id = d.org_id
        left join study ps on s.prev_local_study_id = ps.local_study_id
        left join study ns on s.next_local_study_id = ns.local_study_id
        left join ct_study cts on s.local_study_id = cts.local_study_id
        left join mr_study mrs on s.local_study_id = mrs.local_study_id
        left join xa_study xas on s.local_study_id = xas.local_study_id
    where s.study_date is not null and s.study_start_time is not null and s.study_end_time is not null;
