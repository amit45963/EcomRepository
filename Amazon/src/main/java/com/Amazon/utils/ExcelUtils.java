package com.Amazon.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONObject;

public class ExcelUtils 
{
	
public static List<Map<String,String>> getTestDetails(String sheetname) throws IOException {
		
		List<Map<String,String>> list = null;
		FileInputStream fs = null;
		
		String absPath = new File("src\\main\\resources\\TestData\\TestData.xlsx").getAbsolutePath();
		
		
		try {
			fs = new FileInputStream(absPath);
			XSSFWorkbook workbook = new XSSFWorkbook(fs);
			XSSFSheet sheet = workbook.getSheet(sheetname);
			
			int lastrow = sheet.getLastRowNum();
			int lastcol = sheet.getRow(0).getLastCellNum();
			
			
			Map<String,String> map = null;
			list = new ArrayList<Map<String, String>>();
			
			for(int i=1; i<=lastrow;i++) {
				map = new HashMap<String, String>();
				for(int j=0;j<lastcol; j++) {
					String key = sheet.getRow(0).getCell(j).getStringCellValue();
					if( sheet.getRow(i).getCell(j) == null ||sheet.getRow(i).getCell(j).getCellType() == CellType.BLANK) {
						continue;
					}
					if(sheet.getRow(i).getCell(j).getCellType() == CellType.STRING) {
						String value = sheet.getRow(i).getCell(j).getStringCellValue();
						map.put(key, value);
					}
					else if( DateUtil.isCellDateFormatted(sheet.getRow(i).getCell(j))){
						SimpleDateFormat format = new SimpleDateFormat("ddmmmyyyy");
						String dateValue = format.format(sheet.getRow(i).getCell(j).getDateCellValue());
						map.put(key, dateValue);
					}
					else {
					
				}
				
			}
				list.add(map);
			}
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		finally {
			try {
				if(Objects.nonNull(fs)) {
					fs.close();
				}
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
		return list;
	}
	
	
	public  Map<String,String> getTestData(String sheetname, String scenario) throws Exception {

		FileInputStream fs = null;
		Map<String,String> map = null;
		
		String absPath = new File("src\\\\main\\\\resources\\\\TestData1.xlsx").getAbsolutePath();
		
			fs = new FileInputStream(absPath);
			XSSFWorkbook workbook = new XSSFWorkbook(fs);
			XSSFSheet sheet = workbook.getSheet(sheetname);
			

			XSSFCell cell ;

			int  row = findRow(sheet,scenario);

			if(row==-1) {
				System.out.println("No such test data available");
				workbook.close();
				return null;
			}
			
			
			int lastrow = sheet.getLastRowNum();
			int lastcol = sheet.getRow(0).getLastCellNum();
			
			map = new HashMap<String, String>();
				for(int j=1;j<lastcol; j++) {
				
					String key = sheet.getRow(0).getCell(j).getStringCellValue();
				
				if( sheet.getRow(row).getCell(j) == null ||sheet.getRow(row).getCell(j).getCellType() == CellType.BLANK) {
					continue;
				}
				if(sheet.getRow(row).getCell(j).getCellType() == CellType.STRING) {
					String value = sheet.getRow(row).getCell(j).getStringCellValue();
					map.put(key, value);
				}
				else if( DateUtil.isCellDateFormatted(sheet.getRow(row).getCell(j))){
					SimpleDateFormat format = new SimpleDateFormat("ddmmmyyyy");
					String dateValue = format.format(sheet.getRow(row).getCell(j).getDateCellValue());
					map.put(key, dateValue);
				}

				}
			workbook.close();
		return(map);		

	}

	
	public static int findRow(XSSFSheet sheet, String cellContent) {
	for (Row row : sheet) {
		for (Cell cell : row) {
			if (cell.getCellType() == CellType.STRING) {
				if (cell.getRichStringCellValue().getString().trim().equals(cellContent)) {
					return row.getRowNum();  
				}
			}
		}
	}               
	return -1;
}
	
	
	public JSONObject readRowField(String sheet, String field, String filepath ) throws Exception {

		String absPath = new File(filepath).getAbsolutePath();
		FileInputStream input = new FileInputStream(absPath);
		XSSFWorkbook workbook = new XSSFWorkbook(input);

		XSSFSheet ws=workbook.getSheet(sheet);

		int rowCount=ws.getPhysicalNumberOfRows();

		JSONObject obj = new JSONObject();
		for (int j = 0;j<rowCount;j++) {
			if(ws.getRow(j).getCell(0).getStringCellValue().equals(field) ) {
				if(ws.getRow(j).getCell(1).getCellType() == CellType.STRING) {
					obj.put(field,ws.getRow(j).getCell(1).getStringCellValue());
					break;
				}
				else {

					obj.put(field,ws.getRow(j).getCell(1).getNumericCellValue());

				}

			}
		}

		System.out.println(obj);

		workbook.close();

		return obj;

	}
	
	public void writeStringData(String sheet, String field, String value, String filepath ) throws Exception {

		String absPath = new File(filepath).getAbsolutePath();
		FileInputStream input = new FileInputStream(absPath);
		XSSFWorkbook workbook = new XSSFWorkbook(input);

		XSSFSheet ws=workbook.getSheet(sheet);

		int rownumber = findRow(ws, field);
		ws.getRow(rownumber).getCell(1).setCellValue(value);

		input.close();

		FileOutputStream outputStream = new FileOutputStream(absPath);
		workbook.write(outputStream);
		workbook.close();
		outputStream.close();

	}
	
	public JSONObject readCase(String sheet, String scenario, String filepath) throws IOException {
		String absPath = new File(filepath).getAbsolutePath();
		
		FileInputStream input = new FileInputStream(absPath);
		XSSFWorkbook workbook = new XSSFWorkbook(input);

		FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();	 
		evaluator.evaluateAll();

		XSSFSheet ws=workbook.getSheet(sheet);

		int  row = findRow(ws,scenario);

		if(row==-1) {
			System.out.println("No such test data available");
			workbook.close();
			return null;
		}
	
		int lastcol = ws.getRow(0).getLastCellNum();
		
		JSONObject obj = new JSONObject();
		
			for(int j=1;j<lastcol; j++) {
				String key = ws.getRow(0).getCell(j).getStringCellValue();
				if( ws.getRow(row).getCell(j) == null ||ws.getRow(row).getCell(j).getCellType() == CellType.BLANK) {
					continue;
				}
				
				if(ws.getRow(row).getCell(j).getCellType()== CellType.FORMULA){
					
					CellValue cellValue = evaluator.evaluate(ws.getRow(row).getCell(j));
					 
					if(cellValue.getCellType() == CellType.STRING ) {
					    	String value = ws.getRow(row).getCell(j).getStringCellValue();
							obj.put(key, value);
					}
					        else {
					    	Date javaDate= DateUtil.getJavaDate((double) cellValue.getNumberValue());
						    obj.put(key,new SimpleDateFormat("yyyy-MM-dd").format(javaDate));
					        
					}
					continue;
				}
				
				if(ws.getRow(row).getCell(j).getCellType() == CellType.STRING) {
					String value = ws.getRow(row).getCell(j).getStringCellValue();
					obj.put(key, value);
				}
				else if (ws.getRow(row).getCell(j).getCellType() == CellType.BOOLEAN) {
					obj.put(key,ws.getRow(row).getCell(j).getBooleanCellValue());
					continue;
				}
				else
				{
					if( DateUtil.isCellDateFormatted(ws.getRow(row).getCell(j))){
					SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
					String dateValue = format.format(ws.getRow(row).getCell(j).getDateCellValue());
					obj.put(key, dateValue);
					}
					else {
					obj.put(key, ws.getRow(row).getCell(j).getNumericCellValue());
				
					}
			
		}
			}
			
			workbook.close();
			return(obj);
	

	}
}
