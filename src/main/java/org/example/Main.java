package org.example;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.*;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Main {

    private static List<Employee> staff;
    private static File fileJson;
    private static File fileCSV;

    public Main() {
    }

    private static File makeFiles(String string) {
        File myFile = new File(string);
// создадим новый файл
        try {
            if (myFile.createNewFile())
                System.out.println("Файл был создан");
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        return myFile;
    }

    public static void main(String[] args) {
        ////////////// Создаем файлы /////////////////////
        fileCSV = makeFiles("/home/oem/IdeaProjects/untitled1/data.csv");
        fileJson = makeFiles("/home/oem/IdeaProjects/untitled1/data.json");
        ////////////// Создаем запись ////////////////////
        List<Employee> listCSV = new ArrayList<>();
        listCSV.add(new Employee(1, "John", "Smith", "USA", 25));
        listCSV.add(new Employee(2, "Ivan", "Petrov", "Russia", 23));
        ColumnPositionMappingStrategy<Employee> strategy =
                new ColumnPositionMappingStrategy<>();
        strategy.setType(Employee.class);
        strategy.setColumnMapping("id", "firstName", "lastName", "country", "age");
        try (Writer writer = new FileWriter(fileCSV)) {
            StatefulBeanToCsv<Employee> sbc =
                    new StatefulBeanToCsvBuilder<Employee>(writer)
                            .withMappingStrategy(strategy)
                            .build();
            sbc.write(listCSV);
        } catch (IOException | CsvRequiredFieldEmptyException | CsvDataTypeMismatchException e) {
            e.printStackTrace();
        }
        ////////////////////////

        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        String fileName = "data.csv";
        List<Employee> list = parseCSV(columnMapping, fileName);
        Type listType = new TypeToken<List<Employee>>() {
        }.getType();
        String json = listToJson(list, listType);
        writeString(json);
    }

    private static List<Employee> parseCSV(String[] s, String n) {

        try (CSVReader csvReader = new CSVReader(new FileReader(n))) {
            ColumnPositionMappingStrategy<Employee> strategy =
                    new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping(s[0], s[1], s[2], s[3], s[4]);
            CsvToBean<Employee> csv = new CsvToBeanBuilder<Employee>(csvReader)
                    .withMappingStrategy(strategy)
                    .build();
            staff = csv.parse();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return staff;
    }

    ////////////////////////////////////////////
    private static String listToJson(Object src, Type typeOfSrc) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        //System.out.println(gson.toJson(cat));
        return gson.toJson(src, typeOfSrc);
    }

    ////////////////////////////////////////////
    private static void writeString(String json) {
        try (FileWriter file = new
                FileWriter(fileJson)) {
            file.write(json);
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}