/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.gehc.cpm.jobs;

import cn.gehc.cpm.domain.HisExam;
import cn.gehc.cpm.domain.ReqScheduleInfo;
import cn.gehc.cpm.repository.HisExamRepository;
import cn.gehc.cpm.repository.ReqScheduleInfoRepository;
import cn.gehc.cpm.utils.DataConvertUtils;
import cn.gehc.cpm.utils.DateTimeUtil;
import org.apache.camel.Body;
import org.apache.camel.Headers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 *
 * @author 212579464
 */
@Service
public class ReqScheduleInfoPullJob extends TimerDBReadJob {

    private static final Logger log = LoggerFactory.getLogger(ReqScheduleInfoPullJob.class);

    @Autowired
    private ReqScheduleInfoRepository reqScheduleInfoRepository;

    @Autowired
    private HisExamRepository hisExamRepository;

    public void insertBatchExam(@Headers Map<String, Object> headers, @Body Object body) {

        List<Map<String, Object>> examMap = (List<Map<String, Object>>) body;

        Date maxDate = saveData2DB(examMap);

        //记录读取进度
        if(maxDate != null) {
            this.updateLastPullValue(headers, DateTimeUtil.getStrDate(maxDate, "yyyyMMddHHmmss"));
        }
    }

    /**
     * @param data
     * @return max datetime of the data for recording current progress
     */
    private Date saveData2DB(List<Map<String, Object>> data) {
        List<ReqScheduleInfo> infos = new ArrayList<>(100);
        ReqScheduleInfo info;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        Date maxDate = null;
        StringBuilder warningMsg;
        for(Map<String, Object> obj : data) {
            warningMsg = new StringBuilder("Empty field: ");
            info = new ReqScheduleInfo();

            if(DataConvertUtils.isBlankField(obj.get("SheetID"))) {
                warningMsg.append("SheetID,");
            } else {
                info.setSheetId(String.valueOf(obj.get("SheetId")));
            }

            if(DataConvertUtils.isBlankField(obj.get("ScheduleOperateDateTime"))) {
                warningMsg.append("ScheduleOperateDateTime,");
            } else {
                try {
                    info.setScheduleOperateDateTime(
                            dateFormat.parse(String.valueOf(obj.get("ScheduleOperateDateTime")))
                    );

                    if(maxDate == null) {
                        maxDate = info.getScheduleOperateDateTime();
                    } else {
                        maxDate = (maxDate.compareTo(info.getScheduleOperateDateTime()) > 0) ? maxDate : info.getScheduleOperateDateTime();
                    }
                } catch (Exception ex) {
                    log.warn("SheetId: " + info.getSheetId() + ", parse ScheduleOperateDateTime failed.");
                }
            }

            if(DataConvertUtils.isNotBlankField(obj.get("ScheduleDate")) && DataConvertUtils.isNotBlankField(obj.get("ScheduleTime"))) {
                String scheduleDate = String.valueOf(obj.get("ScheduleDate"));
                String scheduleTime = String.valueOf(obj.get("ScheduleTime"));
                String scheduleTimeInfos[] = scheduleTime.split("[:|-]");
                if(scheduleTimeInfos.length == 2) {
                    info.setScheduleDuration(0);
                    try {
                        info.setScheduleDate(
                                dateFormat.parse(scheduleDate + scheduleTimeInfos[0] + scheduleTimeInfos[1] + "00")
                        );
                    } catch (Exception ex) {
                        log.warn("SheetId: " + info.getSheetId() + ", parse scheduleTime failed");
                    }
                }
                if(scheduleTimeInfos.length == 4) {
                    info.setScheduleDuration(
                            (Integer.parseInt(scheduleTimeInfos[2]) - Integer.parseInt(scheduleTimeInfos[0])) * 60 +
                                    (Integer.parseInt(scheduleTimeInfos[3]) - Integer.parseInt(scheduleTimeInfos[1]))
                    );
                    try {
                        info.setScheduleDate(
                                dateFormat.parse(scheduleDate + scheduleTimeInfos[0] + scheduleTimeInfos[1] + "00")
                        );
                    } catch (Exception ex) {
                        log.warn("SheetId: " + info.getSheetId() + ", parse scheduleTime failed");
                    }
                }
                info.setScheduleTime(scheduleTime);
            } else {
                warningMsg.append("ScheduleDate,ScheduleTime");
            }

            info.setRoomName(String.valueOf(obj.get("RoomName")));

            infos.add(info);

            if(log.isWarnEnabled() && warningMsg.toString().endsWith(",")) {
                log.warn(warningMsg.toString());
            }
        }

        Iterable<ReqScheduleInfo> result = reqScheduleInfoRepository.saveAll(infos);

        saveData2HisExam(infos);

        return maxDate;
    }

    private void saveData2HisExam(List<ReqScheduleInfo> infos) {
        Map<String, ReqScheduleInfo> infoMap = new HashMap<>(100);
        List<String> sheetids = new ArrayList<>(100);
        List<HisExam> hisExams2Sync = new ArrayList<>(100);

        infos.forEach(info -> {
            sheetids.add(info.getSheetId().trim());
            infoMap.put(info.getSheetId().trim(), info);
        });

        List<HisExam> hisExamList = hisExamRepository.findBySheetIdIn(sheetids);
        hisExamList.forEach(hisExam -> {
            ReqScheduleInfo info = infoMap.remove(hisExam.getSheetId());
            hisExams2Sync.add(populateInfo2HisExam(info, hisExam));
        });
        log.info("{} records will be updated", hisExams2Sync.size());

        HisExam hisExam;
        for(ReqScheduleInfo info : infoMap.values()) {
            hisExam = new HisExam();
            hisExam.setSheetId(info.getSheetId());
            hisExams2Sync.add(populateInfo2HisExam(info, hisExam));
        }
        hisExamRepository.saveAll(hisExams2Sync);
    }

    private HisExam populateInfo2HisExam(ReqScheduleInfo info, HisExam hisExam) {
        hisExam.setExamPlace(info.getRoomName());
        hisExam.setPreExamScheduleSubmitDate(info.getScheduleOperateDateTime());
        hisExam.setPreExamScheduleDate(info.getScheduleDate());
        hisExam.setPreExamScheduleDuration(info.getScheduleDuration());

        return hisExam;
    }

}
