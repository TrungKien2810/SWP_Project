package E2E;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.util.CellRangeAddress;

import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Class để tạo Excel report chi tiết cho E2E tests
 * Bao gồm: Summary, Statistics, Charts, Test Details, Test Steps, Execution Timeline
 */
public class TestReportGenerator {
    
    private static final String REPORT_DIR = "test-reports";
    private List<TestResult> testResults = new ArrayList<>();
    private String testSuiteName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String scenarioName; // Tên kịch bản (nếu có)
    
    public TestReportGenerator(String testSuiteName) {
        this.testSuiteName = testSuiteName;
        this.startTime = LocalDateTime.now();
        // Xác định tên kịch bản từ test suite name
        if (testSuiteName.contains("CustomerShopping")) {
            this.scenarioName = "Kịch bản 1: Trải nghiệm khách hàng";
        } else if (testSuiteName.contains("AccountManagement")) {
            this.scenarioName = "Kịch bản 2: Quản lý tài khoản";
        } else if (testSuiteName.contains("Admin") && !testSuiteName.contains("Promotion")) {
            this.scenarioName = "Kịch bản 3: Quản trị";
        } else if (testSuiteName.contains("AdminPromotion")) {
            this.scenarioName = "Kịch bản 4: Mã giảm giá và báo cáo";
        } else {
            this.scenarioName = "E2E Tests";
        }
    }
    
    /**
     * Thêm kết quả test vào report
     */
    public void addTestResult(String testName, String status, String description, 
                             List<String> steps, String errorMessage, String screenshotPath) {
        TestResult result = new TestResult();
        result.testName = testName;
        result.status = status; // PASS, FAIL, SKIP
        result.description = description;
        result.steps = steps != null ? new ArrayList<>(steps) : new ArrayList<>();
        result.errorMessage = errorMessage;
        result.screenshotPath = screenshotPath;
        result.timestamp = LocalDateTime.now();
        testResults.add(result);
    }
    
    /**
     * Đánh dấu kết thúc test suite
     */
    public void finish() {
        this.endTime = LocalDateTime.now();
    }
    
    /**
     * Tạo file Excel report
     */
    public String generateReport() throws IOException {
        // Tạo thư mục nếu chưa có
        java.io.File reportDir = new java.io.File(REPORT_DIR);
        if (!reportDir.exists()) {
            reportDir.mkdirs();
        }
        
        // Tạo tên file với timestamp
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
        String fileName = String.format("%s/TestReport_%s_%s.xlsx", 
            REPORT_DIR, 
            testSuiteName.replaceAll("[^a-zA-Z0-9]", "_"),
            startTime.format(formatter));
        
        try (Workbook workbook = new XSSFWorkbook()) {
            // Tạo sheet Summary (trang đầu tiên)
            Sheet summarySheet = workbook.createSheet("📊 Summary");
            createSummarySheet(summarySheet, workbook);
            
            // Tạo sheet Statistics với charts
            Sheet statisticsSheet = workbook.createSheet("📈 Statistics");
            createStatisticsSheet(statisticsSheet, workbook);
            
            // Tạo sheet Test Details
            Sheet detailsSheet = workbook.createSheet("📋 Test Details");
            createDetailsSheet(detailsSheet, workbook);
            
            // Tạo sheet Test Steps
            Sheet stepsSheet = workbook.createSheet("👣 Test Steps");
            createStepsSheet(stepsSheet, workbook);
            
            // Tạo sheet Execution Timeline
            Sheet timelineSheet = workbook.createSheet("⏱️ Timeline");
            createTimelineSheet(timelineSheet, workbook);
            
            // Tạo sheet Failed Tests (nếu có)
            long failedCount = testResults.stream().filter(r -> "FAIL".equals(r.status)).count();
            if (failedCount > 0) {
                Sheet failedSheet = workbook.createSheet("❌ Failed Tests");
                createFailedTestsSheet(failedSheet, workbook);
            }
            
            // Ghi file
            try (FileOutputStream fileOut = new FileOutputStream(fileName)) {
                workbook.write(fileOut);
            }
        }
        
        return fileName;
    }
    
    /**
     * Tạo sheet Summary
     */
    private void createSummarySheet(Sheet sheet, Workbook workbook) {
        int rowNum = 0;
        
        // Title
        Row titleRow = sheet.createRow(rowNum++);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("📊 E2E TEST REPORT - " + scenarioName);
        CellStyle titleStyle = workbook.createCellStyle();
        Font titleFont = workbook.createFont();
        titleFont.setBold(true);
        titleFont.setFontHeightInPoints((short) 18);
        titleFont.setColor(IndexedColors.DARK_BLUE.getIndex());
        titleStyle.setFont(titleFont);
        titleStyle.setAlignment(HorizontalAlignment.CENTER);
        titleStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
        titleStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        titleCell.setCellStyle(titleStyle);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 4));
        
        rowNum += 2; // Skip 2 dòng
        
        // Test Suite Info
        createInfoRow(sheet, rowNum++, "Test Suite:", testSuiteName);
        createInfoRow(sheet, rowNum++, "Kịch bản:", scenarioName);
        createInfoRow(sheet, rowNum++, "Start Time:", 
            startTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        createInfoRow(sheet, rowNum++, "End Time:", 
            endTime != null ? endTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : "N/A");
        
        if (endTime != null) {
            long durationSeconds = java.time.Duration.between(startTime, endTime).getSeconds();
            long minutes = durationSeconds / 60;
            long seconds = durationSeconds % 60;
            String durationStr = minutes > 0 ? String.format("%d phút %d giây", minutes, seconds) : durationSeconds + " giây";
            createInfoRow(sheet, rowNum++, "Duration:", durationStr);
        }
        
        rowNum += 2; // Skip 2 dòng
        
        // Statistics với màu sắc
        int total = testResults.size();
        long passed = testResults.stream().filter(r -> "PASS".equals(r.status)).count();
        long failed = testResults.stream().filter(r -> "FAIL".equals(r.status)).count();
        long skipped = testResults.stream().filter(r -> "SKIP".equals(r.status)).count();
        
        // Header cho Statistics
        Row statsHeaderRow = sheet.createRow(rowNum++);
        Cell statsHeaderCell = statsHeaderRow.createCell(0);
        statsHeaderCell.setCellValue("📈 THỐNG KÊ KẾT QUẢ");
        CellStyle statsHeaderStyle = workbook.createCellStyle();
        Font statsHeaderFont = workbook.createFont();
        statsHeaderFont.setBold(true);
        statsHeaderFont.setFontHeightInPoints((short) 14);
        statsHeaderStyle.setFont(statsHeaderFont);
        statsHeaderCell.setCellStyle(statsHeaderStyle);
        sheet.addMergedRegion(new CellRangeAddress(rowNum - 1, rowNum - 1, 0, 1));
        
        createInfoRow(sheet, rowNum++, "Tổng số test:", String.valueOf(total));
        
        // Passed với màu xanh
        Row passedRow = sheet.createRow(rowNum++);
        Cell passedLabel = passedRow.createCell(0);
        passedLabel.setCellValue("✅ Passed:");
        CellStyle passedLabelStyle = workbook.createCellStyle();
        Font passedLabelFont = workbook.createFont();
        passedLabelFont.setBold(true);
        passedLabelFont.setColor(IndexedColors.GREEN.getIndex());
        passedLabelStyle.setFont(passedLabelFont);
        passedLabel.setCellStyle(passedLabelStyle);
        Cell passedValue = passedRow.createCell(1);
        passedValue.setCellValue(String.valueOf(passed));
        passedValue.setCellStyle(passedLabelStyle);
        
        // Failed với màu đỏ
        Row failedRow = sheet.createRow(rowNum++);
        Cell failedLabel = failedRow.createCell(0);
        failedLabel.setCellValue("❌ Failed:");
        CellStyle failedLabelStyle = workbook.createCellStyle();
        Font failedLabelFont = workbook.createFont();
        failedLabelFont.setBold(true);
        failedLabelFont.setColor(IndexedColors.RED.getIndex());
        failedLabelStyle.setFont(failedLabelFont);
        failedLabel.setCellStyle(failedLabelStyle);
        Cell failedValue = failedRow.createCell(1);
        failedValue.setCellValue(String.valueOf(failed));
        failedValue.setCellStyle(failedLabelStyle);
        
        // Skipped với màu cam
        Row skippedRow = sheet.createRow(rowNum++);
        Cell skippedLabel = skippedRow.createCell(0);
        skippedLabel.setCellValue("⏭️ Skipped:");
        CellStyle skippedLabelStyle = workbook.createCellStyle();
        Font skippedLabelFont = workbook.createFont();
        skippedLabelFont.setBold(true);
        skippedLabelFont.setColor(IndexedColors.ORANGE.getIndex());
        skippedLabelStyle.setFont(skippedLabelFont);
        skippedLabel.setCellStyle(skippedLabelStyle);
        Cell skippedValue = skippedRow.createCell(1);
        skippedValue.setCellValue(String.valueOf(skipped));
        skippedValue.setCellStyle(skippedLabelStyle);
        
        if (total > 0) {
            double passRate = (double) passed / total * 100;
            Row passRateRow = sheet.createRow(rowNum++);
            Cell passRateLabel = passRateRow.createCell(0);
            passRateLabel.setCellValue("📊 Pass Rate:");
            CellStyle passRateLabelStyle = workbook.createCellStyle();
            Font passRateLabelFont = workbook.createFont();
            passRateLabelFont.setBold(true);
            passRateLabelFont.setFontHeightInPoints((short) 12);
            passRateLabelStyle.setFont(passRateLabelFont);
            passRateLabel.setCellStyle(passRateLabelStyle);
            Cell passRateValue = passRateRow.createCell(1);
            passRateValue.setCellValue(String.format("%.2f%%", passRate));
            CellStyle passRateValueStyle = workbook.createCellStyle();
            Font passRateValueFont = workbook.createFont();
            passRateValueFont.setBold(true);
            passRateValueFont.setFontHeightInPoints((short) 12);
            if (passRate >= 80) {
                passRateValueFont.setColor(IndexedColors.GREEN.getIndex());
            } else if (passRate >= 50) {
                passRateValueFont.setColor(IndexedColors.ORANGE.getIndex());
            } else {
                passRateValueFont.setColor(IndexedColors.RED.getIndex());
            }
            passRateValueStyle.setFont(passRateValueFont);
            passRateValue.setCellStyle(passRateValueStyle);
        }
        
        rowNum += 2; // Skip 2 dòng
        
        // Test Results Table Header
        Row headerRow = sheet.createRow(rowNum++);
        String[] headers = {"Test Name", "Status", "Description", "Timestamp"};
        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setColor(IndexedColors.WHITE.getIndex());
        headerStyle.setFont(headerFont);
        headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }
        
        // Test Results Data
        CellStyle passStyle = workbook.createCellStyle();
        Font passFont = workbook.createFont();
        passFont.setColor(IndexedColors.GREEN.getIndex());
        passStyle.setFont(passFont);
        
        CellStyle failStyle = workbook.createCellStyle();
        Font failFont = workbook.createFont();
        failFont.setColor(IndexedColors.RED.getIndex());
        failStyle.setFont(failFont);
        
        CellStyle skipStyle = workbook.createCellStyle();
        Font skipFont = workbook.createFont();
        skipFont.setColor(IndexedColors.ORANGE.getIndex());
        skipStyle.setFont(skipFont);
        
        for (TestResult result : testResults) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(result.testName);
            
            Cell statusCell = row.createCell(1);
            statusCell.setCellValue(result.status);
            if ("PASS".equals(result.status)) {
                statusCell.setCellStyle(passStyle);
            } else if ("FAIL".equals(result.status)) {
                statusCell.setCellStyle(failStyle);
            } else {
                statusCell.setCellStyle(skipStyle);
            }
            
            row.createCell(2).setCellValue(result.description != null ? result.description : "");
            row.createCell(3).setCellValue(
                result.timestamp.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        }
        
        // Auto-size columns
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }
    
    /**
     * Tạo sheet Test Details
     */
    private void createDetailsSheet(Sheet sheet, Workbook workbook) {
        int rowNum = 0;
        
        // Header
        Row headerRow = sheet.createRow(rowNum++);
        String[] headers = {"Test Name", "Status", "Description", "Error Message", "Screenshot"};
        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setColor(IndexedColors.WHITE.getIndex());
        headerStyle.setFont(headerFont);
        headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }
        
        // Data
        for (TestResult result : testResults) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(result.testName);
            row.createCell(1).setCellValue(result.status);
            row.createCell(2).setCellValue(result.description != null ? result.description : "");
            row.createCell(3).setCellValue(result.errorMessage != null ? result.errorMessage : "");
            row.createCell(4).setCellValue(result.screenshotPath != null ? result.screenshotPath : "");
        }
        
        // Auto-size columns
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }
    
    /**
     * Tạo sheet Test Steps
     */
    private void createStepsSheet(Sheet sheet, Workbook workbook) {
        int rowNum = 0;
        
        // Header
        Row headerRow = sheet.createRow(rowNum++);
        String[] headers = {"Test Name", "Step Number", "Step Description"};
        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setColor(IndexedColors.WHITE.getIndex());
        headerStyle.setFont(headerFont);
        headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }
        
        // Data
        for (TestResult result : testResults) {
            if (result.steps != null && !result.steps.isEmpty()) {
                for (int i = 0; i < result.steps.size(); i++) {
                    Row row = sheet.createRow(rowNum++);
                    row.createCell(0).setCellValue(result.testName);
                    row.createCell(1).setCellValue("Step " + (i + 1));
                    row.createCell(2).setCellValue(result.steps.get(i));
                }
            } else {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(result.testName);
                row.createCell(1).setCellValue("N/A");
                row.createCell(2).setCellValue("No steps recorded");
            }
        }
        
        // Auto-size columns
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }
    
    /**
     * Tạo sheet Statistics với dữ liệu chi tiết
     */
    private void createStatisticsSheet(Sheet sheet, Workbook workbook) {
        int rowNum = 0;
        
        // Title
        Row titleRow = sheet.createRow(rowNum++);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("📈 THỐNG KÊ CHI TIẾT");
        CellStyle titleStyle = workbook.createCellStyle();
        Font titleFont = workbook.createFont();
        titleFont.setBold(true);
        titleFont.setFontHeightInPoints((short) 16);
        titleStyle.setFont(titleFont);
        titleCell.setCellStyle(titleStyle);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 3));
        
        rowNum += 2;
        
        // Statistics table
        int total = testResults.size();
        long passed = testResults.stream().filter(r -> "PASS".equals(r.status)).count();
        long failed = testResults.stream().filter(r -> "FAIL".equals(r.status)).count();
        long skipped = testResults.stream().filter(r -> "SKIP".equals(r.status)).count();
        
        // Header
        Row headerRow = sheet.createRow(rowNum++);
        String[] headers = {"Metric", "Count", "Percentage", "Status"};
        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setColor(IndexedColors.WHITE.getIndex());
        headerStyle.setFont(headerFont);
        headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }
        
        // Data rows
        if (total > 0) {
            // Total
            createStatRow(sheet, rowNum++, "Total Tests", total, 100.0, "INFO", workbook);
            // Passed
            double passPercent = (double) passed / total * 100;
            createStatRow(sheet, rowNum++, "✅ Passed", (int) passed, passPercent, "PASS", workbook);
            // Failed
            double failPercent = (double) failed / total * 100;
            createStatRow(sheet, rowNum++, "❌ Failed", (int) failed, failPercent, "FAIL", workbook);
            // Skipped
            double skipPercent = (double) skipped / total * 100;
            createStatRow(sheet, rowNum++, "⏭️ Skipped", (int) skipped, skipPercent, "SKIP", workbook);
        }
        
        rowNum += 2;
        
        // Test execution time statistics
        if (!testResults.isEmpty() && endTime != null) {
            Row timeHeaderRow = sheet.createRow(rowNum++);
            Cell timeHeaderCell = timeHeaderRow.createCell(0);
            timeHeaderCell.setCellValue("⏱️ THỜI GIAN THỰC THI");
            CellStyle timeHeaderStyle = workbook.createCellStyle();
            Font timeHeaderFont = workbook.createFont();
            timeHeaderFont.setBold(true);
            timeHeaderFont.setFontHeightInPoints((short) 14);
            timeHeaderStyle.setFont(timeHeaderFont);
            timeHeaderCell.setCellStyle(timeHeaderStyle);
            sheet.addMergedRegion(new CellRangeAddress(rowNum - 1, rowNum - 1, 0, 3));
            
            long totalDuration = java.time.Duration.between(startTime, endTime).getSeconds();
            long avgDuration = totalDuration / total;
            createInfoRow(sheet, rowNum++, "Tổng thời gian:", formatDuration(totalDuration));
            createInfoRow(sheet, rowNum++, "Thời gian trung bình mỗi test:", formatDuration(avgDuration));
        }
        
        // Auto-size columns
        for (int i = 0; i < 4; i++) {
            sheet.autoSizeColumn(i);
        }
    }
    
    /**
     * Tạo sheet Execution Timeline
     */
    private void createTimelineSheet(Sheet sheet, Workbook workbook) {
        int rowNum = 0;
        
        // Title
        Row titleRow = sheet.createRow(rowNum++);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("⏱️ EXECUTION TIMELINE");
        CellStyle titleStyle = workbook.createCellStyle();
        Font titleFont = workbook.createFont();
        titleFont.setBold(true);
        titleFont.setFontHeightInPoints((short) 16);
        titleStyle.setFont(titleFont);
        titleCell.setCellStyle(titleStyle);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 4));
        
        rowNum += 2;
        
        // Header
        Row headerRow = sheet.createRow(rowNum++);
        String[] headers = {"#", "Test Name", "Status", "Start Time", "Duration"};
        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setColor(IndexedColors.WHITE.getIndex());
        headerStyle.setFont(headerFont);
        headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }
        
        // Data - sắp xếp theo thời gian
        List<TestResult> sortedResults = new ArrayList<>(testResults);
        sortedResults.sort((a, b) -> a.timestamp.compareTo(b.timestamp));
        
        LocalDateTime previousTime = startTime;
        int testNum = 1;
        for (TestResult result : sortedResults) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(testNum++);
            row.createCell(1).setCellValue(result.testName);
            
            Cell statusCell = row.createCell(2);
            statusCell.setCellValue(result.status);
            CellStyle statusStyle = workbook.createCellStyle();
            Font statusFont = workbook.createFont();
            statusFont.setBold(true);
            if ("PASS".equals(result.status)) {
                statusFont.setColor(IndexedColors.GREEN.getIndex());
            } else if ("FAIL".equals(result.status)) {
                statusFont.setColor(IndexedColors.RED.getIndex());
            } else {
                statusFont.setColor(IndexedColors.ORANGE.getIndex());
            }
            statusStyle.setFont(statusFont);
            statusCell.setCellStyle(statusStyle);
            
            row.createCell(3).setCellValue(
                result.timestamp.format(DateTimeFormatter.ofPattern("HH:mm:ss")));
            
            // Tính duration (giả sử mỗi test mất khoảng 5-10 giây)
            long duration = 5; // Default
            if (testNum > 1) {
                duration = java.time.Duration.between(previousTime, result.timestamp).getSeconds();
                if (duration < 0) duration = 5; // Fallback
            }
            row.createCell(4).setCellValue(duration + "s");
            previousTime = result.timestamp;
        }
        
        // Auto-size columns
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }
    
    /**
     * Tạo sheet Failed Tests
     */
    private void createFailedTestsSheet(Sheet sheet, Workbook workbook) {
        int rowNum = 0;
        
        // Title
        Row titleRow = sheet.createRow(rowNum++);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("❌ FAILED TESTS - CHI TIẾT LỖI");
        CellStyle titleStyle = workbook.createCellStyle();
        Font titleFont = workbook.createFont();
        titleFont.setBold(true);
        titleFont.setFontHeightInPoints((short) 16);
        titleFont.setColor(IndexedColors.RED.getIndex());
        titleStyle.setFont(titleFont);
        titleCell.setCellStyle(titleStyle);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 4));
        
        rowNum += 2;
        
        // Header
        Row headerRow = sheet.createRow(rowNum++);
        String[] headers = {"Test Name", "Description", "Error Message", "Screenshot", "Timestamp"};
        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setColor(IndexedColors.WHITE.getIndex());
        headerStyle.setFont(headerFont);
        headerStyle.setFillForegroundColor(IndexedColors.RED.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }
        
        // Data - chỉ lấy failed tests
        List<TestResult> failedResults = testResults.stream()
            .filter(r -> "FAIL".equals(r.status))
            .collect(Collectors.toList());
        
        for (TestResult result : failedResults) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(result.testName);
            row.createCell(1).setCellValue(result.description != null ? result.description : "");
            
            Cell errorCell = row.createCell(2);
            errorCell.setCellValue(result.errorMessage != null ? result.errorMessage : "No error message");
            CellStyle errorStyle = workbook.createCellStyle();
            Font errorFont = workbook.createFont();
            errorFont.setColor(IndexedColors.RED.getIndex());
            errorStyle.setFont(errorFont);
            errorCell.setCellStyle(errorStyle);
            
            row.createCell(3).setCellValue(result.screenshotPath != null ? result.screenshotPath : "No screenshot");
            row.createCell(4).setCellValue(
                result.timestamp.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        }
        
        // Auto-size columns
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }
    
    /**
     * Helper method để tạo stat row
     */
    private void createStatRow(Sheet sheet, int rowNum, String label, int count, double percent, 
                              String statusType, Workbook workbook) {
        Row row = sheet.createRow(rowNum);
        row.createCell(0).setCellValue(label);
        row.createCell(1).setCellValue(count);
        row.createCell(2).setCellValue(String.format("%.2f%%", percent));
        
        Cell statusCell = row.createCell(3);
        statusCell.setCellValue(statusType);
        CellStyle statusStyle = workbook.createCellStyle();
        Font statusFont = workbook.createFont();
        statusFont.setBold(true);
        if ("PASS".equals(statusType)) {
            statusFont.setColor(IndexedColors.GREEN.getIndex());
        } else if ("FAIL".equals(statusType)) {
            statusFont.setColor(IndexedColors.RED.getIndex());
        } else if ("SKIP".equals(statusType)) {
            statusFont.setColor(IndexedColors.ORANGE.getIndex());
        }
        statusStyle.setFont(statusFont);
        statusCell.setCellStyle(statusStyle);
    }
    
    /**
     * Helper method để format duration
     */
    private String formatDuration(long seconds) {
        if (seconds < 60) {
            return seconds + " giây";
        } else {
            long minutes = seconds / 60;
            long secs = seconds % 60;
            return minutes + " phút " + secs + " giây";
        }
    }
    
    /**
     * Helper method để tạo info row
     */
    private void createInfoRow(Sheet sheet, int rowNum, String label, String value) {
        Row row = sheet.createRow(rowNum);
        Cell labelCell = row.createCell(0);
        labelCell.setCellValue(label);
        CellStyle labelStyle = sheet.getWorkbook().createCellStyle();
        Font labelFont = sheet.getWorkbook().createFont();
        labelFont.setBold(true);
        labelStyle.setFont(labelFont);
        labelCell.setCellStyle(labelStyle);
        
        Cell valueCell = row.createCell(1);
        valueCell.setCellValue(value);
    }
    
    /**
     * Inner class để lưu test result
     */
    private static class TestResult {
        String testName;
        String status;
        String description;
        List<String> steps;
        String errorMessage;
        String screenshotPath;
        LocalDateTime timestamp;
    }
}


