CREATE TABLE drl_dlp (
  exam_type		  	varchar(255) NOT NULL,
  exam_body_part	varchar(255) NOT NULL,
  dlp						  integer
)

ALTER TABLE drl_dlp ADD COLUMN country varchar(255);

insert into drl_dlp values('头颅', 'Head', 860, 'China');
insert into drl_dlp values('鼻窦', 'Sinus', 520, 'China');
insert into drl_dlp values('颈部', 'Neck', 590, 'China');
insert into drl_dlp values('胸部', 'Chest', 470, 'China');
insert into drl_dlp values('腹部', 'Abdomen', 790, 'China');
insert into drl_dlp values('盆腔', 'Pelvis', 700, 'China');
insert into drl_dlp values('腰椎（逐层）', 'Lumbar spine (Axial)', 200, 'China');
insert into drl_dlp values('腰椎（螺旋）', 'Lumbar spine (Helical)', 580, 'China');
insert into drl_dlp values('尿路造影', 'Urogram', 2620, 'China');
insert into drl_dlp values('冠脉CTA（前瞻）', 'Coronary CTA (prospective)', 600, 'China');
insert into drl_dlp values('冠脉CTA（回顾）', 'Coronary CTA (retrospective)', 1030, 'China');
insert into drl_dlp values('颅脑CTA', 'Head CTA', 1390, 'China');
insert into drl_dlp values('颈部CTA', 'Neck CTA', 1130, 'China');
insert into drl_dlp values('胸腹CTA', 'Abdomen Pelvis CTA', 1440, 'China');

insert into drl_dlp values('头颅', 'Head', 962, 'US');
insert into drl_dlp values('颈部', 'Neck', 563, 'US');
insert into drl_dlp values('胸部', 'Chest', 443, 'US');
insert into drl_dlp values('腹部', 'Abdomen', 781, 'US');
insert into drl_dlp values('盆腔', 'Pelvis', 781, 'US');

insert into drl_dlp values('头颅', 'Head', 850, 'France');
insert into drl_dlp values('鼻窦', 'Sinus', 250, 'France');
insert into drl_dlp values('胸部', 'Chest', 350, 'France');
insert into drl_dlp values('腹部', 'Abdomen', 550, 'France');
insert into drl_dlp values('盆腔', 'Pelvis', 625, 'France');
insert into drl_dlp values('腰椎（螺旋）', 'Lumbar spine (Helical)', 725, 'France');
insert into drl_dlp values('冠脉CTA（前瞻）', 'Coronary CTA (prospective)', 375, 'France');
insert into drl_dlp values('冠脉CTA（回顾）', 'Coronary CTA (retrospective)', 875, 'France');

insert into drl_dlp values('头颅', 'Head', 850, 'Germany');
insert into drl_dlp values('鼻窦', 'Sinus', 90, 'Germany');
insert into drl_dlp values('颈部', 'Neck', 330, 'Germany');
insert into drl_dlp values('胸部', 'Chest', 450, 'Germany');
insert into drl_dlp values('腹部', 'Abdomen', 700, 'Germany');
insert into drl_dlp values('盆腔', 'Pelvis', 700, 'Germany');
insert into drl_dlp values('腰椎（逐层）', 'Lumbar spine (Axial)', 180, 'Germany');
insert into drl_dlp values('冠脉CTA（前瞻）', 'Coronary CTA (prospective)', 330, 'Germany');
insert into drl_dlp values('胸腹CTA', 'Abdomen Pelvis CTA', 1000, 'Germany');

insert into drl_dlp values('头颅', 'Head', 970, 'UK');
insert into drl_dlp values('胸部', 'Chest', 610, 'UK');
insert into drl_dlp values('腹部', 'Abdomen', 910, 'UK');
insert into drl_dlp values('盆腔', 'Pelvis', 745, 'UK');
insert into drl_dlp values('尿路造影', 'Urogram', 1150, 'UK');
insert into drl_dlp values('冠脉CTA（前瞻）', 'Coronary CTA (prospective)', 280, 'UK');
insert into drl_dlp values('冠脉CTA（回顾）', 'Coronary CTA (retrospective)', 380, 'UK');
insert into drl_dlp values('胸腹CTA', 'Abdomen Pelvis CTA', 1040, 'UK');

insert into drl_dlp values('头颅', 'Head', 1350, 'Japan');
insert into drl_dlp values('胸部', 'Chest', 550, 'Japan');
insert into drl_dlp values('腹部', 'Abdomen', 1000, 'Japan');
insert into drl_dlp values('盆腔', 'Pelvis', 1000, 'Japan');
insert into drl_dlp values('冠脉CTA（回顾）', 'Coronary CTA (retrospective)', 1400, 'Japan');

insert into drl_dlp values('头颅', 'Head', 1000, 'Australia');
insert into drl_dlp values('颈部', 'Neck', 600, 'Australia');
insert into drl_dlp values('胸部', 'Chest', 450, 'Australia');
insert into drl_dlp values('腹部', 'Abdomen', 700, 'Australia');
insert into drl_dlp values('盆腔', 'Pelvis', 700, 'Australia');
insert into drl_dlp values('腰椎（螺旋）', 'Lumbar spine (Helical)', 900, 'Australia');

CREATE TABLE protocol_benchmark (
   aet varchar(255) NOT NULL,
   device_type varchar(100) NOT NULL,
   protocol_name varchar(255) NOT NULL,
   avg_low float(19),
   avg_high float(19),
   median_low float(19),
   median_high float(19)
)