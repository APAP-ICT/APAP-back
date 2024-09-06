package com.example.apapbackend.domain.report;

import com.example.apapbackend.domain.Info.Info;
import com.example.apapbackend.domain.Info.InfoRepository;
import com.example.apapbackend.domain.Info.InfoService;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final InfoService infoService;

    public void getDailyReport(LocalDate localDate) {
        List<Info> infos = infoService.getInfos(localDate, localDate, null, null);
    }
}
