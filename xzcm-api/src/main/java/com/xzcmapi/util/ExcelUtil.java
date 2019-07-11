package com.xzcmapi.util;

import com.xzcmapi.annotation.ExcelAnno;
import com.xzcmapi.common.BaseObject;
import com.xzcmapi.entity.TemplateExcelInfo;
import com.xzcmapi.util.models.CellError;
import com.xzcmapi.util.models.ExcelSheetObj;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.*;

/**
 * @Description: Excel读写操作
 */
public class ExcelUtil {
    private final static Logger logger = LoggerFactory.getLogger(ExcelUtil.class);

    /**
     * @param filePath  文件路径
     * @param dataClass 接收数据数组
     * @param startRow  开始行
     * @param startCol  开始列
     * @return 返回以sheet页名称为Key的数据Map
     * @throws Exception
     */
    public static Map<String, ExcelSheetObj> parseExcel(String filePath, Class<?>[] dataClass, int[] startRow, int[] startCol, List<TemplateExcelInfo> templateExcelInfos) throws Exception {
        logger.info("线程 {} 开始解析Excel..............................................", Thread.currentThread());
        long startTime = System.currentTimeMillis();
        Workbook workbook = null;
        Map<String, ExcelSheetObj> excelSheetObjMap = new HashMap<>();
        try {
            workbook = newWKInstance(filePath);
            LinkedHashMap<String, Sheet> sheetLinkedHashMap = getExcelSheets(workbook);
            if (Objects.isNull(sheetLinkedHashMap)) {
                return null;
            }
            Iterator<Map.Entry<String, Sheet>> iterator = sheetLinkedHashMap.entrySet().iterator();
            int sheetNum = 0;
            while (iterator.hasNext()) {
                Map.Entry entry = iterator.next();
                String sheetName = (String) entry.getKey();
                Sheet sheet = (Sheet) entry.getValue();
                logger.info("线程 {} 解析：{} sheet页数据", Thread.currentThread(), sheetName);
                try {
                    ExcelSheetObj excelSheetObj = parseSheet(sheet, startRow[sheetNum], startCol[sheetNum], dataClass[sheetNum], templateExcelInfos);
                    excelSheetObjMap.put(sheetName, excelSheetObj);
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
                sheetNum++;
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new Exception("获取文件输入流失败");
        } finally {
            closeWorkBook(workbook);
        }
        logger.info("线程 {} 结束Excel解析,耗时：{} 秒", Thread.currentThread(), (System.currentTimeMillis() - startTime) / 1000);
        return excelSheetObjMap;
    }


    /**
     * 解析单sheet页的Excel
     *
     * @param filePath
     * @param dataClass
     * @param startRow
     * @param startCol
     * @return
     */
    public static ExcelSheetObj parseExcelSingle(String filePath, Class<?>[] dataClass, int[] startRow, int[] startCol) throws Exception {
        logger.info("线程 {} 开始解析Excel..............................................", Thread.currentThread());
        long startTime = System.currentTimeMillis();
        Workbook workbook = null;
        ExcelSheetObj excelSheetObj = null;
        InputStream inputStream = null;
        try {
            String fileType = filePath.substring(filePath.lastIndexOf('.') + 1);
            HttpHeaders headers = new HttpHeaders();
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<byte[]> response = restTemplate.exchange(filePath,
                    HttpMethod.GET, new HttpEntity<byte[]>(headers),
                    byte[].class);

            byte[] result = response.getBody();
            inputStream = new ByteArrayInputStream(result);
            if ("xls".equals(fileType)) {
                workbook = new HSSFWorkbook(new POIFSFileSystem(inputStream));//支持低版本的Excel文件
            } else if ("xlsx".equals(fileType)) {
                workbook = new XSSFWorkbook(inputStream);
            } else {
                throw new Exception("不是Excel文件不能解析");
            }
            LinkedHashMap<String, Sheet> sheetLinkedHashMap = getExcelSheets(workbook);
            if (sheetLinkedHashMap == null || sheetLinkedHashMap.isEmpty()) {
                throw new Exception("解析内容为空");
            }
            Iterator<Map.Entry<String, Sheet>> iterator = sheetLinkedHashMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry entry = iterator.next();
                String sheetName = (String) entry.getKey();
                Sheet sheet = (Sheet) entry.getValue();
                logger.info("线程 {} 解析:{}sheet页数据", Thread.currentThread(), sheetName);
                try {
                    excelSheetObj = parseSheet(sheet, startRow[0], startCol[0], dataClass[0]);
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            closeWorkBook(workbook);
            if (inputStream != null) {
                inputStream.close();
            }
        }
        logger.info("线程 {} 结束Excel解析,耗时：{} 秒", Thread.currentThread(), (System.currentTimeMillis() - startTime) / 1000);
        return excelSheetObj;
    }

    public static ExcelSheetObj parseExcelByFile(MultipartFile file, Class<?>[] dataClass, int[] startRow, int[] startCol) throws Exception {
        logger.info("线程 {} 开始解析Excel..............................................", Thread.currentThread());
        long startTime = System.currentTimeMillis();
        Workbook workbook = null;
        ExcelSheetObj excelSheetObj = null;
        InputStream inputStream = null;
        try {
            String OriginalFilename = file.getOriginalFilename();
            //上传文件的扩展名
            String fileType = OriginalFilename.substring(OriginalFilename.lastIndexOf('.') + 1);

            inputStream = new BufferedInputStream(file.getInputStream());
            if ("xls".equals(fileType)) {
                workbook = new HSSFWorkbook(new POIFSFileSystem(inputStream));//支持低版本的Excel文件
            } else if ("xlsx".equals(fileType)) {
                workbook = new XSSFWorkbook(inputStream);
            } else {
                throw new Exception("不是Excel文件不能解析");
            }
            LinkedHashMap<String, Sheet> sheetLinkedHashMap = getExcelSheets(workbook);
            if (sheetLinkedHashMap == null || sheetLinkedHashMap.isEmpty()) {
                throw new Exception("解析内容为空");
            }
            Iterator<Map.Entry<String, Sheet>> iterator = sheetLinkedHashMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry entry = iterator.next();
                String sheetName = (String) entry.getKey();
                Sheet sheet = (Sheet) entry.getValue();
                logger.info("线程 {} 解析:{}sheet页数据", Thread.currentThread(), sheetName);
                try {
                    excelSheetObj = parseSheet(sheet, startRow[0], startCol[0], dataClass[0]);
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            closeWorkBook(workbook);
            if (inputStream != null) {
                inputStream.close();
            }
        }
        logger.info("线程 {} 结束Excel解析,耗时：{} 秒", Thread.currentThread(), (System.currentTimeMillis() - startTime) / 1000);
        return excelSheetObj;
    }


    /**
     * 解析单sheet页的Excel
     *
     * @param filePath
     * @param dataClass
     * @param startRow
     * @param startCol
     * @return
     */
    public static ExcelSheetObj parseExcelSingle(String filePath, Class<?>[] dataClass, int[] startRow, int[] startCol, List<TemplateExcelInfo> templateExcelInfos) throws Exception {
        logger.info("线程 {} 开始解析Excel..............................................", Thread.currentThread());
        long startTime = System.currentTimeMillis();
        Workbook workbook = null;
        ExcelSheetObj excelSheetObj = null;
        InputStream inputStream = null;
        try {
            String fileType = filePath.substring(filePath.lastIndexOf('.') + 1);
            HttpHeaders headers = new HttpHeaders();
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<byte[]> response = restTemplate.exchange(filePath,
                    HttpMethod.GET, new HttpEntity<byte[]>(headers),
                    byte[].class);

            byte[] result = response.getBody();
            inputStream = new ByteArrayInputStream(result);
            if ("xls".equals(fileType)) {
                workbook = new HSSFWorkbook(new POIFSFileSystem(inputStream));//支持低版本的Excel文件
            } else if ("xlsx".equals(fileType)) {
                workbook = new XSSFWorkbook(inputStream);
            } else {
                throw new Exception("不是Excel文件不能解析");
            }
            LinkedHashMap<String, Sheet> sheetLinkedHashMap = getExcelSheets(workbook);
            if (sheetLinkedHashMap == null || sheetLinkedHashMap.isEmpty()) {
                throw new Exception("解析内容为空");
            }
            Iterator<Map.Entry<String, Sheet>> iterator = sheetLinkedHashMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry entry = iterator.next();
                String sheetName = (String) entry.getKey();
                Sheet sheet = (Sheet) entry.getValue();
                logger.info("线程 {} 解析:{}sheet页数据", Thread.currentThread(), sheetName);
                try {
                    excelSheetObj = parseSheet(sheet, startRow[0], startCol[0], dataClass[0], templateExcelInfos);
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            closeWorkBook(workbook);
            if (inputStream != null) {
                inputStream.close();
            }
        }
        logger.info("线程 {} 结束Excel解析,耗时：{} 秒", Thread.currentThread(), (System.currentTimeMillis() - startTime) / 1000);
        return excelSheetObj;
    }

    /**
     * 关闭文件流
     *
     * @param workbook
     * @throws Exception
     */
    private static void closeWorkBook(Workbook workbook) throws Exception {
        if (workbook != null) {
            try {
                workbook.close();
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    /**
     * 创建WorkBook实例，支持.xla和xlsx
     *
     * @param filePath:完整文件路径
     * @return
     */
    public static Workbook newWKInstance(String filePath) throws Exception {
        InputStream inputStream;
        String fileType = filePath.substring(filePath.lastIndexOf('.') + 1);
        HttpHeaders headers = new HttpHeaders();
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<byte[]> response = restTemplate.exchange(filePath,
                HttpMethod.GET, new HttpEntity<byte[]>(headers),
                byte[].class);

        byte[] result = response.getBody();
        inputStream = new ByteArrayInputStream(result);
        if ("xls".equals(fileType)) {
            return new HSSFWorkbook(new POIFSFileSystem(inputStream));//支持低版本的Excel文件
        } else if ("xlsx".equals(fileType)) {
            return new XSSFWorkbook(inputStream);
        } else {
            throw new Exception("不是Excel文件不能解析");
        }

    }

    /**
     * 获取Excel中的每个sheet页，
     *
     * @param workbook
     * @return 返回按Excel实际顺序的sheet对象
     */
    public static LinkedHashMap<String, Sheet> getExcelSheets(Workbook workbook) {
        LinkedHashMap<String, Sheet> excelSheets = new LinkedHashMap<>();
        for (int sheetIndex = 0; sheetIndex < workbook.getNumberOfSheets(); sheetIndex++) {
            String sheetName = workbook.getSheetName(sheetIndex);
            Sheet sheet = workbook.getSheetAt(sheetIndex);
            excelSheets.put(sheetName, sheet);
        }
        if (excelSheets.isEmpty()) {
            excelSheets = null;
        }
        return excelSheets;
    }


    /**
     * 解析sheet页数据
     *
     * @param sheet
     * @param startRow
     * @param startCol
     * @param dataClass 接收数据的实体对象
     * @return 返回当前sheet的数据
     */
    public static ExcelSheetObj parseSheet(Sheet sheet, int startRow, int startCol, Class<?> dataClass) throws Exception {
        ExcelSheetObj excelSheetObj = new ExcelSheetObj();
        List objList = new ArrayList();
        List<CellError> cellErrorList = new ArrayList<>();
        String sheetName = sheet.getSheetName();
        //读取该sheet页的标题信息
        Row titleRow = sheet.getRow(startRow);
        //获取每个sheet页的头部信息,用于和实体属性匹配
        Map<Integer, String> headerMap = null;
        try {
            headerMap = parseExcelHeader(startCol, titleRow);
        } catch (Exception e) {
            throw e;
        }
        if (headerMap == null) {
            logger.info("{} 解析【{}】sheet 页的头部信息失败，请检查该sheet页的模板格式", Thread.currentThread(), sheetName);
        } else {
            logger.info("{} 解析【{}】sheet 页的头部信息:{}", Thread.currentThread(), sheetName, headerMap.toString());
            //解析数据行
            for (int rowIndex = startRow + 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                try {
                    //获取每一行的数据
                    Row dataRow = sheet.getRow(rowIndex);
                    if (!isBlankRow(dataRow)) {
                        //解析一行中每一列数据
                        Object obj = parseRow(dataClass, dataRow, startCol, headerMap, cellErrorList, sheetName, rowIndex);
                        if (null != obj) {
                            objList.add(obj);
                        }
                    }
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
        excelSheetObj.setSheetName(sheetName);
        excelSheetObj.setDatasList(objList);
        excelSheetObj.setCellErrorList(cellErrorList);
        return excelSheetObj;
    }


    /**
     * 解析sheet页数据
     *
     * @param sheet
     * @param startRow
     * @param startCol
     * @param dataClass 接收数据的实体对象
     * @return 返回当前sheet的数据
     */
    public static ExcelSheetObj parseSheet(Sheet sheet, int startRow, int startCol, Class<?> dataClass, List<TemplateExcelInfo> templateExcelInfos) throws Exception {
        ExcelSheetObj excelSheetObj = new ExcelSheetObj();
        List objList = new ArrayList();
        List<CellError> cellErrorList = new ArrayList<>();
        String sheetName = sheet.getSheetName();
        //解析数据行
        //注意 当Excel 表头和数据正常的时候，getlastrownum 获取的行数是正确的，当表头和数据不规则时， 这个方法会自动过滤掉空格，导致行数少于原行数，需要特殊处理。
        int rowCount = 0;
        if (startRow == 2) {
            rowCount = sheet.getLastRowNum();
        } else {
            rowCount = sheet.getLastRowNum() + (startRow - 3);
        }
        for (int rowIndex = startRow - 1; rowIndex <= rowCount; rowIndex++) {
            try {
                //获取每一行的数据
                Row dataRow = sheet.getRow(rowIndex);
                if (!isBlankRow(dataRow)) {
                    //解析一行中每一列数据
                    Object obj = parseRow(dataClass, dataRow, startCol, templateExcelInfos, cellErrorList, sheetName, rowIndex);
                    if (null != obj) {
                        objList.add(obj);
                    }
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }

        }
        excelSheetObj.setSheetName(sheetName);
        excelSheetObj.setDatasList(objList);
        excelSheetObj.setCellErrorList(cellErrorList);
        return excelSheetObj;
    }


    /**
     * @param dataClass  数据实体
     * @param dataRow    数据行
     * @param startCol   开始列
     * @param headerMap  头部信息
     * @param cellErrors 错误信息
     * @param sheetName
     * @param rowIndex   行数
     * @return 返回实体对象
     */
    public static Object parseRow(Class<?> dataClass, Row dataRow, int startCol, Map<Integer, String> headerMap, List<CellError> cellErrors, String sheetName, int rowIndex) {
        //反射创建实体对象
        Object obj = null;
        try {

            obj = dataClass.newInstance();
            for (int colIndex = startCol; colIndex < dataRow.getLastCellNum(); colIndex++) {
                //获取该列对应的头部信息中文
                String titleName = headerMap.get(colIndex);
                Cell cell = dataRow.getCell(colIndex);
                if (cell != null && !cell.toString().trim().equals("")) {
                    //获取类中所有的字段
                    Field[] fields = dataClass.getDeclaredFields();
                    int fieldCount = 0;
                    for (Field field : fields) {
                        fieldCount++;
                        //获取标记了ExcelAnno的注解字段
                        if (field.isAnnotationPresent(ExcelAnno.class)) {
                            ExcelAnno f = field.getAnnotation(ExcelAnno.class);
                            //实体中注解的属性名称
                            String cellName = f.cellName();
                            if (cellName != null && !cellName.isEmpty()) {
                                //匹配到实体中相应的字段
                                if (chineseCompare(cellName, titleName, "UTF-8")) {
                                    //打开实体中私有变量的权限
                                    field.setAccessible(true);
                                    //实体中变量赋值
                                    try {
                                        field.set(obj, getObj(field.getType(), cell));
                                    } catch (Exception e) {
                                        String errorMsg = "第[" + dataRow.getRowNum() + "]行，字段:[" + cellName + "]的数据类型不正确";
                                        CellError errorObj = new CellError(sheetName, rowIndex, colIndex, null, titleName, errorMsg, e);
                                        cellErrors.add(errorObj);
                                    }
                                    break;
                                }
                            } else {
                                logger.info(Thread.currentThread() + "实体：" + obj.getClass().getSimpleName() + "中的：" + field.getName() + " 未配置cellName属性");
                                continue;
                            }
                            if (fieldCount == fields.length) {
                                //标明没有找到匹配的属性字段
                                logger.info(Thread.currentThread() + "模板中的：" + sheetName + "[" + titleName + "]未与实体：" + obj.getClass().getSimpleName() + " 对应");
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return obj;
    }

    /**
     * @param dataClass  数据实体
     * @param dataRow    数据行
     * @param startCol   开始列
     * @param cellErrors 错误信息
     * @param sheetName
     * @param rowIndex   行数
     * @return 返回实体对象
     */

    public static Object parseRow(Class<?> dataClass, Row dataRow, int startCol, List<TemplateExcelInfo> templateExcelInfos, List<CellError> cellErrors, String sheetName, int rowIndex) {
        //反射创建实体对象
        Object obj = null;
        try {
            obj = dataClass.newInstance();
            if (dataRow.getLastCellNum() == templateExcelInfos.size()) {
                for (int colIndex = startCol; colIndex < dataRow.getLastCellNum(); colIndex++) {
                    Cell cell = dataRow.getCell(colIndex);
                    if (cell != null && !cell.toString().trim().equals("")) {
                        //获取类中所有的字段
                        Field[] fields = dataClass.getDeclaredFields();
                        for (Field field : fields) {
                            //对数据库中取到的实体字段和mongodb中的字段进行对比
                            String result = getRelateName(templateExcelInfos, colIndex + 1);
                            if (Objects.nonNull(result) && result.equals(field.getName())) {
                                field.setAccessible(true);
                                //实体中变量赋值
                                try {
                                    field.set(obj, getObj(field.getType(), cell));
                                } catch (Exception e) {
                                    String errorMsg = "第[" + dataRow.getRowNum() + "]行，字段:[" + field + "]的数据类型不正确";
                                    CellError errorObj = new CellError(sheetName, rowIndex, colIndex, null, "titlename", errorMsg, e);
                                    cellErrors.add(errorObj);
                                }
                                break;
                            } else {
                                logger.info(Thread.currentThread() + "实体：" + obj.getClass().getSimpleName() + "中的：" + field.getName() + " 未配置cellName属性");
                                continue;
                            }
                        }
                    }
                }
            } else {
                CellError errorObj = new CellError(sheetName, rowIndex, 0, null, "titlename", "数据格式和模板格式不一致", null);
                cellErrors.add(errorObj);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return obj;
    }

    private static String getRelateName(List<TemplateExcelInfo> templateExcelInfos, int number) {
        String column = excelColIndexToStr(number);
        String result = null;
        for (TemplateExcelInfo templateExcelInfo : templateExcelInfos) {
            if (templateExcelInfo.getCellNum().equals(column)) {
                result = templateExcelInfo.getRelateName();
                break;
            }
        }
        return result;
    }

    /**
     * Excel 中将数字转化为字符 如 1 转为 A
     *
     * @param columnIndex
     * @return
     */
    private static String excelColIndexToStr(int columnIndex) {
        if (columnIndex <= 0) {
            return null;
        }
        String columnStr = "";
        columnIndex--;
        do {
            if (columnStr.length() > 0) {
                columnIndex--;
            }
            columnStr = ((char) (columnIndex % 26 + (int) 'A')) + columnStr;
            columnIndex = (int) ((columnIndex - columnIndex % 26) / 26);
        } while (columnIndex > 0);
        return columnStr;
    }

    /**
     * Excel 中将字母转化为数字 如A -> 1
     *
     * @param column
     * @return
     */
    public static Integer excelColStrToNum(String column) {
        int num = 0;
        int result = 0;
        int length = column.length();
        for (int i = 0; i < length; i++) {
            char ch = column.charAt(length - i - 1);
            num = (int) (ch - 'A' + 1);
            num *= Math.pow(26, i);
            result += num;
        }
        return result;
    }


    /**
     * 判断Excel中是否为空行
     *
     * @param dataRow
     * @return
     */
    public static boolean isBlankRow(Row dataRow) {
        if (Objects.nonNull(dataRow)) {
            for (int i = dataRow.getFirstCellNum(); i < dataRow.getLastCellNum(); i++) {
                Cell cell = dataRow.getCell(i);
                if (cell != null && cell.getCellType() != Cell.CELL_TYPE_BLANK)
                    return false;
            }
            return true;
        }
        return true;
    }

    /**
     * @param clazz 实体中属性字段的类型
     * @param cell  Excel中的单元格
     * @return 返回单元格对应的实体
     * @throws Exception
     */
    public static Object getObj(Class clazz, Cell cell) throws Exception {
        if (cell == null) {
            return null;
        }
        String cellValue = getCellValue(cell);
        if (cellValue == null) {
            return null;
        }
        if ("java.util.Date".equalsIgnoreCase(clazz.getName())) {
            if (cellValue.matches("\\d{4}/\\d{2}/\\d{2}"))
                return XzcmDateUtil.getUtilDate(cellValue, "yyyy/MM/dd");
            else if (cellValue.matches("\\d{4}-\\d{2}-\\d{2}"))
                return XzcmDateUtil.getUtilDate(cellValue, BaseObject.DATE_FORMAT);
            else
                throw new RuntimeException("日期格式不合适");
        } else if ("java.lang.Integer".equalsIgnoreCase(clazz.getName())) {
            return Integer.parseInt(cellValue);
        } else if ("java.lang.Short".equalsIgnoreCase(clazz.getName())) {
            return Short.parseShort(cellValue);
        } else if ("java.lang.Double".equalsIgnoreCase(clazz.getName())) {
            BigDecimal big = new BigDecimal(cellValue);
            return big.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        } else if ("java.math.BigDecimal".equalsIgnoreCase(clazz.getName())) {
            return new BigDecimal(cellValue);
        } else if ("java.lang.Boolean".equalsIgnoreCase(clazz.getName())) {
            return chineseCompare(cellValue, "是", "UTF-8");
        } else {
            Constructor con = clazz.getConstructor(String.class);
            return con.newInstance(cellValue);
        }
    }

    /**
     * 解析每个Sheet页的头部信息
     *
     * @param startCol 开始列
     * @param titleRow 头部信息所在的行
     * @return
     */
    public static Map<Integer, String> parseExcelHeader(int startCol, Row titleRow) throws Exception {
        try {
            int lastCellNum = titleRow.getLastCellNum();
            if (startCol > lastCellNum) {
                return null;
            }
            Map<Integer, String> headerMap = new HashMap<>();
            for (int columnIndex = startCol; columnIndex < titleRow.getLastCellNum(); columnIndex++) {
                Cell cell = titleRow.getCell(columnIndex);
                headerMap.put(columnIndex, cell.getStringCellValue());
            }
            return headerMap;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new Exception(Thread.currentThread() + "解析头部信息失败");
        }
    }

    /**
     * 中文字符串比较
     *
     * @param str1
     * @param str2
     * @param engCode 字符编码
     * @return
     * @throws Exception
     */
    private static Boolean chineseCompare(String str1, String str2, String engCode) throws Exception {
        String tmpStr1 = new String(str1.getBytes(engCode));
        String tmpStr2 = new String(str2.getBytes(engCode));
        if (tmpStr1.equals(tmpStr2)) {
            return true;
        }
        return false;
    }

    /**
     * 获取单元格的值
     *
     * @param cell
     * @return
     */
    public static String getCellValue(Cell cell) {
        if (cell == null) {
            return null;
        }
        String cellValue = null;
        //根据CellTYpe动态获取Excel中的值
        switch (cell.getCellType()) {
            case Cell.CELL_TYPE_BLANK:
                cellValue = "";
                break;
            case Cell.CELL_TYPE_ERROR:
                cellValue = "";
                break;
            case Cell.CELL_TYPE_BOOLEAN:
                cellValue = String.valueOf(cell.getBooleanCellValue());
                break;
            case Cell.CELL_TYPE_NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    cellValue = String.valueOf(XzcmDateUtil.fomratterDate(cell.getDateCellValue(), BaseObject.DATE_FORMAT));
                } else {
                    DecimalFormat df = new DecimalFormat("#.#########");
                    cellValue = df.format(cell.getNumericCellValue());
                }
                break;
            case Cell.CELL_TYPE_FORMULA:
                if (DateUtil.isCellDateFormatted(cell)) {
                    cellValue = String.valueOf(XzcmDateUtil.fomratterDate(cell.getDateCellValue(), BaseObject.DATE_FORMAT));
                } else {
                    cellValue = String.valueOf(cell.getNumericCellValue());
                }
                break;
            case Cell.CELL_TYPE_STRING:
                cellValue = StringUtils.trim(cell.getStringCellValue());
                break;
            default:
                break;
        }
        return cellValue;
    }

    /**
     * 生成Excel
     *
     * @param dataList  数据List
     * @param titleList 模板头文件
     * @param proNames  实体属性list
     * @param startRow  开始行
     * @param startCol  开始列
     * @return
     * @throws Exception
     */
    public static void createExcel(Workbook workbook,
                                   Sheet sheet,
                                   List dataList,
                                   String[] titleList,
                                   String[] proNames,
                                   int startRow,
                                   int startCol,
                                   String tableName) throws Exception {
        CellStyle headStyle = workbook.createCellStyle();
        CellStyle BodyStyle = workbook.createCellStyle();
        headStyle.setAlignment(HorizontalAlignment.CENTER); // 居中
        BodyStyle.setAlignment(HorizontalAlignment.CENTER);
        //设置头文件字体
        Font fontTitle = workbook.createFont();
        fontTitle.setFontName("黑体");
        fontTitle.setFontHeightInPoints((short) 12);//设置字体大小
        headStyle.setFont(fontTitle);
        Font fontBody = workbook.createFont();
        fontBody.setFontName("宋体");
        fontBody.setFontHeightInPoints((short) 11);
        BodyStyle.setFont(fontBody);

        //设置表名字体格式
        CellStyle titleStyle = workbook.createCellStyle();
        titleStyle.setAlignment(HorizontalAlignment.CENTER);  //文字居中
        Font titleFont = workbook.createFont();
        titleFont.setFontName("黑体");   //字体
        titleFont.setFontHeightInPoints((short) 20);  //字体大小
        titleStyle.setFont(titleFont);

        //写入活动名称
        CellRangeAddress cra = new CellRangeAddress(0, 0, 0, 6);//合并单元格
        sheet.addMergedRegion(cra);
        Row row1 = sheet.createRow(0);
        Cell cell1 = row1.createCell(0);
        cell1.setCellValue(tableName);
        cell1.setCellStyle(titleStyle);

        //写入头文件
        if (Objects.nonNull(titleList)) {
            Row row = sheet.createRow((short) startRow);
            Cell cell = null;
            for (int j = 0; j < titleList.length; j++) {
                cell = row.createCell((short) (startCol + j));
                cell.setCellValue(titleList[j]);
                cell.setCellStyle(headStyle);
            }
        }
        if (!dataList.isEmpty()) {
            int irowNum = startRow + 1;
            for (int i = 0; i < dataList.size(); i++) {
                Object obj = dataList.get(i);
                Row row = sheet.createRow(irowNum + i);
                for (int k = 0; k < proNames.length; k++) {
                    String exportItem = proNames[k];
                    String valueStr = null;
                    if ((getProValue(exportItem, obj)) instanceof Date) {
                        valueStr = XzcmDateUtil.fomratterDate((Date) getProValue(exportItem, obj),null);
                    } else if ((getProValue(exportItem, obj)) instanceof Integer) {
                        valueStr = ((Integer) Objects.requireNonNull(getProValue(exportItem, obj))).toString();
                    } else if ((getProValue(exportItem, obj)) instanceof Long) {
                        valueStr = ((Long) Objects.requireNonNull(getProValue(exportItem, obj))).toString();
                    } else if ((getProValue(exportItem, obj)) instanceof ArrayList) {
                        valueStr = (Objects.requireNonNull(getProValue(exportItem, obj))).toString();
                    } else if ((getProValue(exportItem, obj)) instanceof BigDecimal){
                        valueStr = (Objects.requireNonNull(getProValue(exportItem, obj))).toString();
                    }else {
                        valueStr = (String) getProValue(exportItem, obj);
                    }
                    Cell cell = row.createCell(startCol + k, CellType.STRING);
                    cell.setCellValue(valueStr);
                    cell.setCellStyle(BodyStyle);
                }
            }
        }
        sheet.autoSizeColumn((short) 0, true); //调整第一列宽度
        sheet.autoSizeColumn((short) 1);
        sheet.autoSizeColumn((short) 2);
        sheet.autoSizeColumn((short) 3);
        sheet.autoSizeColumn((short) 4);
        sheet.autoSizeColumn((short) 5);
        sheet.autoSizeColumn((short) 6);
    }

    public static Object getProValue(String exportItem, Object obj) {
        if (obj instanceof HashMap) {
            return ((HashMap) obj).get(exportItem);
        } else {
            String firstLetter = exportItem.substring(0, 1).toUpperCase();
            String getMethodName = "get" + firstLetter + exportItem.substring(1);
            Method getMethod = null;
            try {
                getMethod = obj.getClass().getMethod(getMethodName, new Class[]{});
                return getMethod.invoke(obj, new Object[]{});
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

}

