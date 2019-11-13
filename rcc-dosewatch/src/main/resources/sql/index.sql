
drop index his_pre_body_part_idx;
create index his_pre_body_part_idx on his_exam using hash (pre_exam_body_part);

drop index ris_body_part_idx;
create index ris_body_part_idx on ris_exam using hash (exam_body_part);

drop index his_sheetid_idx;
create index his_sheetid_idx on his_exam using hash (sheetid);

drop index ris_sheetid_idx;
create index ris_sheetid_idx on ris_exam using hash (sheetid);

drop index ris_exam_date_idx;
create index ris_exam_date_idx on ris_exam (exam_date DESC NULLS FIRST);

create index ris_device_type_idx on ris_exam (device_type);

drop index pre_exam_schedule_date_idx;
create index pre_exam_schedule_date_idx on his_exam(pre_exam_schedule_date);

drop index exam_date_idx;
create index exam_date_idx on ris_exam(exam_date);
