package org.example;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;


public class Main {
    private static final String filePath = "/home/work/IdeaProjects/zadanie_ideaplatform/src/main/resources/tickets.json";
    public static void main(String[] args) {
        try {
            // Парсим JSON файл
            JSONParser parser = new JSONParser();
            FileReader reader = new FileReader(filePath);
            JSONObject jsonResponse = (JSONObject) parser.parse(reader);
            JSONArray flights = (JSONArray) jsonResponse.get("tickets");

            // Создаем отображение для хранения минимального времени полета для каждого авиаперевозчика
            Map<String, Duration> minFlightTimes = new HashMap<>();

            // Создаем список для хранения цен на билеты
            List<Double> ticketPrices = new ArrayList<>();

            for (Object obj : flights) {
                JSONObject flight = (JSONObject) obj;

                String carrier = (String) flight.get("carrier");
                String departureTime = (String) flight.get("departure_time");
                String arrivalTime = (String) flight.get("arrival_time");
                String origin_name = (String) flight.get("origin_name");
                String destination_name = (String) flight.get("destination_name");

                // Парсим время вылета и прилета
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("H:mm");
                LocalTime departure = LocalTime.parse(departureTime, formatter);
                LocalTime arrival = LocalTime.parse(arrivalTime, formatter);
                Duration duration = Duration.between(departure, arrival);

                // Проверяем, является ли текущий перелет минимальным для данного авиаперевозчика
                if (!minFlightTimes.containsKey(carrier) || duration.compareTo((minFlightTimes.get(carrier))) < 0) {
                    minFlightTimes.put(carrier, Duration.ofDays((int) duration.toMinutes()));
                }

                double price = Double.parseDouble(flight.get("price").toString());

                // Проверяем, является ли текущий перелет между Владивостоком и Тель-Авивом
                if (origin_name.equals("Владивосток") && destination_name.equals("Тель-Авив")) {
                    ticketPrices.add(price);
                }

                // Проверяем, является ли текущий перелет минимальным для данного авиаперевозчика
                if (!minFlightTimes.containsKey(carrier) || duration.compareTo(minFlightTimes.get(carrier)) < 0) {
                    minFlightTimes.put(carrier, duration);
                }
            }

            // Вычисляем разницу между средней ценой и медианой для полетов между Владивостоком и Тель-Авивом
            double averagePrice = ticketPrices.stream().mapToDouble(Double::doubleValue).average().orElse(0);
            Collections.sort(ticketPrices);
            double medianPrice;
            if (ticketPrices.size() % 2 != 0){
                medianPrice = ticketPrices.get((ticketPrices.size() - 1) / 2);
            }else {
                medianPrice = (ticketPrices.get(ticketPrices.size() / 2) + ticketPrices.get((ticketPrices.size() / 2) - 1)) / 2;
            }

            // Выводим результаты
            for (Map.Entry<String, Duration> entry : minFlightTimes.entrySet()) {
                long hours = entry.getValue().toHours();
                long minutes = entry.getValue().toMinutesPart();
                System.out.println("Минимальное время полета с авиаперевозчиком " + entry.getKey() + ": " + hours + " ч " + minutes + " мин");
            }

            // Запись в файл с результатами
            File file = new File("/home/work/IdeaProjects/zadanie_ideaplatform/src/main/java/bin/res.txt");
            FileWriter writer = new FileWriter(file);
            writer.write("Разница между средней ценой и медианой для полетов между Владивостоком и Тель-Авивом: " + (averagePrice - medianPrice) +"\nСредняя цена: " + averagePrice + "\nМедианная цена: " + medianPrice);
            writer.flush();


            //Вывод в консоль для проверки
            System.out.println("Разница между средней ценой и медианой для полетов между Владивостоком и Тель-Авивом: " + (averagePrice - medianPrice) +"\nСредняя цена: " + averagePrice + "\nМедианная цена: " + medianPrice);
            System.out.println(ticketPrices);
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

