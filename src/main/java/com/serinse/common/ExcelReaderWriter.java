package com.serinse.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.serinse.common.helpers.ExcelCell;

public class ExcelReaderWriter {

	public static List<List<ExcelCell>> readFile(String filename) throws IOException {
		FileInputStream inputStream = new FileInputStream(new File(filename));

		Workbook workbook = getWorkbook(inputStream, filename);
		Sheet firstSheet = workbook.getSheetAt(0);
		Iterator<Row> iterator = firstSheet.iterator();

		List<List<ExcelCell>> rows = new ArrayList<>();

		while (iterator.hasNext()) {
			Row nextRow = iterator.next();
			Iterator<Cell> cellIterator = nextRow.cellIterator();

			List<ExcelCell> cellList = new ArrayList<>();
			while (cellIterator.hasNext()) {
				Cell cell = cellIterator.next();
				ExcelCell ec = new ExcelCell();
				switch (cell.getCellType()) {
				case Cell.CELL_TYPE_STRING:
					ec.type = ExcelCell.STRING;
					ec.value = cell.getStringCellValue();
					break;
				case Cell.CELL_TYPE_BOOLEAN:
					ec.type = ExcelCell.BOOLEAN;
					ec.value = Boolean.toString(cell.getBooleanCellValue());
					break;
				case Cell.CELL_TYPE_NUMERIC:
					ec.type = ExcelCell.DOUBLE;
					ec.value = Double.toString(cell.getNumericCellValue());
					break;
				default:
					ec.value = cell.getStringCellValue();
				}
				cellList.add(ec);
			}
			rows.add(cellList);
		}

		workbook.close();
		inputStream.close();

		return rows;
	}

	private static Workbook getWorkbook(FileInputStream inputStream, String excelFilePath) throws IOException {
		Workbook workbook = null;

		if (excelFilePath.endsWith("xlsx")) {
			workbook = new XSSFWorkbook(inputStream);
		} else if (excelFilePath.endsWith("xls")) {
			workbook = new HSSFWorkbook(inputStream);
		} else {
			throw new IllegalArgumentException("The specified file is not Excel file");
		}

		return workbook;
	}

	public static boolean writeToFile(List<List<ExcelCell>> cells, String sheetName, String filename) {
		HSSFWorkbook workbook = new HSSFWorkbook();
		HSSFSheet sheet = workbook.createSheet(sheetName);
		for (int i = 0; i < cells.size(); i++) {
			Row row = sheet.createRow(i);
			List<ExcelCell> dataRow = cells.get(i);
			for (int j = 0; j < dataRow.size(); j++) {
				Cell cell = row.createCell(j);
				ExcelCell dataCell = dataRow.get(j);
				switch (dataCell.type) {
				case ExcelCell.BOOLEAN:
					try {
						cell.setCellValue(Boolean.parseBoolean(dataCell.value));
					} catch (Exception e) {
						cell.setCellValue(dataCell.value);
					}
					break;
				case ExcelCell.STRING:
					cell.setCellValue(dataCell.value);
					break;
				case ExcelCell.DATE:
					try {
						SimpleDateFormat d1 = new SimpleDateFormat(dataCell.dateFormat);
						cell.setCellValue(d1.parse(dataCell.value));
					} catch (Exception e) {
						cell.setCellValue(dataCell.value);
					}
					break;
				case ExcelCell.DOUBLE:
					try {
						cell.setCellValue(Double.parseDouble(dataCell.value));
					} catch (Exception e) {
						cell.setCellValue(dataCell.value);
					}
					break;
				default:
					cell.setCellValue(dataCell.value);
				}
			}
		}
		boolean ret = false;
		FileOutputStream fos = null;
		try {
			File file = FileHelpers.createIfNotExist(filename);
			fos = new FileOutputStream(file);
			workbook.write(fos);
			ret = true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			ret = false;
		} catch (IOException e) {
			e.printStackTrace();
			ret = false;
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					// Do nothing
				}
			}
			try {
				workbook.close();
			} catch (IOException e) {
				// Do nothing
			}
		}
		return ret;
	}

}
