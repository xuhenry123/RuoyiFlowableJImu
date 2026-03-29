package org.dromara.report.controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.dromara.common.core.domain.R;
import org.dromara.report.domain.vo.ReportVo;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/report")
public class ReportController {

    @GetMapping("/list")
    public R<List<ReportVo>> list() {
        List<ReportVo> list = new ArrayList<>();
        ReportVo vo1 = new ReportVo();
        vo1.setId("1");
        vo1.setCode("sales-report");
        vo1.setName("销售报表");
        vo1.setType("chart");
        vo1.setStatus("1");
        vo1.setDescription("月度销售数据统计");
        list.add(vo1);

        ReportVo vo2 = new ReportVo();
        vo2.setId("2");
        vo2.setCode("user-stats");
        vo2.setName("用户统计");
        vo2.setType("chart");
        vo2.setStatus("1");
        vo2.setDescription("用户增长趋势分析");
        list.add(vo2);

        ReportVo vo3 = new ReportVo();
        vo3.setId("3");
        vo3.setCode("order-detail");
        vo3.setName("订单明细");
        vo3.setType("table");
        vo3.setStatus("1");
        vo3.setDescription("订单详细信息列表");
        list.add(vo3);

        ReportVo vo4 = new ReportVo();
        vo4.setId("4");
        vo4.setCode("finance-summary");
        vo4.setName("财务汇总");
        vo4.setType("table");
        vo4.setStatus("1");
        vo4.setDescription("财务收支汇总报表");
        list.add(vo4);

        return R.ok(list);
    }

    @GetMapping("/{id}")
    public R<ReportVo> getInfo(@PathVariable String id) {
        ReportVo vo = new ReportVo();
        vo.setId(id);
        vo.setCode("report-" + id);
        vo.setName("报表" + id);
        vo.setType("chart");
        vo.setStatus("1");
        return R.ok(vo);
    }

    @GetMapping("/preview/{id}")
    public void preview(@PathVariable String id, HttpServletResponse response) throws Exception {
        response.setContentType("text/html;charset=utf-8");
        response.getWriter().write("<html><body><h1>报表预览</h1><p>报表ID: " + id + "</p></body></html>");
    }

    @GetMapping("/design/url")
    public R<String> getDesignUrl() {
        return R.ok("http://jimureport.baike888.com/index.html");
    }
}
