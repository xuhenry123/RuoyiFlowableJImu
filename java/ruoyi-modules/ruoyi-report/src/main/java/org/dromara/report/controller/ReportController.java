package org.dromara.report.controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.domain.R;
import org.dromara.report.domain.vo.ReportVo;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/report")
public class ReportController {

    private static final Map<String, ReportVo> REPORT_STORE = new ConcurrentHashMap<>();
    
    static {
        ReportVo vo1 = new ReportVo();
        vo1.setId("1");
        vo1.setCode("sales-report");
        vo1.setName("销售报表");
        vo1.setType("chart");
        vo1.setStatus("1");
        vo1.setDescription("月度销售数据统计");
        vo1.setCreateBy("admin");
        vo1.setCreateTime(new Date());
        REPORT_STORE.put(vo1.getId(), vo1);

        ReportVo vo2 = new ReportVo();
        vo2.setId("2");
        vo2.setCode("user-stats");
        vo2.setName("用户统计");
        vo2.setType("chart");
        vo2.setStatus("1");
        vo2.setDescription("用户增长趋势分析");
        vo2.setCreateBy("admin");
        vo2.setCreateTime(new Date());
        REPORT_STORE.put(vo2.getId(), vo2);

        ReportVo vo3 = new ReportVo();
        vo3.setId("3");
        vo3.setCode("order-detail");
        vo3.setName("订单明细");
        vo3.setType("table");
        vo3.setStatus("1");
        vo3.setDescription("订单详细信息列表");
        vo3.setCreateBy("admin");
        vo3.setCreateTime(new Date());
        REPORT_STORE.put(vo3.getId(), vo3);

        ReportVo vo4 = new ReportVo();
        vo4.setId("4");
        vo4.setCode("finance-summary");
        vo4.setName("财务汇总");
        vo4.setType("table");
        vo4.setStatus("1");
        vo4.setDescription("财务收支汇总报表");
        vo4.setCreateBy("admin");
        vo4.setCreateTime(new Date());
        REPORT_STORE.put(vo4.getId(), vo4);
    }

    @GetMapping("/list")
    public R<List<ReportVo>> list() {
        return R.ok(new ArrayList<>(REPORT_STORE.values()));
    }

    @GetMapping("/{id}")
    public R<ReportVo> getInfo(@PathVariable String id) {
        ReportVo vo = REPORT_STORE.get(id);
        if (vo == null) {
            vo = new ReportVo();
            vo.setId(id);
            vo.setCode("report-" + id);
            vo.setName("报表" + id);
            vo.setType("chart");
            vo.setStatus("1");
        }
        return R.ok(vo);
    }

    @PostMapping
    public R<ReportVo> create(@RequestBody ReportVo vo) {
        if (vo.getId() == null || vo.getId().isEmpty()) {
            vo.setId(UUID.randomUUID().toString().replace("-", ""));
        }
        vo.setCreateTime(new Date());
        vo.setCreateBy("admin");
        vo.setStatus("1");
        REPORT_STORE.put(vo.getId(), vo);
        log.info("创建报表: {}", vo.getName());
        return R.ok(vo);
    }

    @PutMapping
    public R<Void> update(@RequestBody ReportVo vo) {
        if (vo.getId() == null || !REPORT_STORE.containsKey(vo.getId())) {
            return R.fail("报表不存在");
        }
        vo.setUpdateTime(new Date());
        vo.setUpdateBy("admin");
        REPORT_STORE.put(vo.getId(), vo);
        log.info("更新报表: {}", vo.getName());
        return R.ok();
    }

    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable String id) {
        if (REPORT_STORE.remove(id) != null) {
            log.info("删除报表: {}", id);
            return R.ok();
        }
        return R.fail("报表不存在");
    }

    @GetMapping("/preview/{id}")
    public void preview(@PathVariable String id, HttpServletResponse response) throws Exception {
        response.setContentType("text/html;charset=utf-8");
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html><html><head><title>报表预览</title>");
        html.append("<style>body{margin:0;padding:20px;font-family:Arial,sans-serif}");
        html.append(".report-preview{border:1px solid #ddd;border-radius:8px;padding:20px}");
        html.append("h1{color:#333;border-bottom:2px solid #409eff;padding-bottom:10px}");
        html.append(".meta{color:#666;font-size:14px;margin-top:20px}");
        html.append("</style></head><body>");
        html.append("<div class='report-preview'>");
        html.append("<h1>报表预览 - ID: ").append(id).append("</h1>");
        
        ReportVo vo = REPORT_STORE.get(id);
        if (vo != null) {
            html.append("<p><strong>报表名称：</strong>").append(vo.getName()).append("</p>");
            html.append("<p><strong>报表类型：</strong>").append(vo.getType()).append("</p>");
            html.append("<p><strong>描述：</strong>").append(vo.getDescription() != null ? vo.getDescription() : "").append("</p>");
            html.append("<p><strong>状态：</strong>").append("1".equals(vo.getStatus()) ? "正常" : "禁用").append("</p>");
        } else {
            html.append("<p>报表不存在</p>");
        }
        
        html.append("</div>");
        html.append("</body></html>");
        response.getWriter().write(html.toString());
    }

    @GetMapping("/design/url")
    public R<String> getDesignUrl() {
        return R.ok("http://jimureport.baike888.com/index.html");
    }

    @GetMapping("/datas/{id}")
    public R<Map<String, Object>> getReportData(@PathVariable String id) {
        Map<String, Object> data = new HashMap<>();
        data.put("id", id);
        data.put("timestamp", System.currentTimeMillis());
        data.put("items", Arrays.asList(
            createDataItem("2026-01", 1200, "success"),
            createDataItem("2026-02", 1500, "success"),
            createDataItem("2026-03", 1800, "success"),
            createDataItem("2026-04", 2100, "warning")
        ));
        return R.ok(data);
    }

    private Map<String, Object> createDataItem(String date, int value, String status) {
        Map<String, Object> item = new HashMap<>();
        item.put("date", date);
        item.put("value", value);
        item.put("status", status);
        return item;
    }
}
