package ru.fedorov.tz.controller;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.PriorityQueue;

@RestController
@RequestMapping("/api/numbers")
public class NumberController {

    @GetMapping("/max")
    public ResponseEntity<Integer> getNthMaxNumber(
            @RequestParam String filePath,
            @RequestParam int n) {
        try {
            FileInputStream fis = new FileInputStream(filePath);
            Workbook workbook = new XSSFWorkbook(fis);
            Sheet sheet = workbook.getSheetAt(0);
            PriorityQueue<Integer> maxHeap = new PriorityQueue<>();

            for (Row row : sheet) {
                for (Cell cell : row) {
                    if (cell.getCellType() == CellType.NUMERIC) {
                        maxHeap.add((int) cell.getNumericCellValue());
                        if (maxHeap.size() > n) {
                            maxHeap.poll();
                        }
                    }
                }
            }

            workbook.close();
            fis.close();

            if (maxHeap.size() < n) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }

            return ResponseEntity.ok(maxHeap.poll());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}