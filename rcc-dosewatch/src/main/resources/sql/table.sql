CREATE TABLE drl_dlp (
  exam_type		  	varchar(255) NOT NULL,
  exam_body_part	varchar(255) NOT NULL,
  dlp						  integer
)

insert into drl_dlp values('头颅', 'Head',860);
insert into drl_dlp values('鼻窦', 'Sinus',520);
insert into drl_dlp values('颈部', 'Neck',590);
insert into drl_dlp values('胸部', 'Chest',470);
insert into drl_dlp values('腹部', 'Abdomen',790);
insert into drl_dlp values('盆腔', 'Pelvis',700);
insert into drl_dlp values('腰椎（逐层）', 'Spine(sequential)',200);
insert into drl_dlp values('腰椎（螺旋）', 'Spine(spiral)',580);
insert into drl_dlp values('尿路造影', 'CTU',2620);
insert into drl_dlp values('冠脉CTA（前瞻）', 'Coronary CT Angiography（prospective）',600);
insert into drl_dlp values('冠脉CTA（回顾）', 'Coronary CTA Angiography（retrospective）',1030);
insert into drl_dlp values('颅脑CTA', 'Head CT Angiography',1390);
insert into drl_dlp values('颈部CTA', 'Neck CT Angiography',1130);
insert into drl_dlp values('胸腹CTA', 'Chest-Abdomen CT Angiography',1440);


CREATE TABLE protocol_benchmark (
   aet varchar(255) NOT NULL,
   device_type varchar(100) NOT NULL,
   protocol_name varchar(255) NOT NULL,
   avg_low float(19),
   avg_high float(19),
   median_low float(19),
   median_high float(19)
)