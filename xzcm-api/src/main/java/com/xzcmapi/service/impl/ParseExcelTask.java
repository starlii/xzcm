package com.xzcmapi.service.impl;

import com.xzcmapi.annotation.ExcelAnno;
import com.xzcmapi.common.Constant;
import com.xzcmapi.entity.DataInfoExcel;
import com.xzcmapi.entity.RowError;
import com.xzcmapi.entity.TemplateExcelInfo;
import com.xzcmapi.model.ColumnError;
import com.xzcmapi.model.DataImportRecord;
import com.xzcmapi.service.PlayerService;
import com.xzcmapi.util.XzcmDateUtil;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class ParseExcelTask {

    private final Logger logger = LoggerFactory.getLogger(ParseExcelTask.class);

    @Async
    public void parseRow(Class<?> dataClass, Row dataRow, int startCol, Map<Integer, String> headerMap, RowError rowError,
                         DataImportRecord dataImportRecord, int rowIndex, List<TemplateExcelInfo> templateExcelInfos,
                         CopyOnWriteArrayList<Integer> dataIndex, CopyOnWriteArrayList<DataInfoExcel> dataInfoExcelList,
                         CopyOnWriteArrayList<RowError> forceErrorList, CopyOnWriteArrayList<RowError> promptErrorList) {
        logger.debug("线程{}正在解析第{}行数据...", Thread.currentThread(), rowIndex);
        if (dataIndex.contains(rowIndex)) {
            return;
        }
        List flag = new ArrayList<>();
        List<ColumnError> columnErrorList = new ArrayList<>();

        //反射创建实体对象
        Object obj = null;
        rowError.setRowIndex(rowIndex + 1);
        try {
            obj = dataClass.newInstance();
            //默认数据模板
            if (Objects.isNull(templateExcelInfos)) {
                //循环解析每行中的每个单元格
                for (int colIndex = startCol; colIndex < dataRow.getLastCellNum(); colIndex++) {
                    //获取该列对应的头部信息中文
                    String titleName = headerMap.get(colIndex);
                    if (StringUtils.isNotBlank(titleName)) {
                        Cell cell = dataRow.getCell(colIndex);
                        matchFields(dataClass, columnErrorList, obj, rowIndex + 1, colIndex, titleName, cell, flag);
                    }
                }
            } else {
                //配置模板
                for (TemplateExcelInfo templateExcelInfo : templateExcelInfos) {
                    if (StringUtils.isNotBlank(templateExcelInfo.getRelateName())) {
                        Cell cell = dataRow.getCell(templateExcelInfo.getCellNum());
                        //获取类中所有的字段
                        Field[] fields = dataClass.getDeclaredFields();
                        for (Field field : fields) {
                            //实体中的属性名称
                            String proName = field.getName();
                            //匹配到实体中相应的字段
                            if (proName.equals(templateExcelInfo.getRelateName())) {
                                ColumnError columnError = new ColumnError();
                                columnError.setColumnIndex(templateExcelInfo.getCellNum() + 1);
                                columnError.setTitleMsg(templateExcelInfo.getCellName());
                                //打开实体中私有变量的权限
                                field.setAccessible(true);
                                // 获取数据或者错误信息
                                Map<Object, ColumnError> map = validityDataGetFieldValue(field, cell, columnError, flag);
                                Map.Entry<Object, ColumnError> next = map.entrySet().iterator().next();
                                field.set(obj, next.getKey());
                                if (StringUtils.isNotBlank(next.getValue().getErrorMsg())) {
                                    ColumnError value = next.getValue();
                                    columnErrorList.add(value);
                                }
                                break;
                            }
                        }
                    }
                }
            }
            if (!flag.isEmpty()) {
                DataInfoExcel dataInfoExcel = (DataInfoExcel) obj;
                saveDataInfoExcelAndError(dataInfoExcel, dataImportRecord, columnErrorList, rowError, rowIndex, dataInfoExcelList, forceErrorList, promptErrorList);
            }
        } catch (Exception e) {
            logger.error("线程解析第行{}数据错误", rowIndex);
        }
        dataIndex.add(rowIndex);
        logger.debug("线程{}解析完成第{}行数据...", Thread.currentThread(), rowIndex);
        return;
    }

    private void saveDataInfoExcelAndError(DataInfoExcel dataInfoExcel, DataImportRecord dataImportRecord,
                                           List<ColumnError> columnErrorList, RowError rowError, int rowIndex,
                                           CopyOnWriteArrayList<DataInfoExcel> dataInfoExcelList,
                                           CopyOnWriteArrayList<RowError> forceErrorList, CopyOnWriteArrayList<RowError> promptErrorList) throws Exception {
        if (!columnErrorList.isEmpty()) {
            rowError.setRowIndex(rowIndex + 1);
            StringBuilder forceErrorSb = new StringBuilder();
            StringBuilder promptErrorSb = new StringBuilder();
            int mark = DataInfoExcel.Color.NONE.getValue();
            List<Boolean> flagList = new ArrayList<>();
            for (ColumnError columnError : columnErrorList) {
                // 严重错误
                if (columnError.getErrorLevel().equals(ColumnError.ErrorLevel.FORCE.getValue())) {
                    forceErrorSb.append("列【".concat(excelColIndexToStr(columnError.getColumnIndex())).concat("】"));
                    forceErrorSb.append(columnError.getTitleMsg().concat(","));
                    forceErrorSb.append(columnError.getErrorMsg());
                    forceErrorSb.append("(".concat(ColumnError.ErrorLevel.FORCE.getRemark()).concat(")").concat(";"));
                    mark = DataInfoExcel.Color.RED.getValue();
                    flagList.add(false);
                }
                // 提醒错误
                if (columnError.getErrorLevel().equals(ColumnError.ErrorLevel.PROMPT.getValue())) {
                    flagList.add(true);
                    promptErrorSb.append("列【".concat(excelColIndexToStr(columnError.getColumnIndex())).concat("】"));
                    promptErrorSb.append(columnError.getTitleMsg().concat(","));
                    promptErrorSb.append(columnError.getErrorMsg());
                    promptErrorSb.append("(".concat(ColumnError.ErrorLevel.PROMPT.getRemark()).concat(")").concat(";"));
                    if (mark != DataInfoExcel.Color.RED.getValue()) {
                        mark = DataInfoExcel.Color.YELLOW.getValue();
                    }
                }
            }
            dataInfoExcel.setColor(mark);
            if (flagList.contains(false)) {
                rowError.setErrorMsg(forceErrorSb.toString());
                forceErrorList.add(rowError);
            } else {
                rowError.setErrorMsg(promptErrorSb.toString());
                promptErrorList.add(rowError);
            }
        }
        dataInfoExcelList.add(dataInfoExcel);
    }

    /**
     * 列转换为字母
     *
     * @param columnIndex
     * @return
     */
    public String excelColIndexToStr(int columnIndex) {
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

    private void matchFields(Class<?> dataClass, List<ColumnError> columnErrorList, Object obj, int rowIndex, int colIndex, String titleName, Cell cell, List flag) throws Exception {
        //获取类中所有的字段
        Field[] fields = dataClass.getDeclaredFields();
        int fieldCount = 0;
        boolean flagM = false;
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
                        flagM = true;
                        ColumnError columnError = new ColumnError();
                        columnError.setColumnIndex(colIndex + 1);
                        columnError.setTitleMsg(cellName);
                        //打开实体中私有变量的权限
                        field.setAccessible(true);
                        //实体中变量赋值
                        try {
                            // 获取数据或者错误信息
                            Map<Object, ColumnError> map = validityDataGetFieldValue(field, cell, columnError, flag);
                            Map.Entry<Object, ColumnError> next = map.entrySet().iterator().next();
                            if (StringUtils.isNotBlank(next.getValue().getErrorMsg())) {
                                ColumnError value = next.getValue();
                                columnErrorList.add(value);
                                break;
                            }
                            field.set(obj, next.getKey());
                        } catch (Exception e) {
                            logger.debug(e.getMessage());
                            throw new RuntimeException(e.getMessage());
                        }
                    }
                } else {
                    logger.info(Thread.currentThread() + "实体：" + obj.getClass().getSimpleName() + "中的：" + field.getName() + " 未配置cellName属性");
                    continue;
                }
                if (fieldCount == fields.length) {
                    //标明没有找到匹配的属性字段
                    logger.info(Thread.currentThread() + "模板中的：[" + titleName + "]未与实体：" + obj.getClass().getSimpleName() + " 对应");
                }
            }
        }
        if (flagM == false) {
            logger.info("行[{}]列[{}],表头[{}]未与任何实体属性匹配", rowIndex, colIndex, titleName);
        }
    }

    private Map<Object, ColumnError> validityDataGetFieldValue(Field field, Cell cell, ColumnError columnError, List flag) throws ParseException {
        Map<Object, ColumnError> map = new HashedMap(1);
        String cellValue = getCellValue(cell);
        cellValue = filterEmoji(cellValue, "");
        if (cellValue != null && !cellValue.equals("")) {
            flag.add(true);
        }
        ExcelAnno.FieldCheck fieldCheck = field.getAnnotation(ExcelAnno.class).fieldCheck();
        switch (fieldCheck) {
            case PLAYER_NAME:
                if (StringUtils.equalsIgnoreCase(cellValue, "")) {
                    columnError.setErrorMsg("选手姓名不能为空");
                    columnError.setErrorLevel(ColumnError.ErrorLevel.FORCE.getValue());
                    map.put(cellValue, columnError);
                    break;
                }
                map.put(cellValue, columnError);
                break;
            case PLAYER_DESC:
                if (StringUtils.equalsIgnoreCase(cellValue, "")) {
                    columnError.setErrorMsg("选后介绍不能为空");
                    columnError.setErrorLevel(ColumnError.ErrorLevel.FORCE.getValue());
                    map.put(0.00, columnError);
                    break;
                }
                map.put(cellValue, columnError);
                break;
            case PLAYER_PHONE:
                if (cellValue.length() >= 32) {
                    columnError.setErrorMsg("电话号码长度过长");
                    columnError.setErrorLevel(ColumnError.ErrorLevel.FORCE.getValue());
                    map.put(cellValue, columnError);
                    break;
                } else if (StringUtils.equalsIgnoreCase(cellValue, "")) {
                    columnError.setErrorMsg("电话号码为空");
                    columnError.setErrorLevel(ColumnError.ErrorLevel.PROMPT.getValue());
                    map.put(cellValue, columnError);
                    break;
                } else {
                    map.put(cellValue, columnError);
                    break;
                }
            case PLAYER_ANO:
                if (StringUtils.equalsIgnoreCase(cellValue, "")) {
                    columnError.setErrorMsg("参赛宣言不能为空");
                    columnError.setErrorLevel(ColumnError.ErrorLevel.FORCE.getValue());
                    map.put(0.00, columnError);
                    break;
                }
                map.put(cellValue, columnError);
                break;
            default:
                ExcelAnno.FieldType fieldType = field.getAnnotation(ExcelAnno.class).fieldType();
                switch (fieldType) {
                    case INTEGER:
                        Integer inte = 0;
                        try {
                            if (cellValue != null && !cellValue.equals("")) {
                                inte = Integer.parseInt(cellValue);
                            }
                        } catch (NumberFormatException e) {
                            columnError.setErrorMsg("数据类型错误");
                            columnError.setErrorLevel(ColumnError.ErrorLevel.FORCE.getValue());
                            map.put(inte, columnError);
                            break;
                        }
                        map.put(inte, columnError);
                        break;
                    case DOUBLE:
                        Double dou1 = 0D;
                        try {
                            if (cellValue != null && !cellValue.equals("")) {
                                dou1 = Double.parseDouble(cellValue);
                            }
                        } catch (NumberFormatException e) {
                            columnError.setErrorMsg("数据类型错误");
                            columnError.setErrorLevel(ColumnError.ErrorLevel.FORCE.getValue());
                            map.put(dou1, columnError);
                            break;
                        }
                        map.put(dou1, columnError);
                        break;
                    case DATE:
                        if (cellValue.matches("\\d{4}/\\d{1,2}/\\d{1,2}")) {
                            map.put(XzcmDateUtil.getUtilDate(cellValue, "yyyy/MM/dd"), columnError);
                            break;
                        } else if (cellValue.matches("\\d{4}-\\d{2}-\\d{2}")) {
                            map.put(XzcmDateUtil.getUtilDate(cellValue, "yyyy-MM-dd"), columnError);
                            break;
                        } else if (cellValue.matches("^\\d{4}\\d{2}\\d{2}")) {
                            map.put(XzcmDateUtil.getUtilDate(cellValue, "yyyyMMdd"), columnError);
                            break;
                        } else if (cellValue.matches("\\d{4}.\\d{1,2}.\\d{1,2}")) {
                            map.put(XzcmDateUtil.getUtilDate(cellValue, "yyyy.MM.dd"), columnError);
                            break;
                        } else if (cellValue == null || cellValue.equals("")) {
                            map.put(null, columnError);
                            break;
                        } else {
                            columnError.setErrorMsg("日期格式错误");
                            columnError.setErrorLevel(ColumnError.ErrorLevel.FORCE.getValue());
                            map.put(XzcmDateUtil.getUtilDate("1970-01-01", "yyyy-MM-dd"), columnError);
                            break;
                        }
                    default:
                        map.put(cellValue, columnError);
                        break;
                }
        }
        return map;
    }

    private String filterEmoji(String source, String slipStr) {
        if (StringUtils.isNotBlank(source)) {
            return source.replaceAll("[\\ud800\\udc00-\\udbff\\udfff\\ud800-\\udfff]", slipStr);
        } else {
            return source;
        }
    }

    private Boolean chineseCompare(String str1, String str2, String engCode) throws Exception {
        if (StringUtils.isNotBlank(str1) && StringUtils.isNotBlank(str2)) {
            String tmpStr1 = new String(str1.getBytes(engCode));
            String tmpStr2 = new String(str2.getBytes(engCode));
            if (tmpStr1.equals(tmpStr2)) {
                return true;
            }
        }
        return false;
    }

    private String getCellValue(Cell cell) {
        if (cell == null) {
            return "";
        }
        String cellValue = "";
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
                    cellValue = String.valueOf(XzcmDateUtil.fomratterDate(cell.getDateCellValue(), Constant.DATE_FORMAT));
                } else {
                    DecimalFormat df = new DecimalFormat("#.#########");
                    cellValue = df.format(cell.getNumericCellValue());
                }
                break;
            case Cell.CELL_TYPE_FORMULA:
                if (DateUtil.isCellDateFormatted(cell)) {
                    cellValue = String.valueOf(XzcmDateUtil.fomratterDate(cell.getDateCellValue(), Constant.DATE_FORMAT));
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

    public Object getObj(Class clazz, Cell cell) throws Exception {
        if (cell == null) {
            return null;
        }
        String cellValue = getCellValue(cell);
        Constructor con = clazz.getConstructor(String.class);
        return con.newInstance(cellValue);
    }
}
